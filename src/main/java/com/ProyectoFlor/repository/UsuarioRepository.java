package com.ProyectoFlor.repository;

import com.ProyectoFlor.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByCorreoAndContrasena(String correo, String contrasena);

    boolean existsByCorreo(String correo);
    
    Optional<Usuario> findByTokenRecuperacion(String token);
}