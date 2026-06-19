package cl.duoc.fichaclinica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.fichaclinica.model.FichaClinica;

@Repository
public interface FichaClinicaRepository extends JpaRepository<FichaClinica, Integer> {

    List<FichaClinica> findByIdMascota(Integer idMascota); // Método para buscar fichas clínicas por ID de mascota
}