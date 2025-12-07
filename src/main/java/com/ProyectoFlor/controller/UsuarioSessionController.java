package com.ProyectoFlor.controller;

import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice   // <- ANTES tenías @Controller
public class UsuarioSessionController {

    private final UsuarioService usuarioService;

    public UsuarioSessionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("usuarioLogeado")
    public Usuario getUsuarioLogeado(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            // Obtiene el Usuario real desde la base de datos
            return usuarioService.buscarPorCorreo(authentication.getName());
        }
        return null;
    }
}