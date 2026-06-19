package cl.duoc.fichaclinica.model;

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
@Table(name = "receta")
@Schema(description = "Entidad que representa una receta médica, incluyendo el medicamento prescrito, las indicaciones de uso y la fecha de emisión, asociada a una atención clínica específica y a una mascota en particular")
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la receta")
    private Integer idReceta;

    
    @Column(nullable = false)
    @Schema(description = "ID de la atención clínica asociada")
    private Integer idAtencion; // FK a Atención Clínica

    
    @Column(nullable = false)
    @Schema(description = "ID de la mascota asociada")
    private Integer idMascota; // FK a Mascota (desde microservicio Registro)

    @Column(nullable = false)
    @Schema(description = "ID del veterinario responsable")
    private Integer idVeterinario; // FK a Veterinario (desde microservicio Registro)

    
    @ManyToOne // Relación con Medicamento
    @JoinColumn(name = "id_medicamento", nullable = false) // FK a Medicamento, no nulo
    private Medicamento medicamento;

    @Column(nullable = false)
    @Schema(description = "Fecha de emisión de la receta")
    private LocalDate fechaEmision;

    @Column(nullable = false)
    @Schema(description = "Indicaciones de uso del medicamento")
    private String indicaciones;
}