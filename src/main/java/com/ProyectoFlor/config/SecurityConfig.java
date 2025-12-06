package com.ProyectoFlor.config;

import com.ProyectoFlor.model.Ruta;
import com.ProyectoFlor.service.CustomUserDetailsService;
import com.ProyectoFlor.service.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * SecurityFilterChain moderno para Spring Security 6 / Spring Boot 3
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   @Lazy RutaService rutaService) throws Exception {

        var rutas = rutaService.getRutas(); // solo se carga cuando se llama, rompe ciclo

        http.authorizeHttpRequests(requests -> {
            for (Ruta ruta : rutas) {
                if (ruta.isRequiereRol()) {
                    // hasRole porque en UserDetails agregamos ROLE_
                    requests.requestMatchers(ruta.getRuta())
                            .hasRole(ruta.getRol().getRol());
                } else {
                    requests.requestMatchers(ruta.getRuta())
                            .permitAll();
                }
            }
            requests.anyRequest().authenticated();
        });

        http.formLogin(form -> form
                .loginPage("/usuario/login")
                .loginProcessingUrl("/usuario/login")
                .usernameParameter("correo")
                .passwordParameter("contrasena")
                .defaultSuccessUrl("/catalogo", true)
                .failureUrl("/usuario/login?error=true")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/usuario/logout")
                .logoutSuccessUrl("/usuario/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        http.exceptionHandling(exceptions ->
                exceptions.accessDeniedPage("/acceso_denegado")
        );

        http.sessionManagement(session ->
                session.maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
        );

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * AuthenticationManager moderno
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .userDetailsService(userDetailsService)
                   .passwordEncoder(passwordEncoder())
                   .and()
                   .build();
    }

    /**
     * PasswordEncoder de prueba (sin encriptar)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // para contraseñas planas en pruebas
    }
}
