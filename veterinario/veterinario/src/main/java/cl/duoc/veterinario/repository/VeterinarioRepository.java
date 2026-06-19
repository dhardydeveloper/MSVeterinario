package cl.duoc.veterinario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.veterinario.model.Veterinario;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Integer> { // se extiende de JpaRepository para heredar métodos CRUD

    // Método personalizado para buscar un veterinario por su RUT
    Optional<Veterinario> findByRut(String rut);
}
