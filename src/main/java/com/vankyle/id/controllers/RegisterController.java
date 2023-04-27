package com.vankyle.id.controllers;

import com.vankyle.id.models.register.RegisterRequest;
import com.vankyle.id.models.register.RegisterResponse;
import com.vankyle.id.service.email.EmailContent;
import com.vankyle.id.service.email.EmailSender;
import com.vankyle.id.service.email.EmailTemplateService;
import com.vankyle.id.models.register.ConfirmEmailResponse;
import com.vankyle.id.service.security.User;
import com.vankyle.id.service.security.UserManager;
import com.vankyle.id.service.security.UsernameAlreadyExistsException;
import com.vankyle.id.service.validation.ValidationService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.Locale;
import java.util.Random;

@Controller
public class RegisterController {

    private final EmailSender emailSender;
    private final ValidationService validationService;
    private final EmailTemplateService emailTemplateService;
    private final UserManager userManager;

    public RegisterController(
            EmailSender emailSender,
            ValidationService validationService,
            EmailTemplateService emailTemplateService,
            UserManager userManager) {
        this.emailSender = emailSender;
        this.validationService = validationService;
        this.emailTemplateService = emailTemplateService;
        this.userManager = userManager;
    }

    @PostMapping("/api/register")
    public @ResponseBody RegisterResponse register(@RequestBody RegisterRequest request) {
        // Check if email exists
        if (userManager.existsByEmail(request.getEmail())) {
            var response = new RegisterResponse();
            response.setStatus(401);
            return response;
        }
        // Pick string before @ as username
        String username = request.getEmail().split("@")[0];
        // If username exists, append random string
        Random random = new Random();
        while (userManager.userExists(username)) {
            int i = random.nextInt();
            username += Integer.toString(i);
        }
        var user = User.withUsername(username)
                .email(request.getEmail())
                .rawPassword(request.getPassword())
                .roles("USER")
                .withGeneratedVerificationSecret()
                .emailVerified(false)
                .accountLocked(true)
                .build();
        try {
            userManager.createUser(user);
        } catch (UsernameAlreadyExistsException e) {
            var response = new RegisterResponse();
            // It should not happen, because we have checked if username exists before
            response.setStatus(500);
            return response;
        }

        // Send activate email
        var code = validationService.generateVerificationLinkCode(user, "confirm-email");
        var localeStrings = request.getLocale().split("-");
        Locale userLocale = new Locale(localeStrings[0], localeStrings[1]);
        try {
            sendActivateEmail(user, code, userLocale);
        } catch (MessagingException e) {
            var response = new RegisterResponse();
            response.setStatus(500);
            return response;
        }
        var response = new RegisterResponse();
        response.setStatus(200);
        return response;
    }

    @GetMapping("/api/confirm-email")
    public @ResponseBody ConfirmEmailResponse confirmEmail(@RequestParam(required = true) String code) {
        var confirmEmailResponse = new ConfirmEmailResponse();
        var verification = validationService.validateVerificationLinkCode(code, "confirm-email");
        if (verification.isValid()) {
            if (userManager.findByUsername(verification.getUsername()).isEmailVerified()) {
                confirmEmailResponse.setStatus(401);
                confirmEmailResponse.setSuccess(false);
                return confirmEmailResponse;
            }
            userManager.confirmEmail(verification.getUsername());
            userManager.updateSecurityStamp(verification.getUsername());
            confirmEmailResponse.setStatus(200);
            confirmEmailResponse.setSuccess(true);
        } else {
            confirmEmailResponse.setStatus(401);
            confirmEmailResponse.setSuccess(false);
        }
        return confirmEmailResponse;
    }

    private void sendActivateEmail(User user, String verificationCode, Locale locale) throws MessagingException {
        EmailContent content = emailTemplateService.newActivateEmail(user, verificationCode, locale);
        emailSender.sendMail(user.getEmail(), content.getSubject(), content.getBody());
    }
}
