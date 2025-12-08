package com.ProyectoFlor.service;

import com.ProyectoFlor.model.*;
import com.ProyectoFlor.repository.CarritoDetalleRepository;
import com.ProyectoFlor.repository.CarritoRepository;
import com.ProyectoFlor.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoDetalleRepository carritoDetalleRepository;
    private final ProductoRepository productoRepository;

    // Obtener carrito activo de un usuario
    public Carrito obtenerCarritoActivo(Usuario usuario) {
        return carritoRepository.findByUsuarioAndEstado(usuario, Carrito.EstadoCarrito.activo)
                .orElseGet(() -> {
                    Carrito nuevo = Carrito.builder()
                            .usuario(usuario)
                            .estado(Carrito.EstadoCarrito.activo)
                            .total(0.0)
                            .build();
                    return carritoRepository.save(nuevo);
                });
    }

    // Agregar producto al carrito
    @Transactional
    public void agregarProducto(Usuario usuario, Long idProducto, int cantidad) {
        Carrito carrito = obtenerCarritoActivo(usuario);
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        CarritoDetalle detalle = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(idProducto))
                .findFirst()
                .orElse(null);

        if (detalle != null) {
            detalle.setCantidad(detalle.getCantidad() + cantidad);
            detalle.setSubtotal(detalle.getProducto().getPrecio().doubleValue() * detalle.getCantidad());
            carritoDetalleRepository.save(detalle);
        } else {
            CarritoDetalle nuevoDetalle = CarritoDetalle.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(cantidad)
                    .subtotal(producto.getPrecio().doubleValue() * cantidad)
                    .build();
            carritoDetalleRepository.save(nuevoDetalle);
            carrito.getItems().add(nuevoDetalle);
        }

        recalcularTotal(carrito);
    }

    // Eliminar producto del carrito
    @Transactional
    public void eliminarItem(Long idDetalle) {
        CarritoDetalle detalle = carritoDetalleRepository.findById(idDetalle)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));
        Carrito carrito = detalle.getCarrito();
        carrito.getItems().remove(detalle);
        carritoDetalleRepository.delete(detalle);
        recalcularTotal(carrito);
    }

    // Recalcular total del carrito, sumando subtotal de items y costo de entrega si aplica
    @Transactional
    public void recalcularTotal(Carrito carrito) {

    double subtotal = carrito.getItems().stream()
            .mapToDouble(item -> item.getSubtotal())
            .sum();

    double costoEnvio = calcularCostoEnvio(carrito);

    carrito.setCostoEnvio(costoEnvio);
    carrito.setTotal(subtotal + costoEnvio);

    carritoRepository.save(carrito);
    }
    
    public double calcularCostoEnvio(Carrito carrito) {

    if ("domicilio".equalsIgnoreCase(carrito.getMetodoEntrega())) {

        if (carrito.getDistritoEntrega() == null) return 0;

        switch (carrito.getDistritoEntrega()) {
            case "San Isidro de El General":
                return 2000;
            case "El General (General Viejo)":
                return 2500;
            case "Daniel Flores":
                return 1500;
            case "Rivas":
                return 3000;
            default:
                return 0; // desconocido
        }

    }

    // Recoger o agendar → envío gratis
    return 0;
    }
}