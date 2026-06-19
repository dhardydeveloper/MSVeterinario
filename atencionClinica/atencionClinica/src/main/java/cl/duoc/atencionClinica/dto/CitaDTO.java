package cl.duoc.atencionClinica.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaDTO {

    // Atributos de la cita
    private Integer idCita;
    private Integer idMascota;
    private Integer idVeterinario;
    private Integer idAgenda;

    // DTO agenda
    private String motivo;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;

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
}