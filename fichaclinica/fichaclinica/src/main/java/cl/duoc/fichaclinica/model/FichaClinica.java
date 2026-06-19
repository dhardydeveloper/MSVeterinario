package cl.duoc.fichaclinica.model;

import java.time.LocalDate;

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
@Table(name = "ficha_clinica")
@Schema(description = "Entidad que representa la ficha clínica de una mascota, incluyendo sus antecedentes, alergias, enfermedades previas y observaciones médicas")
public class FichaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFicha;

    @Schema(description = "ID de la mascota a la que pertenece la ficha clínica")
    @Column(nullable = false)
    private Integer idMascota; // FK a Mascota (desde microservicio Registro)

    @Schema(description = "ID del veterinario responsable de la ficha clínica")
    @Column(nullable = false)
    private String idVeterinario;

    @Schema(description = "Antecedentes médicos de la mascota")
    private String antecedentes;

    @Schema(description = "Alergias de la mascota")
    private String alergias;

    @Schema(description = "Enfermedades previas de la mascota")
    private String enfermedadesPrevias;

    @Schema(description = "Observaciones médicas")
    private String observaciones;

    @Schema(description = "Fecha de creación de la ficha clínica")
    @Column(nullable = false)
    private LocalDate fechaCreacion;
}