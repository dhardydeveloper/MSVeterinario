package cl.duoc.fichaclinica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.fichaclinica.model.Receta;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Integer> { 

    List<Receta> findByIdAtencion(Integer idAtencion); // Método para buscar recetas por ID de atención

    List<Receta> findByIdMascota(Integer idMascota); // Método para buscar recetas por ID de mascota

    List<Receta> findByIdVeterinario(Integer idVeterinario); // Método para buscar recetas por ID de veterinario
}