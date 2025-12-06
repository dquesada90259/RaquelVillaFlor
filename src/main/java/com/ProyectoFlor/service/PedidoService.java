package com.ProyectoFlor.service;

import com.ProyectoFlor.model.*;
import com.ProyectoFlor.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final CarritoRepository carritoRepository;
    private final EnvioRepository envioRepository;

    /**
     * Convierte carrito en pedido.
     */
    @Transactional
    public Pedido crearPedidoDesdeCarrito(Carrito carrito, MetodoPago metodoPago, String metodoEntrega,
                                          Double costoEnvio, LocalDateTime fechaAgendada) {

        Pedido pedido = Pedido.builder()
                .usuario(carrito.getUsuario())
                .total(carrito.getTotal() + costoEnvio)
                .fechaPedido(LocalDateTime.now())
                .estado(Pedido.EstadoPedido.pendiente)
                .metodoPago(metodoPago)
                .metodoEntrega(metodoEntrega)
                .costoEnvio(costoEnvio)
                .fechaEntregaProgramada(fechaAgendada)
                .build();

        pedidoRepository.save(pedido);

        // Convertir items
        for (CarritoDetalle item : carrito.getItems()) {
            DetallePedido dp = DetallePedido.builder()
                    .pedido(pedido)
                    .producto(item.getProducto())
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getProducto().getPrecio().doubleValue())
                    .subtotal(item.getSubtotal())
                    .build();
            detallePedidoRepository.save(dp);
        }

        // Vaciar y cerrar carrito
        carrito.setEstado(Carrito.EstadoCarrito.comprado);
        carritoRepository.save(carrito);

        // Crear registro de envío si aplica
        if (metodoEntrega.equals("domicilio")) {
            Envio envio = Envio.builder()
                    .pedido(pedido)
                    .estado(Envio.EstadoEnvio.pendiente)
                    .build();

            envioRepository.save(envio);
        }

        return pedido;
    }

    /**
     * Permite modificar o cancelar un pedido agendado.
     */
    @Transactional
    public Pedido modificarFecha(Long idPedido, LocalDateTime nuevaFecha) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setFechaEntregaProgramada(nuevaFecha);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void cancelarPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(Pedido.EstadoPedido.cancelado);
        pedidoRepository.save(pedido);
    }
    
    public Pedido obtenerUltimoPedido(Usuario usuario) {
    return pedidoRepository.findByUsuario(usuario)
            .stream()
            .max(Comparator.comparing(Pedido::getFechaPedido))
            .orElseThrow(() -> new RuntimeException("No hay pedidos para este usuario"));
}
}
