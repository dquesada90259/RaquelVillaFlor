package com.villaflor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.villaflor.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
