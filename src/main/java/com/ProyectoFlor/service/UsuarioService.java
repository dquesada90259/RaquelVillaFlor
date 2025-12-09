package com.ProyectoFlor.service;

import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retorna el usuario actualmente autenticado mediante Spring Security.
     */
    public Usuario obtenerUsuarioActual() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {

            // Buscar el usuario REAL en base al correo del UserDetails
            return usuarioRepository.findByCorreo(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }

        if (principal instanceof Usuario usuario) {
            return usuario;
        }

        return null;
    }

    /**
     * Busca un usuario por correo.
     */
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    /**
     * Registrar un nuevo usuario (encripta contraseña).
     */
    public Usuario registrar(Usuario usuario) {

        // 🔥🔥 AGREGADO — Asignar rol USER por defecto si viene vacío o null
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("USER");
        }

        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Guarda cambios del usuario.
     */
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Obtiene un usuario por ID.
     */
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Validar inicio de sesión manual.
     */
    public boolean validarCredenciales(String correo, String contrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isEmpty()) return false;

        return passwordEncoder.matches(contrasena, usuarioOpt.get().getContrasena());
    }

    /**
     * Actualizar datos personales del usuario.
     */
    public Usuario actualizarDatos(Long id, Usuario cambios) {
        Usuario usuario = obtenerPorId(id);

        usuario.setNombre(cambios.getNombre());
        usuario.setTelefono(cambios.getTelefono());
        usuario.setDireccion(cambios.getDireccion());

        return usuarioRepository.save(usuario);
    }

    /**
     * Cambiar contraseña del usuario.
     */
    public void cambiarContrasena(Long id, String nuevaContrasena) {
        Usuario usuario = obtenerPorId(id);
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
    }

    /**
     * Buscar usuario por token de recuperación.
     */
    public Optional<Usuario> buscarPorToken(String token) {
        return usuarioRepository.findByTokenRecuperacion(token);
    }

    /**
     * Guardar token de recuperación.
     */
    public void asignarTokenRecuperacion(Long id, String token) {
        Usuario usuario = obtenerPorId(id);
        usuario.setTokenRecuperacion(token);
        usuarioRepository.save(usuario);
    }

    /**
     * Eliminar token luego de usarlo.
     */
    public void limpiarTokenRecuperacion(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setTokenRecuperacion(null);
        usuarioRepository.save(usuario);
    }

    // ---------------------------------------------------------
    // 🔥 MÉTODOS NECESARIOS PARA CORREGIR TUS ERRORES DE COMPILACIÓN
    // ---------------------------------------------------------

    /**
     * Usado por UsuarioController (evitaba compilar).
     */
    public boolean usuarioExiste(String correo) {
        return usuarioRepository.findByCorreo(correo).isPresent();
    }

    /**
     * Algunos controladores necesitan recibir Usuario y no Optional.
     */
    public Usuario obtenerPorCorreoObligatorio(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}