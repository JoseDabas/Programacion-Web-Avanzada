package com.webavanzada.pucmm.repositorios;

import com.webavanzada.pucmm.entidades.MockEndpoint;
import com.webavanzada.pucmm.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MockEndpointRepository extends JpaRepository<MockEndpoint, Long> {
    // Punto 3: Un usuario no debe ver lo de otro
    List<MockEndpoint> findAllByUsuario(Usuario usuario);

    // Para el motor de renderizado
    MockEndpoint findByRutaAndMetodo(String ruta, String metodo);
}