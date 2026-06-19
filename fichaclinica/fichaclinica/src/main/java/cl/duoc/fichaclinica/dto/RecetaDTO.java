package cl.duoc.fichaclinica.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecetaDTO {

    private Integer idReceta;

    private Integer idAtencion; // ID de la atención clínica a la que pertenece esta receta
    private Integer idMascota; // ID de la mascota a la que pertenece esta receta
    private String nombreMascota; // Nombre de la mascota
    private String especieMascota; // Especie de la mascota
    private String razaMascota; // Raza de la mascota

    // datos cliente
    private String nombreCliente;
    private String correoCliente;

    // datos veterinario
    private Integer idVeterinario;
    private String nombreVeterinario;
    private String especialidadVeterinario;

    // datos medicamento
    private Integer idMedicamento;
    private String nombreMedicamento;
    private String descripcionMedicamento;
    private String dosisRecomendada;

    // datos receta
    private LocalDate fechaEmision;
    private String indicaciones;
}