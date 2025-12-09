package com.ProyectoFlor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ProyectoFlor.model.Producto;
import com.ProyectoFlor.model.Categoria;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoria(Categoria categoria);

    List<Producto> findByNombreContainingIgnoreCaseAndCategoria(String nombre, Categoria categoria);
}