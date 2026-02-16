package com.imirly.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subcategories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String codigo; // fontaneria, limpieza, etc.

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String imagen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // JSON Schema o configuración de campos (opcional, para validación dinámica)
    @Column(columnDefinition = "TEXT")
    private String formConfigJson;
}