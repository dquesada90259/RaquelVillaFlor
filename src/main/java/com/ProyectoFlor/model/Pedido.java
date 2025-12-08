package com.ProyectoFlor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private Double total;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    public enum EstadoPedido {
        pendiente, pagado, enviado, entregado, cancelado
    }

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago")
    private MetodoPago metodoPago;

    private String metodoEntrega;

    private Double costoEnvio;

    @Column(name = "fecha_entrega_programada")
    private LocalDateTime fechaEntregaProgramada;

    // 👇 NECESARIO PARA QUE el HTML funcione
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private java.util.List<DetallePedido> detalles;
}

