package com.ProyectoFlor.controller;

import com.ProyectoFlor.model.Carrito;
import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.service.CarritoService;
import com.ProyectoFlor.service.PedidoService;
import com.ProyectoFlor.service.UsuarioService;
import com.ProyectoFlor.repository.MetodoPagoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {

    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final MetodoPagoRepository metodoPagoRepository;

    // =============================================
    // GET - Selección de método de entrega
    // =============================================
    @GetMapping("/entrega")
    public String seleccionEntrega(Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoActivo(usuario);

        model.addAttribute("carrito", carrito);
        return "checkout-entrega";
    }

    // =============================================
    // POST - Procesar método de entrega
    // =============================================
    @PostMapping("/entrega")
    public String procesarEntrega(@RequestParam String metodoEntrega,
                                  @RequestParam(required = false) String direccionDomicilio,
                                  @RequestParam(required = false) String distrito,
                                  @RequestParam(required = false) String fechaRecoger,
                                  @RequestParam(required = false) String fechaAgendar,
                                  Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoActivo(usuario);

        // ========================================================
        // 1️⃣ ENTREGA A DOMICILIO
        // ========================================================
        if (metodoEntrega.equals("domicilio")) {

            if (direccionDomicilio == null || direccionDomicilio.trim().isEmpty()) {
                model.addAttribute("error", "Debes ingresar la dirección exacta.");
                return "checkout-entrega";
            }

            if (distrito == null || distrito.trim().isEmpty()) {
                model.addAttribute("error", "Debes seleccionar un distrito válido.");
                return "checkout-entrega";
            }

            carrito.setMetodoEntrega("domicilio");
            carrito.setDireccionEntrega(direccionDomicilio.trim());
            carrito.setDistritoEntrega(distrito);
            carrito.setFechaEntregaProgramada(null);

            carritoService.recalcularTotal(carrito);
            return "redirect:/checkout/pago";
        }

        // ========================================================
        // 2️⃣ RECOGER EN TIENDA o AGENDAR ENTREGA
        // ========================================================
        String fechaStr = null;

        if (metodoEntrega.equals("recoger")) {
            fechaStr = fechaRecoger;
        } else if (metodoEntrega.equals("agendar")) {
            fechaStr = fechaAgendar;
        }

        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            model.addAttribute("error", "Debes seleccionar una fecha y hora.");
            return "checkout-entrega";
        }

        LocalDateTime fecha;

        try {
            fecha = LocalDateTime.parse(fechaStr.trim());
        } catch (Exception e) {
            model.addAttribute("error", "La fecha seleccionada no es válida.");
            return "checkout-entrega";
        }

        // ❌ Fecha pasada
        if (fecha.isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "La fecha seleccionada no puede estar en el pasado.");
            return "checkout-entrega";
        }

        // ❌ Domingo
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            model.addAttribute("error", "No se realizan entregas ni retiros los domingos.");
            return "checkout-entrega";
        }

        // ❌ Horario fuera de rango
        int hora = fecha.getHour();
        if (hora < 8 || hora > 19) {
            model.addAttribute("error", "Debes seleccionar una hora dentro del horario de atención (8am–7pm).");
            return "checkout-entrega";
        }

        // Guardar configuración
        carrito.setMetodoEntrega(metodoEntrega);
        carrito.setFechaEntregaProgramada(fecha);
        carrito.setDireccionEntrega(null);
        carrito.setDistritoEntrega(null);

        carritoService.recalcularTotal(carrito);

        return "redirect:/checkout/pago";
    }

    // =============================================
    // GET - Pago
    // =============================================
    @GetMapping("/pago")
    public String seleccionarPago(Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoActivo(usuario);

        model.addAttribute("carrito", carrito);
        model.addAttribute("metodos", metodoPagoRepository.findAll());

        return "checkout-pago";
    }
}