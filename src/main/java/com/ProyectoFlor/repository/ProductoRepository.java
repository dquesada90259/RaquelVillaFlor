package com.ProyectoFlor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ProyectoFlor.model.Producto;
import com.ProyectoFlor.model.Categoria;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos cuyo nombre contenga el texto buscado (ignorando mayúsculas/minúsculas)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar productos por categoría
    List<Producto> findByCategoria(Categoria categoria);

    // Buscar productos por nombre y categoría
    List<Producto> findByNombreContainingIgnoreCaseAndCategoria(String nombre, Categoria categoria);
}