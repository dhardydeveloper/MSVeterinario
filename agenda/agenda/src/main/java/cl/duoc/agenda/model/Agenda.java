package cl.duoc.agenda.model;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "agenda")
@Schema(description = "Objeto que representa una agenda de atención veterinaria")
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la agenda", example = "1")
    private Integer idAgenda;

    // Este ID pertenece al microservicio Veterinario.
    @Column(nullable = false)
    @Schema(description = "Identificador del veterinario asociado a la agenda", example = "5")
    private Integer idVeterinario; 

    @Column(nullable = false)
    @Schema(description = "Fecha de atención de la agenda", example = "2026-06-08")
    private LocalDate fecha;

    @Column(nullable = false)
    @Schema(description = "Hora de inicio", example = "09:00")
    private LocalTime horaInicio;

    @Column(nullable = false)
    @Schema(description = "Hora de término", example = "18:00")
    private LocalTime horaFin;

    @Column(nullable = false)
    @Schema(description = "Estado de la agenda", example = "Disponible")
    private String estado;
}