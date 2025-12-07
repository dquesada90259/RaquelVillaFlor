package com.ProyectoFlor.controller;

import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register"; // Tu archivo correcto
    }

    // Procesar registro
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, Model model, HttpSession session) {

        // Validar si el correo ya existe
        if (usuarioService.usuarioExiste(usuario.getCorreo())) {
            model.addAttribute("error", "El correo ya está registrado");
            return "register";
        }

        // Validar contraseña vacía
        if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
            model.addAttribute("error", "Debes ingresar una contraseña");
            return "register";
        }

        // Registrar usuario correctamente
        usuarioService.registrar(usuario);

        session.setAttribute("mensaje", "Registro exitoso, ya puedes iniciar sesión");
        return "redirect:/usuario/login";
    }

    // Mostrar formulario de login
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        return "login";
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuario/login?logout=true";
    }

    // Home opcional mostrando usuario autenticado
    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null) {
            String correo = authentication.getName();
            Usuario usuario = usuarioService.buscarPorCorreo(correo);
            model.addAttribute("usuario", usuario);
        }
        return "home";
    }
}
