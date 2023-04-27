package com.vankyle.id.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    // Create routes for: /register, /forgot-password, /reset-password, /confirm-email,
    // /oidc, /404 and /account/**, they all return index
    @RequestMapping("/register")
    public String register() {
        return "index";
    }

    @RequestMapping("/forgot-password")
    public String forgotPassword() {
        return "index";
    }

    @RequestMapping("/reset-password")
    public String resetPassword() {
        return "index";
    }

    @RequestMapping("/confirm-email")
    public String confirmEmail() {
        return "index";
    }

    @RequestMapping("/oidc")
    public String oidc() {
        return "index";
    }

    @RequestMapping("/404")
    public String notFound() {
        return "index";
    }

    @RequestMapping("/account/**")
    public String account() {
        return "index";
    }

    @RequestMapping("/logout")
    public String logout() {
        return "index";
    }


}
