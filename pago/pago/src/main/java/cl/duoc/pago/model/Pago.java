package cl.duoc.pago.model;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pago")
@Schema(description = "Objeto que representa un pago realizado por un paciente en el sistema de atención clínica.")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del pago, generado automáticamente por la base de datos.", example = "1")    
    private Integer idPago;

    
    //Este ID viene desde Atención Clínica.
    @Column(nullable = false)
    @Schema(description = "Identificador de la atención clínica asociada al pago.", example = "1")
    private Integer idAtencion;

    
    //relación interna porque TipoPago vive dentro del microservicio Pago.
    @ManyToOne
    @JoinColumn(name = "id_tipo_pago", nullable = false)
    @Schema(description = "Tipo de pago asociado.", example = "tipo1")
    private TipoPago tipoPago;

    @Column(nullable = false)
    @Schema(description = "Monto del pago.", example = "15000.0")
    private Double monto;

    @Column(nullable = false)
    @Schema(description = "Fecha del pago.", example = "2026-05-09")
    private LocalDate fechaPago;

    @Column(nullable = false)
    @Schema(description = "Estado del pago.", example = "PAGADO")
    private String estadoPago;
}