package cl.duoc.examenes.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtencionDTO {

    private Integer idAtencion; // ID de la atención, se asigna al crear la atención

    // Datos de la cita
    private Integer idCita;
    private Integer idMascota;
    private Integer idVeterinario;

    // Datos de la mascota
    private String nombreMascota;
    private String especieMascota;
    private String razaMascota;

    // Datos del cliente
    private String nombreCliente;
    private String correoCliente;

    // Datos del veterinario
    private String nombreVeterinario;
    private String especialidadVeterinario;

    // Datos del tipo de atención
    private Integer idTipoAtencion;
    private String nombreTipoAtencion;
    private Double precioBase;

    // Datos del box
    private Integer idBox;
    private String nombreBox;

    // Datos de la atención
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private String estadoCita;

    // Datos adicionales de la atención
    private LocalDate fechaAtencion;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private Double pesoActual;
}