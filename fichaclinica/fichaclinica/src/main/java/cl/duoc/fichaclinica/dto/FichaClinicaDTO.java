package cl.duoc.fichaclinica.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FichaClinicaDTO {

    private Integer idFicha; // ID de la ficha clínica

    private Integer idMascota; // ID de la mascota a la que pertenece esta ficha clínica

    // datos de la ficha clínica
    private String antecedentes;
    private String alergias;
    private String enfermedadesPrevias;
    private String observaciones;
    private LocalDate fechaCreacion; // Fecha de creación de la ficha clínica
}