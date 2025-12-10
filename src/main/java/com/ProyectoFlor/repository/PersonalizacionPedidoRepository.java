package com.ProyectoFlor.repository;

import com.ProyectoFlor.model.PersonalizacionPedido;
import com.ProyectoFlor.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalizacionPedidoRepository extends JpaRepository<PersonalizacionPedido, Long> {

    PersonalizacionPedido findByPedido(Pedido pedido);
}
