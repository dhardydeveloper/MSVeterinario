package cl.duoc.atencionClinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.atencionClinica.model.Box;

@Repository
public interface BoxRepository extends JpaRepository<Box, Integer> { // Repositorio para la entidad Box

}