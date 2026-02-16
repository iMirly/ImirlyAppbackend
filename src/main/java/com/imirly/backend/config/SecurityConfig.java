package com.imirly.backend.config;

import com.imirly.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                //  CSRF desactivado (API REST + JWT)
                .csrf(csrf -> csrf.disable())

                //  CORS (importante para frontend / Android)
                .cors(Customizer.withDefaults())

                //  Stateless (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //  AUTORIZACIÓN
                .authorizeHttpRequests(auth -> auth

                        // ─────────────────────────────
                        //  FRONTEND ESTÁTICO (PÚBLICO)
                        // ─────────────────────────────
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/static/**"
                        ).permitAll()

                        // ─────────────────────────────
                        //  H2 CONSOLE (DEV)
                        // ─────────────────────────────
                        .requestMatchers("/h2-console/**").permitAll()

                        // ─────────────────────────────
                        //  AUTH
                        // ─────────────────────────────
                        .requestMatchers("/api/auth/**").permitAll()

                        // ─────────────────────────────
                        //  DATOS PÚBLICOS
                        // ─────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/anuncios/public/**").permitAll()

                        // ─────────────────────────────
                        //  RESTO REQUIERE JWT
                        // ─────────────────────────────
                        .anyRequest().authenticated()
                )

                //  Provider de autenticación
                .authenticationProvider(authenticationProvider())

                //  Filtro JWT
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        //  H2 Console necesita frames
        http.headers(headers ->
                headers.frameOptions(frame -> frame.sameOrigin())
        );

        return http.build();
    }

    // ─────────────────────────────
    // AUTH PROVIDER
    // ─────────────────────────────
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // ─────────────────────────────
    // AUTH MANAGER
    // ─────────────────────────────
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    // ─────────────────────────────
    // PASSWORD ENCODER
    // ─────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
