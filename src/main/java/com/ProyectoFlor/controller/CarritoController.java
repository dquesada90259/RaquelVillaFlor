package com.ProyectoFlor.controller;

import com.ProyectoFlor.model.Carrito;
import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.service.CarritoService;
import com.ProyectoFlor.service.UsuarioService; 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;

    @GetMapping
public String verCarrito(Model model) {
    Usuario usuario = usuarioService.obtenerUsuarioActual();
    if (usuario == null) {
        return "redirect:/login";  
    }
    Carrito carrito = carritoService.obtenerCarritoActivo(usuario);
    model.addAttribute("carrito", carrito);
    model.addAttribute("items", carrito.getItems());
    return "carrito";  
}

    // Agregar producto
    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Long idProducto,
                                  @RequestParam(defaultValue = "1") int cantidad) {
        Usuario usuario = usuarioService.obtenerUsuarioActual();
        carritoService.agregarProducto(usuario, idProducto, cantidad);
        return "redirect:/carrito";
    }

    // Eliminar producto
    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Long idDetalle) {
        carritoService.eliminarItem(idDetalle);
        return "redirect:/carrito";
    }
}