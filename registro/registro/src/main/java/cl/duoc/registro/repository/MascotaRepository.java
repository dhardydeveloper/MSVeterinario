package cl.duoc.registro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.registro.model.Mascota;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> { // Extiende JpaRepository para proporcionar métodos CRUD básicos

    // Agregar un método para buscar mascotas por el ID del cliente
    List<Mascota> findByClienteId(Integer clienteId);

    // Agregar un método para buscar mascotas por nombre (con búsqueda parcial e insensible a mayúsculas)
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);

    // Agregar un método para buscar por número de chip
    Optional<Mascota> findByNumeroChip(String numeroChip);
}