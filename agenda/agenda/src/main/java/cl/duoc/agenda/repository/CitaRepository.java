package cl.duoc.agenda.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.agenda.model.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    // Métodos de consulta personalizados para Cita
    List<Cita> findByIdMascota(Integer idMascota); // Método para buscar citas por ID de mascota

    // Métodos de consulta personalizados para Cita
    List<Cita> findByIdVeterinario(Integer idVeterinario); // Método para buscar citas por ID de veterinario

    // Métodos de consulta personalizados para Cita
    List<Cita> findByFecha(LocalDate fecha); // Método para buscar citas por fecha

    // Métodos de consulta personalizados para Cita
    List<Cita> findByEstado(String estado); // Método para buscar citas por estado

    // Métodos de consulta personalizados para Cita
    List<Cita> findByAgendaIdAgenda(Integer idAgenda); // Método para buscar citas por ID de agenda
}