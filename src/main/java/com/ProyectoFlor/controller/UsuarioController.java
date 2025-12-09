package com.ProyectoFlor.controller;

import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ---------- REGISTRO ----------
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, Model model, HttpSession session) {

        if (usuarioService.usuarioExiste(usuario.getCorreo())) {
            model.addAttribute("error", "El correo ya está registrado");
            return "register";
        }

        usuarioService.registrar(usuario);

        session.setAttribute("mensaje", "Registro exitoso, ya puedes iniciar sesión");
        return "redirect:/usuario/login";
    }

    // ---------- LOGIN ----------
    @GetMapping("/login")
    public String loginPage(HttpSession session,
                            Model model,
                            @RequestParam(value = "expired", required = false) String expired) {

        if (expired != null) {
            model.addAttribute("sessionExpired", "Tu sesión expiró por inactividad.");
        }

        return "login";
    }

    // ---------- Cerrar sesión ----------
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuario/login";
    }

    // ---------- RECUPERAR CONTRASEÑA ----------
    @GetMapping("/recuperar")
    public String mostrarRecuperar() {
        return "recuperar";
    }

    @PostMapping("/recuperar")
    public String procesarRecuperar(@RequestParam String correo, Model model) {

        Usuario usuario;

        try {
            usuario = usuarioService.buscarPorCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        } catch (Exception e) {
            model.addAttribute("error", "El correo no está registrado");
            return "recuperar";
        }

        // Generar token
        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);
        usuarioService.guardar(usuario);

        model.addAttribute("mensaje",
                "Se envió un enlace a tu correo. Link temporal: "
                        + "http://localhost:50/usuario/restablecer?token=" + token);

        return "recuperar";
    }

    // ---------- FORMULARIO DE RESTABLECER ----------
    @GetMapping("/restablecer")
    public String mostrarRestablecer(@RequestParam String token, Model model) {

        Usuario usuario = usuarioService.buscarPorToken(token)
                .orElse(null);

        if (usuario == null) {
            model.addAttribute("error", "El enlace de recuperación no es válido.");
            return "restablecer";
        }

        model.addAttribute("token", token);
        return "restablecer";
    }

    // ---------- PROCESAR NUEVA CONTRASEÑA ----------
    @PostMapping("/restablecer")
    public String procesarRestablecer(@RequestParam String token,
                                      @RequestParam String contrasena,
                                      Model model) {

        Usuario usuario = usuarioService.buscarPorToken(token)
                .orElse(null);

        if (usuario == null) {
            model.addAttribute("error", "El enlace no es válido.");
            return "restablecer";
        }

        usuarioService.cambiarContrasena(usuario.getId(), contrasena);
        usuarioService.limpiarTokenRecuperacion(usuario.getId());

        model.addAttribute("mensaje", "Tu contraseña fue actualizada con éxito.");
        return "login";
    }
}