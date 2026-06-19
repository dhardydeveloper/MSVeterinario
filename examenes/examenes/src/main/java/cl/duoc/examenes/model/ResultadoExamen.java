package cl.duoc.examenes.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "resultado_examen")
@Schema(description = "Entidad que representa el resultado de un examen solicitado por un veterinario para una mascota en una atención veterinaria.")
public class ResultadoExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del resultado de examen", example = "1")
    private Integer idResultado;

    @ManyToOne // Relación con OrdenExamen
    @JoinColumn(name = "id_orden_examen", nullable = false) // el @JoinColumn indica que esta entidad tiene una columna id_orden_examen que es una clave foránea a OrdenExamen
    @Schema(description = "id Orden de examen asociada", example = "1")
    private OrdenExamen ordenExamen;

    @Column(nullable = false)
    @Schema(description = "Resultado del examen", example = "Negativo")
    private String resultado;

    @Schema(description = "Observación sobre el resultado del examen", example = "El examen fue negativo para la enfermedad.")
    private String observacion;

    @Column(nullable = false)
    @Schema(description = "Fecha del resultado", example = "2023-01-01")
    private LocalDate fechaResultado;
}