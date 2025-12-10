package com.ProyectoFlor.service;

import com.ProyectoFlor.model.Pedido;
import com.ProyectoFlor.model.PersonalizacionPedido;
import com.ProyectoFlor.repository.PersonalizacionPedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalizacionPedidoService {

    private final PersonalizacionPedidoRepository repo;

    public PersonalizacionPedido obtenerOcrear(Pedido pedido) {
        PersonalizacionPedido p = repo.findByPedido(pedido);
        if (p == null) {
            p = new PersonalizacionPedido();
            p.setPedido(pedido);
            repo.save(p);
        }
        return p;
    }

    public void guardar(PersonalizacionPedido p) {
        repo.save(p);
    }

    public double calcularCostoArreglo(String tipo) {
        return switch (tipo) {
            case "ramo" -> 5000.0;
            case "caja" -> 7000.0;
            case "florero" -> 9000.0;
            case "canasta" -> 11000.0;
            default -> 0.0;
        };
    }

    public double calcularCostoComplementos(List<String> complementos) {
        if (complementos == null) return 0.0;

        double total = 0;
        for (String c : complementos) {
            if (c.equals("peluche")) total += 8000;
            if (c.equals("chocolates")) total += 5000;
            if (c.equals("globo")) total += 3000;
        }
        return total;
    }
}
