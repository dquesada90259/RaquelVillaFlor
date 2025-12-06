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
        return "registro";
    }

    // Procesar registro
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, Model model, HttpSession session) {

    if (usuarioService.usuarioExiste(usuario.getCorreo())) {
        model.addAttribute("error", "El correo ya está registrado");
        return "registro";
    }

    usuarioService.registrar(usuario);

    session.setAttribute("mensaje", "Registro exitoso, ya puedes iniciar sesión");
    return "redirect:/usuario/login";
    }

    // Mostrar formulario de login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(HttpSession session) {
    // Obtenemos el usuario desde Spring Security
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String correo = auth.getName();
    Usuario usuario = usuarioService.buscarPorCorreo(correo);

    session.setAttribute("usuarioLogeado", usuario);
    return "redirect:/catalogo"; // redirige a catálogo después del login
}

    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuario/login";
    }
    
    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        String correo = authentication.getName(); // obtiene el correo del usuario logueado
        Usuario usuario = usuarioService.buscarPorCorreo(correo);
        model.addAttribute("usuario", usuario); // enviamos el usuario completo a la vista
    return "home"; // tu página principal
    }
}
