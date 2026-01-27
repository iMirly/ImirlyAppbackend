package com.imirly.backend.dto;

import com.imirly.backend.entity.Anuncio;
import com.imirly.backend.entity.PriceType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public class AnuncioDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private PriceType priceType;
    private Double price;
    private String comunidadAutonoma;
    private String provincia;
    private String localidad;
    private LocalTime horaInicio;  // CORREGIDO: era "horalnicio"
    private LocalTime horaFin;
    private Set<String> diasDisponibles;
    private String detallesJson;
    private Boolean activo;        // CORREGIDO: era "active"
    private LocalDateTime createdAt; // CORREGIDO: era "createAlt"
    private Long subcategoryId;
    private String subcategoryName;
    private Long userId;

    // Constructor vacío
    public AnuncioDTO() {}

    // Constructor desde entidad (CORREGIDO)
    public AnuncioDTO(Anuncio anuncio) {
        this.id = anuncio.getId();
        this.titulo = anuncio.getTitulo();
        this.descripcion = anuncio.getDescripcion();
        this.priceType = anuncio.getPriceType();
        this.price = anuncio.getPrice();
        this.comunidadAutonoma = anuncio.getComunidadAutonoma();
        this.provincia = anuncio.getProvincia();
        this.localidad = anuncio.getLocalidad();
        this.horaInicio = anuncio.getHoraInicio();     // CORREGIDO
        this.horaFin = anuncio.getHoraFin();
        this.diasDisponibles = anuncio.getDiasDisponibles();
        this.detallesJson = anuncio.getDetallesJson();
        this.activo = anuncio.getActivo();             // CORREGIDO
        this.createdAt = anuncio.getCreatedAt();       // CORREGIDO

        // Asegúrate de que getSubcategory() no sea null
        if (anuncio.getSubcategory() != null) {
            this.subcategoryId = anuncio.getSubcategory().getId();
            this.subcategoryName = anuncio.getSubcategory().getName(); // CORREGIDO: getName() no getId()
        }

        // Asegúrate de que getUser() no sea null
        if (anuncio.getUser() != null) {
            this.userId = anuncio.getUser().getId();
        }
    }

    // Getters y setters para todos los campos
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

    public Long getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}