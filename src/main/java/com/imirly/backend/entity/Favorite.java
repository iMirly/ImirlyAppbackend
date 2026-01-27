package com.imirly.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "anuncio_id"})
)
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "anuncio_id", nullable = false)
    private Anuncio anuncio;
}
