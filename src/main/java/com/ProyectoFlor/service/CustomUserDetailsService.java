package com.ProyectoFlor.service;

import com.ProyectoFlor.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.buscarPorCorreo(correo);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        // Creamos la autoridad con ROLE_ prefijo si usamos hasRole()
        SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority("ROLE_" + usuario.getRol());

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getContrasena()) // NoOpPasswordEncoder acepta texto plano
                .authorities(Collections.singleton(autoridad))
                .build();
    }
}