package cl.duoc.registro.model;

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
@Table(name = "Mascota")
@Schema(description = "Objeto que representa a una mascota en el sistema de registro") 
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la mascota")
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "Nombre de la mascota", example = "Firulais")
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Especie de la mascota", example = "Perro")
    private String especie;

    @Column(nullable = false)
    @Schema(description = "Raza de la mascota", example = "Labrador")
    private String raza;

    @Column(nullable = false)
    @Schema(description = "Edad de la mascota", example = "5")
    private Integer edad;

    @Column(nullable = false)
    @Schema(description = "Sexo de la mascota", example = "Macho")
    private String sexo;

    @Column(nullable = false)
    @Schema(description = "Color de la mascota", example = "Café")
    private String color;

    @Column(nullable = false)
    @Schema(description = "Peso de la mascota", example = "25.5")
    private Double peso;

    @Column(name = "numero_chip")
    @Schema(description = "Número de chip de la mascota", example = "CHIP12345")
    private String numeroChip;

    // Relación: Muchas mascotas pueden pertenecer a un cliente.
    @ManyToOne // Relación de muchos a uno con Cliente
    @JoinColumn(name = "cliente_id", nullable = false) // Clave foránea que referencia a Cliente
    private Cliente cliente; 
}