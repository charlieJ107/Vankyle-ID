package com.vankyle.id.controllers;

import com.vankyle.id.models.login.ForgotPasswordRequest;
import com.vankyle.id.models.login.ForgotPasswordResponse;
import com.vankyle.id.models.login.ResetPasswordRequest;
import com.vankyle.id.models.login.ResetPasswordResponse;
import com.vankyle.id.service.email.EmailSender;
import com.vankyle.id.service.email.EmailTemplateService;
import com.vankyle.id.service.security.User;
import com.vankyle.id.service.security.UserManager;
import com.vankyle.id.service.validation.ValidationService;
import jakarta.mail.MessagingException;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Controller
public class LoginController {
    private final EmailSender emailSender;
    private final UserManager userManager;
    private final EmailTemplateService emailTemplateService;
    private final ValidationService validationService;

    public LoginController(
            EmailSender emailSender,
            UserManager userManager,
            EmailTemplateService emailTemplateService,
            ValidationService validationService) {
        this.emailSender = emailSender;
        this.userManager = userManager;
        this.emailTemplateService = emailTemplateService;
        this.validationService = validationService;
    }

    /**
     * Check if the reset password code is valid or not, if valid,
     *
     * @param code Reset password validation code
     * @return Reset password page
     * @see ResetPasswordResponse
     */
    @GetMapping("${vankyle.id.api-path}/reset-password")
    public @ResponseBody ResetPasswordResponse resetPassword(@RequestParam String code) {
        var verification = validationService.validateVerificationLinkCode(code, "reset-password");
        var response = new ResetPasswordResponse();
        if (verification.isValid()) {
            response.setStatus(200);
        } else {
            response.setStatus(401);
        }

        return response;
    }

    /**
     * Handle submit of the reset password
     *
     * @param resetPasswordRequest Reset password request
     * @return The forgot password response
     * @see ResetPasswordResponse
     */
    @PostMapping("${vankyle.id.api-path}/reset-password")
    public @ResponseBody ResetPasswordResponse resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        var code = resetPasswordRequest.getCode();
        var verification = validationService.validateVerificationLinkCode(code, "reset-password");
        var response = new ResetPasswordResponse();
        if (verification.isValid()) {
            userManager.resetPassword(verification.getUsername(), resetPasswordRequest.getPassword());
            response.setStatus(200);
        } else {
            response.setStatus(401);
        }
        return response;
    }

    /**
     * Handle the forgot password API, send reset password email
     *
     * @param forgotPasswordRequest Request body
     * @return Response body
     */
    @PostMapping("${vankyle.id.api-path}/forgot-password")
    public @ResponseBody ForgotPasswordResponse forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        User user;
        try {
            if (forgotPasswordRequest.getUsername() != null) {
                user = userManager.findByUsername(forgotPasswordRequest.getUsername());
            } else {
                user = userManager.findByEmail(forgotPasswordRequest.getEmail());
            }
        } catch (Exception e) {
            var forgotPasswordResponse = new ForgotPasswordResponse();
            forgotPasswordResponse.setStatus(404);
            return forgotPasswordResponse;
        }
        // User found
        var locale = new Locale("en", "US");

        if (forgotPasswordRequest.getLocale() != null) {
            var split = forgotPasswordRequest.getLocale().split("-");
            if (split.length == 2) {
                locale = new Locale(split[0], split[1]);
            } else if (split.length == 1) {
                locale = new Locale(split[0]);
            }
        }

        // Send email
        try {
            sendResetPasswordEmail(user, locale);
        } catch (Exception e) {
            // Failed to send email
            var forgotPasswordResponse = new ForgotPasswordResponse();
            forgotPasswordResponse.setStatus(500);
            return forgotPasswordResponse;
        }
        // Email sent
        var forgotPasswordResponse = new ForgotPasswordResponse();
        forgotPasswordResponse.setStatus(200);
        return forgotPasswordResponse;
    }

    private void sendResetPasswordEmail(User user, Locale locale) throws MessagingException {
        var code = validationService.generateVerificationLinkCode(user, "reset-password");
        var emailContent = emailTemplateService.newResetPasswordEmail(user, code, locale);
        emailSender.sendMail(user.getEmail(), emailContent.getSubject(), emailContent.getBody());
    }

    @Data
    public static class LoginModel {
        private String username;
        private String password;
    }

}
