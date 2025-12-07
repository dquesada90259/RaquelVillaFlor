package com.ProyectoFlor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/acceso_denegado")
    public String accesoDenegado() {
        return "acceso_denegado"; // nombre del HTML
    }
}