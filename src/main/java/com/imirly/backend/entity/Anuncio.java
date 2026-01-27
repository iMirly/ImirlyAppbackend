package com.imirly.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "anuncios")
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceType priceType;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String comunidadAutonoma;

    @Column(nullable = false)
    private String provincia;

    private String localidad;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    @ElementCollection
    @CollectionTable(name = "anuncio_dias", joinColumns = @JoinColumn(name = "anuncio_id"))
    @Column(name = "dia")
    private Set<String> diasDisponibles;

    @Column(columnDefinition = "TEXT")
    private String detallesJson;

    private Boolean activo = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIncludeProperties({"id", "nombre"}) // Solo incluye id y nombre del usuario
    private User user;

    @ManyToOne
    @JoinColumn(name = "subcategory_id", nullable = false)
    @JsonIncludeProperties({"id", "name"}) // Solo incluye id y name de la subcategoría
    private Subcategory subcategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getComunidadAutonoma() {
        return comunidadAutonoma;
    }

    public void setComunidadAutonoma(String comunidadAutonoma) {
        this.comunidadAutonoma = comunidadAutonoma;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Set<String> getDiasDisponibles() {
        return diasDisponibles;
    }

    public void setDiasDisponibles(Set<String> diasDisponibles) {
        this.diasDisponibles = diasDisponibles;
    }

    public String getDetallesJson() {
        return detallesJson;
    }

    public void setDetallesJson(String detallesJson) {
        this.detallesJson = detallesJson;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }
}