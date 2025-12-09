package com.ProyectoFlor.controller;

import com.ProyectoFlor.model.Pedido;
import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.service.PedidoService;
import com.ProyectoFlor.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; 


@Controller
@RequiredArgsConstructor
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    @GetMapping("/confirmacion")
    public String confirmacionPedido(Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Pedido pedido = pedidoService.obtenerUltimoPedido(usuario); 
        model.addAttribute("pedido", pedido);
        return "pedido-confirmacion";
    }

    // Mis pedidos

    @GetMapping("/mis-pedidos") 
    public String misPedidos(Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioActual();
        model.addAttribute("pedidos", pedidoService.obtenerPedidosPorUsuario(usuario));
        return "mis-pedidos";
    }

    // formulario para modificar pedido

    @GetMapping("/editar/{id}")
    public String editarPedido(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(id);

        if (!pedidoService.puedeModificar(pedido)) {
            return "redirect:/pedido/mis-pedidos?error=no-edit";
        }

        model.addAttribute("pedido", pedido);
        return "pedido-editar";
    }

    @PostMapping("/editar/{id}")
    public String guardarCambios(@PathVariable Long id,
                                 @ModelAttribute("pedido") Pedido pedidoForm) {

        pedidoService.modificarPedidoCompleto(
                id,
                pedidoForm.getMetodoEntrega(),
                pedidoForm.getDireccionEntrega(),
                pedidoForm.getDistritoEntrega(),
                pedidoForm.getFechaEntregaProgramada()
        );

        return "redirect:/pedido/mis-pedidos?success=edit";
    }

    // cancelar pedido

    @GetMapping("/cancelar/{id}")
    public String cancelarPedido(@PathVariable Long id) {
        try {
            pedidoService.cancelarPedido(id);
            return "redirect:/pedido/mis-pedidos?success=cancel";
        } catch (RuntimeException e) {
            return "redirect:/pedido/mis-pedidos?error=no-cancel";
        }
    }
}
