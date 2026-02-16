package com.imirly.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "anuncio_metadata")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnuncioMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anuncio_id", nullable = false)
    private Anuncio anuncio;

    // Almacenamos todos los campos específicos como JSON
    // En PostgreSQL usaríamos @Column(columnDefinition = "jsonb")
    // En H2 usamos TEXT y parseamos manualmente
    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    // Campos comunes que podrían indexarse (opcional)
    private Boolean servicioDomicilio;
    private Integer anosExperiencia;
    private Boolean disponibilidadUrgente;

    // Método helper para obtener el JSON
    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }
}