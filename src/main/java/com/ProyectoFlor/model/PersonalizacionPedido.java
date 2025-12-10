package com.ProyectoFlor.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "personalizacion_pedido")
public class PersonalizacionPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con pedido existente
    @OneToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    // PP-001 Tipo de arreglo (ramo, caja, florero, canasta)
    private String tipoArreglo;
    private Double costoArreglo;

    // PP-002 Flores seleccionadas
    @ElementCollection
    @CollectionTable(name = "personalizacion_flores", joinColumns = @JoinColumn(name = "id_personalizacion"))
    @Column(name = "flor")
    private List<String> flores;

    // Colores
    private String colorPredominante;

    // PP-003 Dedicatoria
    @Column(length = 500)
    private String dedicatoria;

    // Complementos elegidos
    @ElementCollection
    @CollectionTable(name = "personalizacion_complementos", joinColumns = @JoinColumn(name = "id_personalizacion"))
    @Column(name = "complemento")
    private List<String> complementos;

    private Double costoComplementos = 0.0;

    // Total calculado de la personalización
    private Double totalPersonalizacion;
}
