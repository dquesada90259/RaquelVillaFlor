package com.ProyectoFlor.controller;

import com.ProyectoFlor.model.Pedido;
import com.ProyectoFlor.model.PersonalizacionPedido;
import com.ProyectoFlor.model.Usuario;
import com.ProyectoFlor.service.PedidoService;
import com.ProyectoFlor.service.PersonalizacionPedidoService;
import com.ProyectoFlor.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/personalizacion")
public class PersonalizacionPedidoController {

    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final PersonalizacionPedidoService personalizacionService;

    @GetMapping("/{idPedido}")
    public String personalizar(@PathVariable Long idPedido, Model model) {

        Pedido pedido = pedidoService.obtenerPedidoPorId(idPedido);

        PersonalizacionPedido p = personalizacionService.obtenerOcrear(pedido);

        model.addAttribute("pedido", pedido);
        model.addAttribute("personalizacion", p);

        model.addAttribute("tiposArreglo", List.of("ramo", "caja", "florero", "canasta"));
        model.addAttribute("colores", List.of("rojo", "blanco", "amarillo", "rosa", "morado", "azul"));
        model.addAttribute("floresDisponibles", List.of("rosas", "tulipanes", "girasoles", "lirios", "margaritas"));
        model.addAttribute("complementosDisponibles", List.of("peluche", "chocolates", "globo"));

        return "personalizacion-form";
    }

    @PostMapping("/guardar/{idPedido}")
    public String guardar(
            @PathVariable Long idPedido,
            @RequestParam String tipoArreglo,
            @RequestParam(required = false) List<String> flores,
            @RequestParam(required = false) String colorPredominante,
            @RequestParam(required = false) String dedicatoria,
            @RequestParam(required = false) List<String> complementos
    ) {

        Pedido pedido = pedidoService.obtenerPedidoPorId(idPedido);
        PersonalizacionPedido p = personalizacionService.obtenerOcrear(pedido);

        // Validaciones PP-004
        if (tipoArreglo == null || tipoArreglo.isEmpty()) {
            return "redirect:/personalizacion/" + idPedido + "?error=tipo";
        }

        if (flores == null || flores.isEmpty()) {
            return "redirect:/personalizacion/" + idPedido + "?error=flores";
        }

        // Guardar PP-001 y PP-002
        p.setTipoArreglo(tipoArreglo);
        p.setCostoArreglo(personalizacionService.calcularCostoArreglo(tipoArreglo));
        p.setFlores(flores);
        p.setColorPredominante(colorPredominante);

        // PP-003 Dedicatoria
        p.setDedicatoria(dedicatoria);

        // PP-003 Complementos
        p.setComplementos(complementos);
        p.setCostoComplementos(personalizacionService.calcularCostoComplementos(complementos));

        // Total
        p.setTotalPersonalizacion(
                p.getCostoArreglo() + p.getCostoComplementos()
        );

        personalizacionService.guardar(p);

        return "redirect:/personalizacion/" + idPedido + "?success=ok";
    }
}
