package com.imirly.backend.security;

import com.imirly.backend.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        String secret = jwtConfig.getSecret();

        log.info("=== JWT CONFIG ===");
        log.info("Secret es null: {}", secret == null);
        log.info("Secret está vacío: {}", secret != null && secret.isEmpty());
        log.info("Secret length: {}", secret != null ? secret.length() : 0);

        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT secret no configurado. Verifica application.properties");
        }

        // USAR STRING DIRECTO (no Base64) - asegurar que tenga al menos 64 caracteres
        // HS512 necesita 512 bits = 64 bytes
        if (secret.length() < 64) {
            log.warn("Secret muy corto ({} chars), extendiendo a 64 chars", secret.length());
            secret = String.format("%-64s", secret).replace(' ', 'X'); // Rellenar con 'X'
        }

        log.info("Secret final length: {} chars", secret.length());
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationMs());

        return Jwts.builder()
                .subject(userPrincipal.getId().toString())
                .claim("email", userPrincipal.getEmail())
                .claim("role", userPrincipal.getAuthorities().iterator().next().getAuthority())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Firma JWT inválida");
        } catch (MalformedJwtException ex) {
            log.error("Token JWT inválido");
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT expirado");
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT no soportado");
        } catch (IllegalArgumentException ex) {
            log.error("Claims JWT vacío");
        }
        return false;
    }
}