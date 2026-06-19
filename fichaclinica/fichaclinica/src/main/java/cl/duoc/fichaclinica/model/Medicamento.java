package cl.duoc.fichaclinica.model;

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
@Table(name = "medicamento")
@Schema(description = "Entidad que representa un medicamento, incluyendo su nombre, descripción y dosis recomendada para su uso en mascotas")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del medicamento")
    private Integer idMedicamento;

    @Column(nullable = false)
    @Schema(description = "Nombre del medicamento")
    private String nombreMedicamento;

    @Column(nullable = false)
    @Schema(description = "Descripción del medicamento")
    private String descripcion;

    @Column(nullable = false)
    @Schema(description = "Dosis recomendada del medicamento")
    private String dosisRecomendada;
}