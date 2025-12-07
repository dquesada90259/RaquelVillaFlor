package com.ProyectoFlor.service;

import com.ProyectoFlor.model.Rol;
import com.ProyectoFlor.model.Ruta;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RutaService {

    public List<Ruta> getRutas() {

        List<Ruta> rutas = new ArrayList<>();

        // Rutas públicas
        rutas.add(new Ruta("/usuario/login", false, null));
        rutas.add(new Ruta("/usuario/registro", false, null));
        rutas.add(new Ruta("/css/**", false, null));
        rutas.add(new Ruta("/img/**", false, null));

        // Rutas protegidas
        rutas.add(new Ruta("/admin/**", true, new Rol("admin")));
        rutas.add(new Ruta("/catalogo/**", true, new Rol("usuario")));
        rutas.add(new Ruta("/carrito/**", true, new Rol("usuario")));

        return rutas;
    }
}