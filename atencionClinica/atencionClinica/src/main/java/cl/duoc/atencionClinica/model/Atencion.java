package cl.duoc.atencionClinica.model;

import java.time.LocalDate;

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
@Table(name = "atencion")
@Schema(description = "Objeto que representa una atención clínica veterinaria")
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la atención clínica", example = "1")
    private Integer idAtencion;

    // Estas relaciones son externas al microservicio 
    @Column(nullable = false)
    @Schema(description = "Identificador de la cita asociada", example = "10")
    private Integer idCita; // Relación con Cita

    @Column(nullable = false)
    @Schema(description = "Identificador de la mascota atendida", example = "15")
    private Integer idMascota; // Relación con Mascota

    @Column(nullable = false)
    @Schema(description = "Identificador del veterinario responsable", example = "20")
    private Integer idVeterinario; // Relación con Veterinario

    // Relaciones internas al microservicio
    @ManyToOne
    @JoinColumn(name = "id_tipo_atencion", nullable = false) // Relación con TipoAtencion
    @Schema(description = "Tipo de atención realizada")
    private TipoAtencion tipoAtencion;

    @ManyToOne
    @JoinColumn(name = "id_box", nullable = false) // Relación con Box
    @Schema(description = "Box donde se realizó la atención")
    private Box box;

    @Column(nullable = false)
    @Schema(description = "Fecha de la atención clínica", example = "2026-06-08")
    private LocalDate fechaAtencion;

    @Column(nullable = false)
    @Schema(description = "Diagnóstico realizado por el veterinario", example = "Otitis leve")
    private String diagnostico;

    @Column(nullable = false)
    @Schema(description = "Tratamiento indicado para la mascota", example = "Aplicación de gotas óticas durante 7 días")
    private String tratamiento;

    @Schema(description = "Observaciones adicionales de la atención", example = "Control en 15 días")
    private String observaciones;

    @Schema(description = "Peso actual de la mascota en kilogramos", example = "12.5")
    private Double pesoActual;
}