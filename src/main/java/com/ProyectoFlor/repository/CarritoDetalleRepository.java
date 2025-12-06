package com.ProyectoFlor.repository;

import com.ProyectoFlor.model.CarritoDetalle;
import com.ProyectoFlor.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarritoDetalleRepository extends JpaRepository<CarritoDetalle, Long> {
    List<CarritoDetalle> findByCarrito(Carrito carrito);
}
