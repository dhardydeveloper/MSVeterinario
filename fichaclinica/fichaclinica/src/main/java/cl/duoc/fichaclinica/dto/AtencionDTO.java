package cl.duoc.fichaclinica.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtencionDTO {

    private Integer idAtencion; // ID de la atención clínica

    private Integer idCita; // ID de la cita asociada a esta atención
    private Integer idMascota; // ID de la mascota atendida
    private Integer idVeterinario; // ID del veterinario que realizó la atención

    // datos mascota
    private String nombreMascota;
    private String especieMascota;
    private String razaMascota;

    // datos cliente
    private String nombreCliente;
    private String correoCliente;

    // datos veterinario
    private String nombreVeterinario;
    private String especialidadVeterinario;

    //| datos tipo atencion
    private Integer idTipoAtencion;
    private String nombreTipoAtencion;
    private Double precioBase;

    // datos box
    private Integer idBox;
    private String nombreBox;

    // datos cita
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private String estadoCita;

    // datos atencion
    private LocalDate fechaAtencion;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private Double pesoActual;
}