package com.imirly.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo; // hogar, clases, deporte, etc.

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String imagen;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("nombre")
    @Builder.Default
    private List<Subcategory> subcategories = new ArrayList<>();
}