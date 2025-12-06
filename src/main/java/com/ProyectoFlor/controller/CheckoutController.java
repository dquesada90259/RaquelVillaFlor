package com.ProyectoFlor.controller;

import com.ProyectoFlor.model.*;
import com.ProyectoFlor.service.CarritoService;
import com.ProyectoFlor.service.PedidoService;
import com.ProyectoFlor.service.UsuarioService;
import com.ProyectoFlor.repository.MetodoPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {

    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final MetodoPagoRepository metodoPagoRepository;

    // 1️⃣ Selección de método de entrega
    @GetMapping("/entrega")
    public String seleccionEntrega(Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoActivo(usuario);

        model.addAttribute("carrito", carrito);
        return "checkout-entrega";
    }

    @PostMapping("/entrega")
    public String procesarEntrega(@RequestParam String metodoEntrega,
                                  @RequestParam(required = false) String fechaAgendada) {

        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoActivo(usuario);

        LocalDateTime fecha = null;
        if (fechaAgendada != null && !fechaAgendada.isEmpty()) {
            fecha = LocalDateTime.parse(fechaAgendada); // formato: yyyy-MM-ddTHH:mm
        }

        // Guardamos info temporal en carrito
        carrito.setMetodoEntrega(metodoEntrega);
        carrito.setFechaEntregaProgramada(fecha);
        carritoService.recalcularTotal(carrito);

        return "redirect:/checkout/pago";
    }

    // 2️⃣ Selección de método de pago
    @GetMapping("/pago")
    public String seleccionarPago(Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoActivo(usuario);
        var metodos = metodoPagoRepository.findAll();

        model.addAttribute("carrito", carrito);
        model.addAttribute("metodos", metodos);
        return "checkout-pago";
    }

    @PostMapping("/pago")
    public String procesarPago(@RequestParam Long idMetodoPago, Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoActivo(usuario);

        MetodoPago metodoPago = metodoPagoRepository.findById(idMetodoPago)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        Pedido pedido = pedidoService.crearPedidoDesdeCarrito(
                carrito,
                metodoPago,
                carrito.getMetodoEntrega(),
                2000.0, // Ejemplo: costo de envío fijo o calculado dinámicamente
                carrito.getFechaEntregaProgramada()
        );

        // Pasamos el pedido a la vista de confirmación
        model.addAttribute("pedido", pedido);

        return "pedido-confirmacion";
    }
}