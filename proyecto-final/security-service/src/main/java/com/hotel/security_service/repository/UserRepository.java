package com.hotel.security_service.repository;

import com.hotel.security_service.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

// Repositorio de Spring Data MongoDB para realizar operaciones CRUD sobre la coleccion de usuarios
public interface UserRepository extends MongoRepository<User, String> {
    
    // Metodo personalizado para buscar a un usuario por su correo electronico
    Optional<User> findByEmail(String email);
}
