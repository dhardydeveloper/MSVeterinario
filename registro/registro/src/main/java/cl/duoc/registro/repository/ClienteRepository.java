package cl.duoc.registro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.registro.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> { // Extiende JpaRepository para proporcionar métodos CRUD básicos

    // Agregar un método para buscar por RUT
    Optional<Cliente> findByRut(String rut);
    
    // Agregar un método para buscar por correo electrónico
    Optional<Cliente> findByCorreo(String correo);
}