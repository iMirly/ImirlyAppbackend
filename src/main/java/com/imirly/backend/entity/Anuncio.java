package com.imirly.backend.entity;

import com.imirly.backend.entity.enums.AnuncioStatus;
import com.imirly.backend.entity.enums.PriceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "anuncios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PriceType tipoPrecio;

    @Column(length = 200)
    private String ubicacion;

    @Column(length = 500)
    private String imagenPrincipalUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AnuncioStatus status = AnuncioStatus.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private User propietario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id", nullable = false)
    private Subcategory subcategory;

    // Metadatos específicos de la subcategoría (JSONB en PostgreSQL, TEXT en H2)
    @OneToOne(mappedBy = "anuncio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AnuncioMetadata metadata;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "anuncio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    // Contador de favoritos (denormalizado para performance)
    @Builder.Default
    private Integer favoritesCount = 0;

    // Métodos de negocio
    public void publicar() {
        if (status != AnuncioStatus.BORRADOR) {
            throw new IllegalStateException("Solo se pueden publicar anuncios en estado BORRADOR");
        }
        this.status = AnuncioStatus.PUBLICADO;
        this.publishedAt = LocalDateTime.now();
    }

    // En Anuncio.java
    public void republicar() {
        if (this.status != AnuncioStatus.DESPUBLICADO) {
            throw new IllegalStateException("Solo se pueden republicar anuncios despublicados");
        }
        this.status = AnuncioStatus.PUBLICADO;
        // No actualizamos publishedAt para mantener la fecha original
    }

    public void despublicar() {
        if (status == AnuncioStatus.PUBLICADO) {
            this.status = AnuncioStatus.DESPUBLICADO;
        }
    }

    public void incrementFavoritesCount() {
        this.favoritesCount++;
    }

    public void decrementFavoritesCount() {
        if (this.favoritesCount > 0) {
            this.favoritesCount--;
        }
    }

    public void eliminar() {
        this.status = AnuncioStatus.ELIMINADO;
    }

    public boolean isActivo() {
        return status == AnuncioStatus.PUBLICADO;
    }

    public boolean isEditable() {
        return status == AnuncioStatus.BORRADOR || status == AnuncioStatus.DESPUBLICADO;
    }
}