package com.ProyectoFlor.service;

import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario obtenerUsuarioActual() {
        return usuarioRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Usuario demo no encontrado"));
    }

    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario login(String correo, String contrasena) {
        return usuarioRepository.findByCorreoAndContrasena(correo, contrasena)
                .orElse(null);
    }

    public Usuario registrar(Usuario usuario) {

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        usuario.setRol("usuario");
        return usuarioRepository.save(usuario);
    }
    
    public boolean usuarioExiste(String correo) {
    return usuarioRepository.existsByCorreo(correo);
}
}