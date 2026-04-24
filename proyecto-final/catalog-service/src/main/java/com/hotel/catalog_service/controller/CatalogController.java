package com.hotel.catalog_service.controller;

import com.hotel.catalog_service.model.Propiedad;
import com.hotel.catalog_service.repository.PropiedadRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST de consultas directas de propiedades
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final PropiedadRepository propiedadRepository;

    public CatalogController(PropiedadRepository propiedadRepository) {
        this.propiedadRepository = propiedadRepository;
    }

    // Endpoint principal para leer todo el inventario de hoteles y propiedades
    @GetMapping
    public List<Propiedad> listarTodas() {
        return propiedadRepository.findAll();
    }

    @GetMapping("/{id}")
    public Propiedad obtenerPropiedad(@PathVariable String id) {
        return propiedadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
    }

    @PostMapping
    public Propiedad crearPropiedad(@RequestBody Propiedad propiedad) {
        return propiedadRepository.save(propiedad);
    }

    @PutMapping("/{id}")
    public Propiedad actualizarPropiedad(@PathVariable String id, @RequestBody Propiedad propiedadDetails) {
        return propiedadRepository.findById(id).map(prop -> {
            prop.setNombre(propiedadDetails.getNombre());
            prop.setDescripcion(propiedadDetails.getDescripcion());
            prop.setUbicacion(propiedadDetails.getUbicacion());
            prop.setTipoHabitacion(propiedadDetails.getTipoHabitacion());
            prop.setTipoPropiedad(propiedadDetails.getTipoPropiedad());
            prop.setPrecioPorNoche(propiedadDetails.getPrecioPorNoche());
            prop.setImagenes(propiedadDetails.getImagenes());
            prop.setAmenidades(propiedadDetails.getAmenidades());
            return propiedadRepository.save(prop);
        }).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
    }

    @DeleteMapping("/{id}")
    public void eliminarPropiedad(@PathVariable String id) {
        propiedadRepository.deleteById(id);
    }

    // Endpoint que permite realizar un filtrado avanzado para el modulo de busquedas
    @GetMapping("/search")
    public List<Propiedad> buscarPropiedades(
            @RequestParam(required = false, defaultValue = "") String ubicacion,
            @RequestParam(required = false, defaultValue = "") String tipoHabitacion,
            @RequestParam(required = false, defaultValue = "999999") Double precioMaximo,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin
    ) {
        // En esta capa ejecutamos el filtrado basado en ubicacion, tipo de habitacion y el umbral de precio maximo
        return propiedadRepository.findByUbicacionContainingIgnoreCaseAndTipoHabitacionContainingIgnoreCaseAndPrecioPorNocheLessThanEqual(
                ubicacion, tipoHabitacion, precioMaximo);
    }
}
