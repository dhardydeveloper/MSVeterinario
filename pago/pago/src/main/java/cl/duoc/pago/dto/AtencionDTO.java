package cl.duoc.pago.dto;

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

    private Integer idCita;
    private Integer idMascota;
    private Integer idVeterinario;

    private String nombreMascota;
    private String especieMascota;
    private String razaMascota;

    private String nombreCliente;
    private String correoCliente;

    private String nombreVeterinario;
    private String especialidadVeterinario;

    private Integer idTipoAtencion;
    private String nombreTipoAtencion;
    private Double precioBase;

    private Integer idBox;
    private String nombreBox;

    private LocalDate fechaCita;
    private LocalTime horaCita;
    private String estadoCita;

    private LocalDate fechaAtencion;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private Double pesoActual;
}