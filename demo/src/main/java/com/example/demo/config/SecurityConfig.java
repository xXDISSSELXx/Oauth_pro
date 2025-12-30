package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Todo requiere login
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/", true) // Al loguearse, ir al inicio
                )
                .csrf(csrf -> csrf.disable()); // <--- ESTO ES LO QUE SOLUCIONA EL ERROR DEL FORMULARIO

        return http.build();
    }

}