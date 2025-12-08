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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   @Lazy RutaService rutaService) throws Exception {

        http.authorizeHttpRequests(auth -> {

            // Rutas públicas fijas
            auth.requestMatchers(
                    "/", "/index", "/home",
                    "/catalogo", "/productos/**",
                    "/usuario/login", "/usuario/registro",
                    "/error", "/acceso_denegado"
            ).permitAll();

            // Recursos estáticos
            auth.requestMatchers("/css/**", "/js/**", "/img/**").permitAll();

            // Recuperar/restablecer CONTRASEÑA SIEMPRE accesible sin login
            auth.requestMatchers(
                    "/usuario/recuperar",
                    "/usuario/recuperar/**",
                    "/usuario/restablecer",
                    "/usuario/restablecer/**"
            ).permitAll();

            // Rutas dinámicas desde BD
            var rutas = rutaService.getRutas();

            for (Ruta ruta : rutas) {

                if (ruta.isRequiereRol() && ruta.getRol() != null) {
                    auth.requestMatchers(ruta.getRuta())
                            .hasRole(ruta.getRol().getRol());
                } else {
                    auth.requestMatchers(ruta.getRuta()).permitAll();
                }
            }

            // Todo lo demás requiere autenticación
            auth.anyRequest().authenticated();
        });

        // LOGIN
        http.formLogin(form -> form
                .loginPage("/usuario/login")
                .loginProcessingUrl("/usuario/login")
                .usernameParameter("correo")
                .passwordParameter("contrasena")
                .defaultSuccessUrl("/catalogo", true)
                .failureUrl("/usuario/login?error=true")
                .permitAll()
        );

        // LOGOUT
        http.logout(logout -> logout
                .logoutUrl("/usuario/logout")
                .logoutSuccessUrl("/usuario/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        // ACCESO DENEGADO
        http.exceptionHandling(e -> e.accessDeniedPage("/acceso_denegado"));

        // CONTROL DE SESIÓN — Expiración por inactividad
        http.sessionManagement(session -> session
                .invalidSessionUrl("/usuario/login?expired=true")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );

        // Desactivar CSRF para facilitar pruebas
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
