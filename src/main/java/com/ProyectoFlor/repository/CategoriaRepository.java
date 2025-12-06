package com.ProyectoFlor.repository;

import com.ProyectoFlor.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    //Aqui se pueden agregar busquedas personalizadas
}