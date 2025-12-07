package com.ProyectoFlor.service;  

import com.ProyectoFlor.model.Ruta;
import com.ProyectoFlor.model.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ProyectoFlor.repository.UsuarioRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RutaService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositorio para acceder a los usuarios

    // Este método devuelve las rutas basadas en los roles del usuario
    public List<Ruta> getRutas() {
        // Obtenemos los roles de los usuarios desde la base de datos
        List<String> roles = usuarioRepository.findAllRoles();

        return roles.stream()
                .map(rol -> new Ruta("/" + rol + "/**", true, new Rol(rol))) // Generamos rutas basadas en los roles
                .collect(Collectors.toList());
    }
}
