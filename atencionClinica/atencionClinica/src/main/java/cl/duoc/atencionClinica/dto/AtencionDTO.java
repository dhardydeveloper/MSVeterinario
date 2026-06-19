package cl.duoc.atencionClinica.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtencionDTO {

    private Integer idAtencion; 

    // Atributos relacionados con la atención
    private Integer idCita; // Relacion con CitaDTO
    private Integer idMascota; // Relacion con MascotaDTO
    private Integer idVeterinario; // Relacion con VeterinarioDTO

    // DTO mascota
    private String nombreMascota;
    private String especieMascota;
    private String razaMascota;

    // DTO cliente
    private String nombreCliente;
    private String correoCliente;

    // DTO veterinario
    private String nombreVeterinario;
    private String especialidadVeterinario;

    // DTO tipo atencion
    private Integer idTipoAtencion;
    private String nombreTipoAtencion;
    private Double precioBase;

    // DTO box
    private Integer idBox;
    private String nombreBox;

    // DTO cita
    private LocalDate fechaCita; 
    private LocalTime horaCita; 
    private String estadoCita;

    // DTO atención
    private LocalDate fechaAtencion; 
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private Double pesoActual;
}