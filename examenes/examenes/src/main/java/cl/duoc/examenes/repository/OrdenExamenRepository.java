package cl.duoc.examenes.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.examenes.model.OrdenExamen;

@Repository
public interface OrdenExamenRepository extends JpaRepository<OrdenExamen, Integer> { // Repositorio para la entidad OrdenExamen

    
    List<OrdenExamen> findByIdAtencion(Integer idAtencion); // Método para buscar por idAtencion

    List<OrdenExamen> findByIdMascota(Integer idMascota); // Método para buscar por idMascota

    List<OrdenExamen> findByIdVeterinario(Integer idVeterinario); // Método para buscar por idVeterinario

    List<OrdenExamen> findByEstado(String estado); // Método para buscar por estado

    List<OrdenExamen> findByFechaSolicitud(LocalDate fechaSolicitud); // Método para buscar por fechaSolicitud
}