package cl.duoc.agenda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroDTO {

    private Integer idCliente;
    private String rutCliente;
    private String nombreCliente;
    private String apellidoCliente;
    private String telefonoCliente;
    private String correoCliente;
    private String direccionCliente;
    private String comunaCliente;
    private String regionCliente;

    private Integer idMascota;
    private String nombreMascota;
    private String especieMascota;
    private String razaMascota;
    private Integer edadMascota;
    private String sexoMascota;
    private String colorMascota;
    private Double pesoMascota;
    private String numeroChipMascota;
}