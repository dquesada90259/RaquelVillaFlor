package com.ProyectoFlor.repository;

import com.ProyectoFlor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByCorreo(String correo);

    Optional<Usuario> findByCorreoAndContrasena(String correo, String contrasena);

    Optional<Usuario> findByCorreo(String correo);
}
