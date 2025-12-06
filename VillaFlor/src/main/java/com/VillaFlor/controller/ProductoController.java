package com.villaflor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.villaflor.service.ProductoService;

@Controller
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/catalogo")
    public String verCatalogo(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "catalogo";
    }
}
