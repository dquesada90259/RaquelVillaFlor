package com.ProyectoFlor.service;

import com.ProyectoFlor.model.*;
import com.ProyectoFlor.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.*;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.List; 

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

                .direccionEntrega(carrito.getDireccionEntrega())
                .distritoEntrega(carrito.getDistritoEntrega())

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
     * Permite modificar la fecha de un pedido agendado.
     */
    @Transactional
    public Pedido modificarFecha(Long idPedido, LocalDateTime nuevaFecha) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        
        if (!puedeModificar(pedido)) {
            throw new RuntimeException("El pedido ya está en proceso y no se puede modificar.");
        }

        pedido.setFechaEntregaProgramada(nuevaFecha);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void cancelarPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!puedeModificar(pedido)) {
            throw new RuntimeException("El pedido ya está en proceso y no se puede cancelar.");
        }

        pedido.setEstado(Pedido.EstadoPedido.cancelado);
        pedidoRepository.save(pedido);
    }

    public Pedido obtenerUltimoPedido(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario)
                .stream()
                .max(Comparator.comparing(Pedido::getFechaPedido))
                .orElseThrow(() -> new RuntimeException("No hay pedidos para este usuario"));
    }

    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    // MODIFICACIÓN

    // Lista todos los pedidos de un usuario
    public List<Pedido> obtenerPedidosPorUsuario(Usuario usuario) { 
        return pedidoRepository.findByUsuario(usuario);
    }

    // Regla de negocio: cuándo se puede modificar/cancelar un pedido
    public boolean puedeModificar(Pedido pedido) { // <-- NUEVO
        // Pendiente o pagado = todavía no va en camino
        return pedido.getEstado() == Pedido.EstadoPedido.pendiente
                || pedido.getEstado() == Pedido.EstadoPedido.pagado;
    }

    // Modificar datos del pedido (fecha, dirección, método, distrito)
    @Transactional
    public Pedido modificarPedidoCompleto( 
            Long idPedido,
            String nuevoMetodoEntrega,
            String nuevaDireccion,
            String nuevoDistrito,
            LocalDateTime nuevaFechaEntrega
    ) {
        Pedido pedido = obtenerPedidoPorId(idPedido);

        if (!puedeModificar(pedido)) {
            throw new RuntimeException("El pedido ya está en proceso y no se puede modificar.");
        }

        pedido.setMetodoEntrega(nuevoMetodoEntrega);
        pedido.setDireccionEntrega(nuevaDireccion);
        pedido.setDistritoEntrega(nuevoDistrito);
        pedido.setFechaEntregaProgramada(nuevaFechaEntrega);

        return pedidoRepository.save(pedido);
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void iniciarTareasProgramadas() {
        scheduler.scheduleAtFixedRate(() -> {
            List<Pedido> pendientes = pedidoRepository.findByEstado(Pedido.EstadoPedido.pendiente);

            for (Pedido p : pendientes) {
                // Si ya pasaron al menos 20 segundos desde su creación
                if (p.getFechaPedido().isBefore(LocalDateTime.now().minusSeconds(20))) {
                    p.setEstado(Pedido.EstadoPedido.enviado);
                    pedidoRepository.save(p);
                }
            }

        }, 5, 5, TimeUnit.SECONDS);  // revisa cada 5 segundos
    }
}

