package cl.duoc.registro.model;

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

@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@AllArgsConstructor
@NoArgsConstructor
@Entity // Indica que esta clase es una entidad de JPA
@Table(name = "cliente")
@Schema(description = "Objeto que representa a un cliente en el sistema de registro")
public class Cliente {

    @Id // para que sea la clave primaria de la tabla 
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    @Schema(description = "Identificador único del cliente", example = "1")
    private Integer id;

    @Column(nullable = false) // El campo no puede ser nulo
    @Schema(description = "Rol único tributario del cliente", example = "12.345.678-9")
    private String rut;

    @Column(nullable = false)
    @Schema(description = "Nombre del cliente", example = "Juan")
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Apellido del cliente", example = "Pérez")
    private String apellido;

    @Column(nullable = false)
    @Schema(description = "Número telefónico del cliente", example = "987654321")
    private String telefono;

    @Column(nullable = false)
    @Schema(description = "Correo electrónico del cliente", example = "juan@gmail.com")
    private String correo;

    @Column(nullable = false)
    @Schema(description = "Dirección del cliente", example = "Av. Los Leones 123")
    private String direccion;

    @Column(nullable = false)
    @Schema(description = "Comuna del cliente", example = "Providencia")
    private String comuna;

    @Column(nullable = false)
    @Schema(description = "Región del cliente", example = "Metropolitana")
    private String region;
}