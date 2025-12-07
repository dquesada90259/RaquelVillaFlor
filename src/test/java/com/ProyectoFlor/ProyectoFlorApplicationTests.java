package com.ProyectoFlor;

import com.ProyectoFlor.service.RutaService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
class ProyectoFlorApplicationTests {

    @MockBean
    private RutaService rutaService; // Mock temporal para el test

    @Test
    void contextLoads() {
        // Para que Spring Security pueda inicializar, devolvemos una lista simple
        when(rutaService.getRutas()).thenReturn(List.of());
    }
}
