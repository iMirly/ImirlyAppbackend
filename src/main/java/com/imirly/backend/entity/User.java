package com.imirly.backend.entity;

import com.imirly.backend.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    // Campos opcionales inicialmente, obligatorios para publicar
    @Column(length = 20)
    private String telefono;

    private LocalDate fechaNacimiento;

    @Column(length = 200)
    private String direccionCalle;

    @Column(length = 100)
    private String direccionCiudad;

    @Column(length = 10)
    private String direccionCodigoPostal;

    @Column(length = 500)
    private String fotoPerfilUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Anuncio> anuncios = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    // Método de utilidad para verificar si puede publicar
    public boolean puedePublicarAnuncio() {
        return telefono != null && !telefono.isBlank() &&
                fechaNacimiento != null &&
                direccionCalle != null && !direccionCalle.isBlank() &&
                direccionCiudad != null && !direccionCiudad.isBlank() &&
                direccionCodigoPostal != null && !direccionCodigoPostal.isBlank();
    }

    public boolean tieneDatosCompletos() {
        return puedePublicarAnuncio();
    }
}