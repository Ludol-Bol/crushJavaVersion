package com.crushVers.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/main-page")
    public String dashboard() {
        return "main-page";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/verify-code")
    public String verifyCode() {
        return "verify-code";
    }
}