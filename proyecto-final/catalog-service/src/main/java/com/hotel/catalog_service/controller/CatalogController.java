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
