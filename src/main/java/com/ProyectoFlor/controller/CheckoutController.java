package com.ProyectoFlor.controller;

import com.ProyectoFlor.model.Carrito;
import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.repository.MetodoPagoRepository;
import com.ProyectoFlor.service.CarritoService;
import com.ProyectoFlor.service.PedidoService;
import com.ProyectoFlor.service.UsuarioService;

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

    // Método de entrega

    @GetMapping("/entrega")
    public String seleccionEntrega(Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoActivo(usuario);

        model.addAttribute("carrito", carrito);
        model.addAttribute("usuarioLogeado", usuario);

        return "checkout-entrega";
    }

    // Procesar método de entrega
    @PostMapping("/entrega")
    public String procesarEntrega(@RequestParam String metodoEntrega,
                                  @RequestParam(required = false) String direccionDomicilio,
                                  @RequestParam(required = false) String distrito,
                                  @RequestParam(required = false) String fechaRecoger,
                                  @RequestParam(required = false) String fechaAgendar,
                                  Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoActivo(usuario);


        //️⃣ ENTREGA A DOMICILIO

        if (metodoEntrega.equals("domicilio")) {

            if (direccionDomicilio == null || direccionDomicilio.trim().isEmpty()) {
                model.addAttribute("error", "Debes ingresar la dirección exacta.");
                model.addAttribute("carrito", carrito);
                return "checkout-entrega";
            }

            if (distrito == null || distrito.trim().isEmpty()) {
                model.addAttribute("error", "Debes seleccionar un distrito válido.");
                model.addAttribute("carrito", carrito);
                return "checkout-entrega";
            }

            carrito.setMetodoEntrega("domicilio");
            carrito.setDireccionEntrega(direccionDomicilio.trim());
            carrito.setDistritoEntrega(distrito);
            carrito.setFechaEntregaProgramada(null);

            carritoService.recalcularTotal(carrito);
            return "redirect:/checkout/pago";
        }


        // 2️⃣ RECOGER EN TIENDA o AGENDAR

        String fechaStr = metodoEntrega.equals("recoger") ? fechaRecoger : fechaAgendar;

        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            model.addAttribute("error", "Debes seleccionar una fecha y hora.");
            model.addAttribute("carrito", carrito);
            return "checkout-entrega";
        }

        LocalDateTime fecha;
        try {
            fecha = LocalDateTime.parse(fechaStr.trim());
        } catch (Exception e) {
            model.addAttribute("error", "La fecha seleccionada no es válida.");
            model.addAttribute("carrito", carrito);
            return "checkout-entrega";
        }

        if (fecha.isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "La fecha seleccionada no puede estar en el pasado.");
            model.addAttribute("carrito", carrito);
            return "checkout-entrega";
        }

        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            model.addAttribute("error", "No se realizan entregas los domingos.");
            model.addAttribute("carrito", carrito);
            return "checkout-entrega";
        }

        int hora = fecha.getHour();
        if (hora < 8 || hora > 19) {
            model.addAttribute("error", "Horario permitido: 8am a 7pm.");
            model.addAttribute("carrito", carrito);
            return "checkout-entrega";
        }

        carrito.setMetodoEntrega(metodoEntrega);
        carrito.setFechaEntregaProgramada(fecha);

        carrito.setDireccionEntrega(null);
        carrito.setDistritoEntrega(null);

        carritoService.recalcularTotal(carrito);

        return "redirect:/checkout/pago";
    }

    //Selección de pago

    @GetMapping("/pago")
    public String seleccionarPago(Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoActivo(usuario);

        model.addAttribute("carrito", carrito);
        model.addAttribute("metodos", metodoPagoRepository.findAll());
        model.addAttribute("usuarioLogeado", usuario);

        return "checkout-pago";
    }


    // cONFIRMAR PAGO Y CREAR PEDIDO

    @PostMapping("/pago")
    public String procesarPago(@RequestParam Long idMetodoPago, Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarritoActivo(usuario);

        if (carrito.getItems().isEmpty()) {
            model.addAttribute("error", "Tu carrito está vacío.");
            model.addAttribute("carrito", carrito);
            model.addAttribute("metodos", metodoPagoRepository.findAll());
            model.addAttribute("usuarioLogeado", usuario);
            return "checkout-pago";
        }

        var metodoPago = metodoPagoRepository.findById(idMetodoPago)
                .orElseThrow(() -> new RuntimeException("Método de pago no válido"));

        var pedido = pedidoService.crearPedidoDesdeCarrito(
                carrito,
                metodoPago,
                carrito.getMetodoEntrega(),
                carrito.getCostoEnvio(),
                carrito.getFechaEntregaProgramada()
        );

        return "redirect:/checkout/confirmacion/" + pedido.getId();
    }


    // GET - MOSTRAR CONFIRMACIÓN DEL PEDIDO

    @GetMapping("/confirmacion/{id}")
    public String mostrarConfirmacion(@PathVariable Long id, Model model) {

        var pedido = pedidoService.obtenerPedidoPorId(id);
        Usuario usuario = usuarioService.obtenerUsuarioActual();

        model.addAttribute("usuarioLogeado", usuario);

        model.addAttribute("pedido", pedido);
        return "pedido-confirmacion";
    }
}