package com.ProyectoFlor.service;

import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene el usuario actualmente logueado.
     * Por ahora, para pruebas, retorna el primer usuario de la base de datos.
     * Más adelante se puede reemplazar con Spring Security.
     */
    public Usuario obtenerUsuarioActual() {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(1L); // ejemplo: usuario con ID 1
        return usuarioOpt.orElseThrow(() -> new RuntimeException("Usuario demo no encontrado"));
    }

    /**
     * Busca un usuario por correo.
     */
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Guarda un nuevo usuario.
     */
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza datos de un usuario existente.
     */
    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}