package cl.duoc.agenda.model;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cita")
@Schema(description = "Objeto que representa una cita veterinaria")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la cita", example = "1")
    private Integer idCita;

    // Este ID pertenece al microservicio Mascota.
    @Column(nullable = false)
    @Schema(description = "Identificador de la mascota", example = "10")
    private Integer idMascota;

    // Este ID pertenece al microservicio Veterinario.
    @Column(nullable = false)
    @Schema(description = "Identificador del veterinario", example = "5")
    private Integer idVeterinario;

   // Relación con Agenda, cada cita pertenece a una agenda específica.
    @ManyToOne
    @JoinColumn(name = "id_agenda", nullable = false) // Clave foránea a Agenda, no nula
    @Schema(description = "Agenda a la que pertenece la cita")
    private Agenda agenda;

    @Column(nullable = false)
    @Schema(description = "Motivo de la consulta", example = "Control general")
    private String motivo;

    @Column(nullable = false)
    @Schema(description = "Fecha de la cita", example = "2026-06-08")
    private LocalDate fecha;

    @Column(nullable = false)
    @Schema(description = "Hora de la cita", example = "09:00")
    private LocalTime hora;

    @Column(nullable = false)
    @Schema(description = "Estado de la cita", example = "Programada")
    private String estado;
}