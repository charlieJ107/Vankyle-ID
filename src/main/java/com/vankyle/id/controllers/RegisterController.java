package com.vankyle.id.controllers;

import com.vankyle.id.services.verification.VerificationSender;
import lombok.Data;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RegisterController {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsManager userDetailsManager;
    private final VerificationSender verificationSender;

    public RegisterController(
            PasswordEncoder passwordEncoder,
            UserDetailsManager userDetailsManager,
            VerificationSender verificationSender
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsManager = userDetailsManager;
        this.verificationSender = verificationSender;
    }

    @PostMapping("/api/register")
    public @ResponseBody RegisterResponse register(@RequestBody RegisterRequest registerRequest, @RequestParam String locale) {
        UserDetails user = User.withUsername(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .passwordEncoder(this.passwordEncoder::encode)
                .roles("USER")
                .build();
        try {
            this.userDetailsManager.createUser(user);
            this.verificationSender.sendVerification(registerRequest.getEmail(), locale);
            return new RegisterResponse(200);
        } catch (IllegalArgumentException e) {
            return new RegisterResponse(400);
        }
    }

    @Data
    public static class RegisterRequest {
        private String password;
        private String email;
        private String locale;
    }

    @Data
    public static class RegisterResponse {
        public RegisterResponse(int status) {
            this.status = status;
        }

        private int status;
    }
}
