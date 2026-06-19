package cl.duoc.atencionClinica.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.atencionClinica.model.Atencion;

@Repository
public interface AtencionRepository extends JpaRepository<Atencion, Integer> { // Repositorio para la entidad Atencion

    
    List<Atencion> findByIdCita(Integer idCita); // Buscar atenciones por ID de cita

    List<Atencion> findByIdMascota(Integer idMascota); // Buscar atenciones por ID de mascota

    List<Atencion> findByIdVeterinario(Integer idVeterinario); // Buscar atenciones por ID de veterinario

    List<Atencion> findByFechaAtencion(LocalDate fechaAtencion); // Buscar atenciones por fecha de atención
}