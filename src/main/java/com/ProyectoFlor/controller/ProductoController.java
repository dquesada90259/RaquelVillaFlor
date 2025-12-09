package com.ProyectoFlor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable; // ← agregado
import com.ProyectoFlor.service.ProductoService;
import com.ProyectoFlor.service.CategoriaService;
import com.ProyectoFlor.model.Producto;
import com.ProyectoFlor.model.Categoria;

import java.util.List;

@Controller
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping({"/catalogo", "/productos"})
    public String verCatalogo(
            @RequestParam(required = false) String ch,        // búsqueda por nombre
            @RequestParam(required = false) Long categoriaId, // filtro por categoría
            Model model) {

        List<Producto> productos;

        // Obtener categoría si se seleccionó
        Categoria categoria = (categoriaId != null) ? categoriaService.obtenerPorId(categoriaId) : null;

        if (ch != null && !ch.isEmpty() && categoria != null) {
            // Filtrar por nombre y categoría
            productos = productoService.buscarPorNombreYCategoria(ch, categoria);
        } else if (ch != null && !ch.isEmpty()) {
            // Filtrar solo por nombre
            productos = productoService.buscarPorNombre(ch);
        } else if (categoria != null) {
            // Filtrar solo por categoría
            productos = productoService.buscarPorCategoria(categoria);
        } else {
            // Sin filtro
            productos = productoService.listarTodos();
        }

        // Enviar datos a la vista
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("busqueda", ch != null ? ch : "");
        model.addAttribute("categoriaSeleccionada", categoriaId);

        System.out.println("Lista de categorías: " + model.getAttribute("categorias"));

        return "catalogo"; // Thymeleaf: catalogo.html
    }

    // ⭐⭐⭐ AGREGADO — SIN MODIFICAR NADA DEL CÓDIGO EXISTENTE ⭐⭐⭐
    @GetMapping("/producto/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {

        Producto producto = productoService.obtenerPorId(id);

        if (producto == null) {
            return "redirect:/catalogo"; // Si el ID no existe
        }

        model.addAttribute("producto", producto);
        return "producto-detalle"; // Vista de detalles
    }
}