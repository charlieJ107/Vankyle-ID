package com.vankyle.id.controllers;

import com.vankyle.id.models.account.email.SendEmailVerificationCodeRequest;
import com.vankyle.id.models.account.email.SendEmailVerificationCodeResponse;
import com.vankyle.id.models.account.email.UpdateEmailRequest;
import com.vankyle.id.models.account.email.UpdateEmailResponse;
import com.vankyle.id.models.account.name.ChangeInfoRequest;
import com.vankyle.id.models.account.name.ChangeInfoResponse;
import com.vankyle.id.models.account.password.ChangePasswordRequest;
import com.vankyle.id.models.account.password.ChangePasswordResponse;
import com.vankyle.id.models.account.phone.SendPhoneVerificationRequest;
import com.vankyle.id.models.account.phone.SendPhoneVerificationResponse;
import com.vankyle.id.models.account.phone.UpdatePhoneRequest;
import com.vankyle.id.models.account.phone.UpdatePhoneResponse;
import com.vankyle.id.service.email.EmailSender;
import com.vankyle.id.service.email.EmailTemplateService;
import com.vankyle.id.service.security.User;
import com.vankyle.id.service.security.UserManager;
import com.vankyle.id.service.validation.ValidationService;
import jakarta.mail.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;

@RestController
@RequestMapping("${vankyle.id.api-path}/account")
public class AccountController {
    private final UserManager userManager;
    private final ValidationService validationService;
    private final EmailTemplateService emailTemplateService;
    private final EmailSender emailSender;

    private static final Log logger = LogFactory.getLog(AccountController.class);

    public AccountController(UserManager userManager, ValidationService validationService, EmailTemplateService emailTemplateService, EmailSender emailSender) {
        this.userManager = userManager;
        this.validationService = validationService;
        this.emailTemplateService = emailTemplateService;
        this.emailSender = emailSender;
    }

    @PostMapping("/email")
    public SendEmailVerificationCodeResponse sendEmailVerificationCode(
            Principal principle,
            @RequestBody SendEmailVerificationCodeRequest email) {
        var user = userManager.findByUsername(principle.getName());
        if (user != null) {
            try {
                sendEmailVerificationCode(user, email.getEmail(), Locale.forLanguageTag(email.getLocale()));
            } catch (MessagingException e) {
                var response = new SendEmailVerificationCodeResponse();
                response.setStatus(500);
                return response;
            }
        } else {
            var response = new SendEmailVerificationCodeResponse();
            response.setStatus(404);
            return response;

        }
        var response = new SendEmailVerificationCodeResponse();
        response.setStatus(200);
        return response;
    }

    @PutMapping("/email")
    public UpdateEmailResponse updateEmail(Principal principle, @RequestBody UpdateEmailRequest request) {
        var user = userManager.findByUsername(principle.getName());
        if (user != null) {
            var code = validationService.validateEmailConfirmationCode(request.getCode(), user);
            if (code.isValid()) {
                user.setEmail(request.getEmail());
                userManager.updateUser(user);
            } else {
                var response = new UpdateEmailResponse();
                response.setStatus(400);
                return response;
            }
        } else {
            var response = new UpdateEmailResponse();
            response.setStatus(404);
            return response;
        }
        var response = new UpdateEmailResponse();
        response.setStatus(200);
        return response;
    }

    @PutMapping("/password")
    public ChangePasswordResponse changePassword(Principal principal, @RequestBody ChangePasswordRequest request) {
        try {
            userManager.changePassword(request.getCurrentPassword(), request.getNewPassword());
        } catch (AccessDeniedException e) {
            var response = new ChangePasswordResponse();
            response.setStatus(403);
            return response;
        }
        var response = new ChangePasswordResponse();
        response.setStatus(200);
        return response;
    }

    @PutMapping("/info")
    public ChangeInfoResponse updateInfo(Principal principal, @RequestBody ChangeInfoRequest request) {
        var user = userManager.findByUsername(principal.getName());
        if (user != null) {
            user.setName(request.getName());
            userManager.updateUser(user);
        } else {
            var response = new ChangeInfoResponse();
            response.setStatus(404);
            return response;
        }
        var response = new ChangeInfoResponse();
        response.setStatus(200);
        return response;
    }

    @PostMapping("/phone")
    public SendPhoneVerificationResponse sendPhoneVerificationCode(Principal principal, @RequestBody SendPhoneVerificationRequest request) {
        var user = userManager.findByUsername(principal.getName());
        if (user != null) {
            sendPhoneVerificationCode(user, request.getPhone(), Locale.forLanguageTag(request.getLocale()));
        }
        var response = new SendPhoneVerificationResponse();
        response.setStatus(200);
        return response;
    }

    @PutMapping("/phone")
    public UpdatePhoneResponse updatePhone(Principal principal, @RequestBody UpdatePhoneRequest updatePhoneRequest){
        var user = userManager.findByUsername(principal.getName());
        if (user != null) {
            var code = validationService.validatePhoneConfirmationCode(updatePhoneRequest.getCode(), user);
            if (code.isValid()) {
                user.setPhone(updatePhoneRequest.getPhone());
                userManager.updateUser(user);
            } else {
                var response = new UpdatePhoneResponse();
                response.setStatus(400);
                return response;
            }
        } else {
            var response = new UpdatePhoneResponse();
            response.setStatus(404);
            return response;
        }
        var response = new UpdatePhoneResponse();
        response.setStatus(200);
        return response;
    }
    private void sendPhoneVerificationCode(User user, String phone, Locale locale) {
            logger.warn("send phone verification code to " + phone + " with locale " + locale);
            logger.warn("SMS service is not implemented yet so this is just a stub, no code is sent.");
            logger.warn("Please check the console for the verification code.");
            // TODO: implement SMS service
    }
    private void sendEmailVerificationCode(User user, String email, Locale locale) throws MessagingException {
        var code = validationService.generateEmailConfirmationCode(user);
        var content = emailTemplateService.newConfirmEmail(user, code, locale);
        emailSender.sendMail(email, content.getSubject(), content.getBody());
    }

}
