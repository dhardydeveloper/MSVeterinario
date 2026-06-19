package cl.duoc.examenes.model;

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
@Table(name = "tipo_examen")
@Schema(description = "Entidad que representa un tipo de examen que puede ser solicitado por un veterinario para una mascota en una atención veterinaria.")
public class TipoExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del tipo de examen", example = "1")
    private Integer idTipoExamen;

    @Column(nullable = false)
    @Schema(description = "Nombre del tipo de examen", example = "Examen de sangre")
    private String nombreExamen;

    @Column(nullable = false)
    @Schema(description = "Descripción del tipo de examen", example = "Examen para detectar anomalías en la sangre")
    private String descripcion;

    @Column(nullable = false)
    @Schema(description = "Precio del tipo de examen", example = "10000.0")
    private Double precio;
}