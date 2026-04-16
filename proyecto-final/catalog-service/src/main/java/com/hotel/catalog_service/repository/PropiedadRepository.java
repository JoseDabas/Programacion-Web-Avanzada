package com.hotel.catalog_service.repository;

import com.hotel.catalog_service.model.Propiedad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

// Repositorio de Spring Data MongoDB encargado del acceso a las propiedades
public interface PropiedadRepository extends MongoRepository<Propiedad, String> {

    // Consulta personalizada para aplicar filtros flexibles directos sobre la coleccion
    // Regex ignora mayusculas/minusculas. Si el precio es null, busca menores a 999999
    @Query("{ " +
            "  $and: [ " +
            "    { $or: [ { $expr: { $eq: ['?0', 'null'] } }, { 'ubicacion': { $regex: '?0', $options: 'i' } } ] }, " +
            "    { $or: [ { $expr: { $eq: ['?1', 'null'] } }, { 'tipoHabitacion': { $regex: '?1', $options: 'i' } } ] }, " +
            "    { 'precioPorNoche': { $lte: ?2 } } " +
            "  ] " +
            "}")
    List<Propiedad> searchPropiedades(String ubicacion, String tipoHabitacion, double maxPrecio);
    
    // Spring Data permite una forma mas tradicional tambien basada en metodos
    List<Propiedad> findByUbicacionContainingIgnoreCaseAndTipoHabitacionContainingIgnoreCaseAndPrecioPorNocheLessThanEqual(String ubicacion, String tipoHabitacion, Double precio);
}
