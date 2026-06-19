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
@Table(name = "box")
@Schema(description = "Objeto que representa un box de atención clínica")
public class Box {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del box", example = "1")
    private Integer idBox;

    @Column(nullable = false)
    @Schema(description = "Nombre del box de atención", example = "Box 1")
    private String nombreBox;

    @Column(nullable = false)
    @Schema(description = "Descripción del box", example = "Box destinado a consultas generales")
    private String descripcion;

    @Column(nullable = false)
    @Schema(description = "Estado del box", example = "Disponible")
    private String estado;
}