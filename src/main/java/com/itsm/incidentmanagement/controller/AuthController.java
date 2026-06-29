package com.itsm.incidentmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        System.out.println("=== Página de login acessada ===");
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        System.out.println("=== Logout ===");
        return "redirect:/login?logout";
    }
}