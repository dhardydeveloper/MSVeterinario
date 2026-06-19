package cl.duoc.agenda.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaDTO {

    private Integer idCita;
    private Integer idMascota;
    private Integer idVeterinario;
    private Integer idAgenda;

    private String motivo;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;

    private String nombreMascota;
    private String especieMascota;
    private String razaMascota;

    private String nombreCliente;
    private String correoCliente;

    private String nombreVeterinario;
    private String especialidadVeterinario;
}