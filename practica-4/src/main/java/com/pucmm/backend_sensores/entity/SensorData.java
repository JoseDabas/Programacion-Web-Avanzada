package com.pucmm.backend_sensores.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entidad JPA para almacenar los datos recibidos de los sensores.
 * 
 * La anotación @Entity indica que esta clase se mapeará a una tabla en la base
 * de datos.
 * La anotación @Table permite especificar el nombre de la tabla de forma
 * explícita (en este caso "datos_sensor").
 */
@Entity
@Table(name = "datos_sensor")
public class SensorData implements Serializable {

    /**
     * Identificador único del registro (Llave Primaria).
     * 
     * @Id designa esta variable como primary key.
     * @GeneratedValue y strategy = IDENTITY autogenera el ID en PostgreSQL (columna
     *                 tipo serial/identity).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha y hora en que el sensor generó la lectura.
     * Se recibe como String en formato "DD/MM/YYYY HH:mm:ss".
     * @JsonProperty("fechaGeneración") asegura que la librería (Jackson) sepa que
     * en el JSON
     * entrante este campo se llama "fechaGeneración" (con tilde) y lo asocie
     * correctamente.
     */
    @JsonProperty("fechaGeneración")
    @Column(name = "fecha_generacion")
    private String fechaGeneracion;

    /**
     * ID único que identifica al dispositivo (sensor).
     * 
     * @JsonProperty asegura el mapeo exacto del nombre "IdDispositivo" especificado
     *               en el JSON.
     */
    @JsonProperty("IdDispositivo")
    @Column(name = "id_dispositivo")
    private Integer idDispositivo;

    /**
     * Lectura de temperatura del ambiente.
     * Se mapea como Double para abarcar cualquier Number enviado en el JSON.
     */
    @JsonProperty("temperatura")
    @Column(name = "temperatura")
    private Double temperatura;

    /**
     * Lectura de humedad del ambiente.
     * También mapeada como Double.
     */
    @JsonProperty("humedad")
    @Column(name = "humedad")
    private Double humedad;

    // ==========================================
    // Constructores
    // ==========================================

    /**
     * Constructor por defecto vacío.
     * Es estrictamente requerido tanto por JPA/Hibernate para instanciar los
     * objetos
     * desde la base de datos, como por Jackson para poder deserializar desde el
     * JSON.
     */
    public SensorData() {
    }

    public SensorData(String fechaGeneracion, Integer idDispositivo, Double temperatura, Double humedad) {
        this.fechaGeneracion = fechaGeneracion;
        this.idDispositivo = idDispositivo;
        this.temperatura = temperatura;
        this.humedad = humedad;
    }

    // ==========================================
    // Getters y Setters
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Integer getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(Integer idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getHumedad() {
        return humedad;
    }

    public void setHumedad(Double humedad) {
        this.humedad = humedad;
    }

    // ==========================================
    // Métodos útiles para Logs
    // ==========================================

    @Override
    public String toString() {
        return "SensorData{" +
                "id=" + id +
                ", fechaGeneracion='" + fechaGeneracion + '\'' +
                ", idDispositivo=" + idDispositivo +
                ", temperatura=" + temperatura +
                ", humedad=" + humedad +
                '}';
    }
}
