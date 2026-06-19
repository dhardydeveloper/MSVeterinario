package cl.duoc.atencionClinica.model;

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
@Table(name = "tipo_atencion")
@Schema(description = "Objeto que representa un tipo de atención clínica veterinaria")
public class TipoAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del tipo de atención", example = "1")
    private Integer idTipoAtencion;

    @Column(nullable = false)
    @Schema(description = "Nombre del tipo de atención", example = "Consulta General")
    private String nombreTipo;

    @Column(nullable = false)
    @Schema(description = "Descripción del tipo de atención", example = "Evaluación médica general de la mascota")
    private String descripcion;

    @Column(nullable = false)
    @Schema(description = "Precio base de la atención", example = "25000")
    private Double precioBase;
}