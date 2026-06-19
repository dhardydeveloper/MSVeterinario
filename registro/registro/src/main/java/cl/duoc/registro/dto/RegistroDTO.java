package cl.duoc.registro.dto; 

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroDTO { // DTO para combinar datos de Cliente y Mascota en una sola respuesta

    // Datos del cliente / dueño
    private Integer idCliente;
    private String rutCliente;
    private String nombreCliente;
    private String apellidoCliente;
    private String telefonoCliente;
    private String correoCliente;
    private String direccionCliente;
    private String comunaCliente;
    private String regionCliente;

    // Datos de la mascota
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