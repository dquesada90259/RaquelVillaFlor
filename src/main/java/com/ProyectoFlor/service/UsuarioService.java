package com.ProyectoFlor.service;

import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene al usuario actualmente logueado usando Spring Security.
     */
    public Usuario obtenerUsuarioActual() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            String correo = userDetails.getUsername(); // Spring usa el correo como username

            return usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + correo));
        }

        return null;
    }

    // Buscar usuario por correo
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Buscar usuario por token de recuperación
    public Usuario buscarPorToken(String token) {
        return usuarioRepository.findByTokenRecuperacion(token)
                .orElse(null);
    }

    // Guardar usuario
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Login simple (solo si no usas Spring Security)
    public Usuario login(String correo, String contrasena) {
        return usuarioRepository.findByCorreoAndContrasena(correo, contrasena)
                .orElse(null);
    }

    // Registrar usuario
    public Usuario registrar(Usuario usuario) {

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        usuario.setRol("usuario");
        return usuarioRepository.save(usuario);
    }

    // Verificar si existe correo
    public boolean usuarioExiste(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }
}