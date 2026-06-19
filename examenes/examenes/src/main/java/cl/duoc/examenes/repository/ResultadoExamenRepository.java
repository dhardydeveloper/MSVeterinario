package cl.duoc.examenes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.examenes.model.ResultadoExamen;

@Repository
public interface ResultadoExamenRepository extends JpaRepository<ResultadoExamen, Integer> { // Repositorio para la entidad ResultadoExamen

    List<ResultadoExamen> findByOrdenExamenIdOrdenExamen(Integer idOrdenExamen); // Método para buscar por idOrdenExamen
}