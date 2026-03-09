package com.pucmm.backend_sensores.repository;

import com.pucmm.backend_sensores.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz Repository para la entidad SensorData.
 * 
 * Al extender la interfaz JpaRepository, Spring Data proporciona
 * automáticamente las
 * implementaciones de todos los métodos CRUD básicos para la tabla
 * "datos_sensor".
 * (save, findAll, findById, delete, etc.) sin necesidad de escribir código SQL
 * manual.
 */
@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

}
