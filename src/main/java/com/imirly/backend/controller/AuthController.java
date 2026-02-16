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
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByEmail(request.getEmail());
        String jwt = jwtTokenProvider.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(
                JwtResponse.builder()
                        .token(jwt)
                        .id(user.getId())
                        .nombre(user.getNombre())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .datosCompletos(user.tieneDatosCompletos())
                        .build()
        );
    }


    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {

        User user = userService.register(request);

        //  Auto-login tras registro (con email)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(
                JwtResponse.builder()
                        .token(jwt)
                        .id(user.getId())
                        .nombre(user.getNombre())
                        .email(user.getEmail())
                        .role("USER")
                        .datosCompletos(user.tieneDatosCompletos())
                        .build()
        );
    }
}
