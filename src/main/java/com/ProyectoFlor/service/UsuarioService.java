package com.ProyectoFlor.service;

import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // ⚠ Este método NO sirve en producción, pero lo dejo porque ya estaba
    public Usuario obtenerUsuarioActual() {
        return usuarioRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Usuario demo no encontrado"));
    }

    // Buscar usuario por correo (Spring Security depende de este)
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Buscar usuario por token de recuperación
    public Usuario buscarPorToken(String token) {
        return usuarioRepository.findByTokenRecuperacion(token)
                .orElse(null);
    }

    // Guardar cambios al usuario
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Login simple (ya casi no se usa gracias a Spring Security)
    public Usuario login(String correo, String contrasena) {
        return usuarioRepository.findByCorreoAndContrasena(correo, contrasena)
                .orElse(null);
    }

    // Registrar usuario
    public Usuario registrar(Usuario usuario) {

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        usuario.setRol("usuario"); // Rol predeterminado
        return usuarioRepository.save(usuario);
    }

    // Verificar si un correo ya existe
    public boolean usuarioExiste(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }
}