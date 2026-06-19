package cl.duoc.examenes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.examenes.model.TipoExamen;

@Repository
public interface TipoExamenRepository extends JpaRepository<TipoExamen, Integer> { // Repositorio para la entidad TipoExamen

}