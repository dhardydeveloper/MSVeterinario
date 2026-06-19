package cl.duoc.pago.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoDTO {

    private Integer idPago;

    private Integer idAtencion;
    private Integer idMascota;
    private String nombreMascota;
    private String nombreCliente;
    private String correoCliente;

    private Integer idVeterinario;
    private String nombreVeterinario;

    private String nombreTipoAtencion;
    private Double precioBase;

    private Integer idTipoPago;
    private String nombreTipoPago;

    private Double monto;
    private LocalDate fechaPago;
    private String estadoPago;
}