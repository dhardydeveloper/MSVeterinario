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
@Table(name = "orden_examen")
@Schema(description = "Entidad que representa una orden de examen solicitada por un veterinario para una mascota en una atención veterinaria.")
public class OrdenExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la orden de examen", example = "1")
    private Integer idOrdenExamen;

    @Column(nullable = false)
    @Schema(description = "ID de la atención veterinaria", example = "1")
    private Integer idAtencion; // Relación con la atención veterinaria.

    @Column(nullable = false)
    @Schema(description = "ID de la mascota", example = "1")
    private Integer idMascota; // Relación con la mascota.

    @Column(nullable = false)
    @Schema(description = "ID del veterinario", example = "1")
    private Integer idVeterinario; // Relación con el veterinario que solicita el examen.

    // Relación con el tipo de examen.
    @ManyToOne
    @JoinColumn(name = "id_tipo_examen", nullable = false)
    private TipoExamen tipoExamen;

    @Column(nullable = false)
    @Schema(description = "Fecha de solicitud", example = "2023-01-01")
    private LocalDate fechaSolicitud;

    @Column(nullable = false)
    @Schema(description = "Estado de la orden", example = "Pendiente")
    private String estado;
}