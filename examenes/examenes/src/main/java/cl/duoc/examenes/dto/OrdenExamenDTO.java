package cl.duoc.examenes.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenExamenDTO {

    private Integer idOrdenExamen; // ID de la orden de examen, se asigna al crear la orden

    // Datos de la atención asociada a la orden de examen
    private Integer idAtencion;
    private Integer idMascota;
    private String nombreMascota;
    private String especieMascota;
    private String razaMascota;

    // Datos del cliente asociado a la atención
    private String nombreCliente;
    private String correoCliente;

    // Datos del veterinario asociado a la atención
    private Integer idVeterinario;
    private String nombreVeterinario;
    private String especialidadVeterinario;

    // Datos del tipo de examen asociado a la orden de examen
    private Integer idTipoExamen;
    private String nombreExamen;
    private String descripcionExamen;
    private Double precioExamen;

    // Datos de la orden de examen
    private LocalDate fechaSolicitud;
    private String estado;
}