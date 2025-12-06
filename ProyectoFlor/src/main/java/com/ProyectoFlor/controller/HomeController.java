package com.ProyectoFlor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author kathe
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        System.out.println("HomeController: /login solicitado"); // para verificar que se detecta
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

}
