package cl.duoc.atencionClinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.atencionClinica.model.TipoAtencion;

@Repository
public interface TipoAtencionRepository extends JpaRepository<TipoAtencion, Integer> { // Repositorio para la entidad TipoAtencion

}