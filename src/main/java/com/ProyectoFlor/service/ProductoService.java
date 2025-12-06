package com.ProyectoFlor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ProyectoFlor.model.Producto;
import com.ProyectoFlor.repository.ProductoRepository;
import com.ProyectoFlor.model.Categoria;
import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public void guardar(Producto producto) {
        productoRepository.save(producto);
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }
    
    public List<Producto> buscarPorCategoria(Categoria categoria) {
    return productoRepository.findByCategoria(categoria);
    }

    public List<Producto> buscarPorNombreYCategoria(String nombre, Categoria categoria) {
    return productoRepository.findByNombreContainingIgnoreCaseAndCategoria(nombre, categoria);
    }
    
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
