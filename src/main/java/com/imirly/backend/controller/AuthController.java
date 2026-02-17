package com.imirly.backend.controller;

import com.imirly.backend.dto.request.LoginRequest;
import com.imirly.backend.dto.request.RegisterRequest;
import com.imirly.backend.dto.response.JwtResponse;
import com.imirly.backend.entity.User;
import com.imirly.backend.security.JwtTokenProvider;
import com.imirly.backend.security.UserDetailsImpl;
import com.imirly.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Buscar usuario por email para obtener el ID
            User user = userService.findByEmail(request.getEmail());

            // Autenticar usando el ID como username (String) y la contraseña
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getId().toString(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            return ResponseEntity.ok(JwtResponse.builder()
                    .token(jwt)
                    .id(userDetails.getId())
                    .nombre(userDetails.getNombre())
                    .email(userDetails.getEmail())
                    .role(userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""))
                    .datosCompletos(user.tieneDatosCompletos())
                    .build());

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en login: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Crear usuario
            User user = userService.register(request);

            // Auto-login después de registro
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getId().toString(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            return ResponseEntity.ok(JwtResponse.builder()
                    .token(jwt)
                    .id(user.getId())
                    .nombre(user.getNombre())
                    .email(user.getEmail())
                    .role("USER")
                    .datosCompletos(false)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error en registro: " + e.getMessage());
        }
    }
}