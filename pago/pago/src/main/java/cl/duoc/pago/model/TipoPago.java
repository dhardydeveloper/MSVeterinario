package cl.duoc.pago.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tipo_pago")
@Schema(description = "Objeto que representa un tipo de pago en el sistema de atención clínica.")
public class TipoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del tipo de pago, generado automáticamente por la base de datos.", example = "1")
    private Integer idTipoPago;

    @Column(nullable = false)
    @Schema(description = "Nombre del tipo de pago.", example = "EFECTIVO")
    private String nombreTipoPago;

    @Column(nullable = false)
    @Schema(description = "Descripción del tipo de pago.", example = "Pago realizado en efectivo")
    private String descripcion;
}