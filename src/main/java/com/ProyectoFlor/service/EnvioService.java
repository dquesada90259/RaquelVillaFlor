package com.ProyectoFlor.service;

import com.ProyectoFlor.model.Envio;
import com.ProyectoFlor.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnvioService {

    private final EnvioRepository envioRepository;

    public void actualizarEstado(Long idEnvio, Envio.EstadoEnvio estado) {
        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new RuntimeException("Envio no encontrado"));
        envio.setEstado(estado);
        envioRepository.save(envio);
    }
}
