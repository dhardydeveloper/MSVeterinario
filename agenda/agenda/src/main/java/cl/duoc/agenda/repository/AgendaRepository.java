package cl.duoc.agenda.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.duoc.agenda.model.Agenda;

public interface AgendaRepository extends JpaRepository<Agenda, Integer> { 

    List<Agenda> findByIdVeterinario(Integer idVeterinario);

    List<Agenda> findByFecha(LocalDate fecha);

    List<Agenda> findByEstado(String estado);

    // Método para buscar agendas cruzadas
    @Query("""  
        SELECT a FROM Agenda a
        WHERE a.idVeterinario = :idVeterinario
        AND a.fecha = :fecha
        AND (:idExcluir IS NULL OR a.idAgenda <> :idExcluir)
        AND a.horaInicio < :horaFin
        AND a.horaFin > :horaInicio
    """) // Explicación de la consulta:
    // @Query: Indica que este método utiliza una consulta personalizada escrita en JPQL (Java Persistence Query Language).
    // SELECT a FROM Agenda a: Selecciona todas las agendas (a) de la tabla Agenda.
    // WHERE a.idVeterinario = :idVeterinario: Filtra las agendas para que solo se incluyan aquellas que pertenecen al veterinario con el ID especificado.
    // AND a.fecha = :fecha: Filtra las agendas para que solo se incluyan aquellas que ocurren en la fecha especificada.
    // AND (:idExcluir IS NULL OR a.idAgenda <> :idExcluir): Si se proporciona un ID para excluir, filtra las agendas para que no se incluya la agenda con ese ID. Si no se proporciona un ID para excluir (es decir, es NULL), esta condición se ignora.
    // AND a.horaInicio < :horaFin: Filtra las agendas para que solo se incluyan aquellas cuyo inicio es antes de la hora de fin especificada.
    // AND a.horaFin > :horaInicio: Filtra las agendas para que solo se incluyan aquellas cuyo fin es después de la hora de inicio especificada. Esto asegura que se detecten las agendas que se superponen con el rango de tiempo dado.
    
    List<Agenda> buscarAgendasCruzadas( // Método para buscar agendas cruzadas
            @Param("idVeterinario") Integer idVeterinario, // Parámetro para el ID del veterinario
            @Param("fecha") LocalDate fecha, // Parámetro para la fecha de la agenda
            @Param("horaInicio") LocalTime horaInicio, // Parámetro para la hora de inicio del rango de tiempo a verificar
            @Param("horaFin") LocalTime horaFin, // Parámetro para la hora de fin del rango de tiempo a verificar
            @Param("idExcluir") Integer idExcluir // Parámetro para el ID de la agenda a excluir
    );
}