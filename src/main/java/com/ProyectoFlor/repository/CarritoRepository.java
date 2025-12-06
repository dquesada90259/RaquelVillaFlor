package com.ProyectoFlor.repository;

import com.ProyectoFlor.model.Carrito;
import com.ProyectoFlor.model.CarritoDetalle;
import com.ProyectoFlor.model.Producto;
import com.ProyectoFlor.model.Usuario;     
import com.ProyectoFlor.model.Carrito.EstadoCarrito; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuarioAndEstado(Usuario usuario, EstadoCarrito estado);
}