package com.ProyectoFlor;  

import com.ProyectoFlor.services.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private RutaService rutaService;  // Inyectamos el servicio para obtener las rutas

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var rutas = rutaService.getRutas();  // Obtenemos las rutas dinámicamente desde la base de datos

        // Configuración de las rutas y acceso a ellas
        http.authorizeHttpRequests(requests -> {
            for (Ruta ruta : rutas) {
                if (ruta.isRequiereRol()) {
                    requests.requestMatchers(ruta.getRuta()).hasRole(ruta.getRol().getRol());
                } else {
                    requests.requestMatchers(ruta.getRuta()).permitAll();
                }
            }
            requests.anyRequest().authenticated();  // Protege todas las demás rutas
        });

        // Configuración del formulario de login
        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
        )
        // Configuración de logout
        .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        )
        // Manejo de excepciones
        .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/acceso_denegado")
        )
        // Configuración de sesiones
        .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );

        return http.build();
    }

    // Password Encoder para encriptar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Utilizamos BCrypt para la seguridad
    }

    // Configuración global de autenticación
    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build, 
                                  @Lazy PasswordEncoder passwordEncoder, 
                                  @Lazy UserDetailsService userDetailsService) throws Exception {
        build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);  // Conectamos el servicio de usuarios
    }
}
