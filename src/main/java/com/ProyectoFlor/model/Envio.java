package com.ProyectoFlor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    private EstadoEnvio estado;

    public enum EstadoEnvio {
        pendiente, en_camino, entregado, devuelto
    }

    private LocalDateTime fechaEnvio;

    private LocalDateTime fechaEntrega;

    private String empresa;

    private String tracking;
}

