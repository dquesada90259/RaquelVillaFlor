package com.ProyectoFlor.controller;

import com.ProyectoFlor.model.Pedido;
import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.service.PedidoService;
import com.ProyectoFlor.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    @GetMapping("/confirmacion")
    public String confirmacionPedido(Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioActual();
        Pedido pedido = pedidoService.obtenerUltimoPedido(usuario); // método que devuelve el último pedido
        model.addAttribute("pedido", pedido);
        return "pedido-confirmacion";
    }
    
}
