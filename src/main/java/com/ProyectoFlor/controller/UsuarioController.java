package com.ProyectoFlor.controller;

import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;


@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro"; // nombre del archivo .html
    }

    // Procesar registro
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, Model model) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            model.addAttribute("error", "El correo ya está registrado");
            return "registro";
        }

        usuario.setRol("usuario");
        usuarioRepository.save(usuario);
        model.addAttribute("mensaje", "Registro exitoso. Ya puedes iniciar sesión.");
        return "redirect:/usuario/login";
    }

    // Mostrar formulario de login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
public String procesarLogin(@RequestParam String correo,
                            @RequestParam String contrasena,
                            Model model) {

    Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoAndContrasena(correo, contrasena);

    if (usuarioOpt.isPresent()) {
        Usuario usuario = usuarioOpt.get();
        model.addAttribute("usuario", usuario);
        return "redirect:/catalogo"; // o a donde quieras redirigirlo después
    } else {
        model.addAttribute("error", "Correo o contraseña incorrectos");
        return "login";
    }
}
}
