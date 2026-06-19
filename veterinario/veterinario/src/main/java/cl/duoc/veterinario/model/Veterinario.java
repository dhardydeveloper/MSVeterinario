package cl.duoc.veterinario.model;

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
@Table(name = "veterinario")
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del veterinario", example = "1")
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "RUT del veterinario", example = "12345678-9")
    private String rut;

    @Column(nullable = false)
    @Schema(description = "Nombre del veterinario", example = "Juan")
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Apellido del veterinario", example = "Pérez")
    private String apellido;

    @ManyToOne // Relación ManyToOne con Especialidad, indicando que un veterinario tiene una especialidad
    @JoinColumn(name = "especialidad_id", nullable = false) // Clave foránea a la tabla Especialidad
    @Schema(description = "Especialidad del veterinario")
    private Especialidad especialidad;

}
