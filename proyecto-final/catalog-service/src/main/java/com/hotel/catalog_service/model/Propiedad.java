package com.hotel.catalog_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

// Modelo raiz del catalogo: Representa una habitacion u hotel disponible para los turistas
@Document(collection = "propiedades")
public class Propiedad {

    @Id
    private String id; // Identificador en MongoDB
    private String nombre; // Nombre del hotel o la propiedad
    private String descripcion; // Descripcion detallada
    private List<String> amenidades; // Lista de ventajas (WiFi, Piscina, etc)
    private List<String> imagenes; // Galería de URLs de fotos
    private Double precioPorNoche; // Tarifa
    private String ubicacion; // Ciudad o barrio
    private String tipoHabitacion; // Ej: Suite, Sencilla, Presidencial
    private String tipoPropiedad; // Ej: Hotel, Resort, Apartamento

    public Propiedad() {
    }

    public Propiedad(String nombre, String descripcion, List<String> amenidades, List<String> imagenes, Double precioPorNoche, String ubicacion, String tipoHabitacion, String tipoPropiedad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.amenidades = amenidades;
        this.imagenes = imagenes;
        this.precioPorNoche = precioPorNoche;
        this.ubicacion = ubicacion;
        this.tipoHabitacion = tipoHabitacion;
        this.tipoPropiedad = tipoPropiedad;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<String> getAmenidades() { return amenidades; }
    public void setAmenidades(List<String> amenidades) { this.amenidades = amenidades; }
    public List<String> getImagenes() { return imagenes; }
    public void setImagenes(List<String> imagenes) { this.imagenes = imagenes; }
    public Double getPrecioPorNoche() { return precioPorNoche; }
    public void setPrecioPorNoche(Double precioPorNoche) { this.precioPorNoche = precioPorNoche; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getTipoHabitacion() { return tipoHabitacion; }
    public void setTipoHabitacion(String tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }
    public String getTipoPropiedad() { return tipoPropiedad; }
    public void setTipoPropiedad(String tipoPropiedad) { this.tipoPropiedad = tipoPropiedad; }
}
