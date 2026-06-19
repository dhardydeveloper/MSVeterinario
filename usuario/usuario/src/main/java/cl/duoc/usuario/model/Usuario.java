package cl.duoc.usuario.model;

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

@Data // generara automáticamente los métodos getters, setters, toString, equals y hashCode
@AllArgsConstructor 
@NoArgsConstructor
@Entity // indica que esta clase es una entidad de JPA y se mapeará a una tabla en la base de datos
@Table(name = "usuario") 
@Schema(description = "Entidad que representa un usuario del sistema, con sus datos personales y su rol asociado") 
public class Usuario {

    @Id // primary key de la tabla usuario
    @GeneratedValue(strategy = GenerationType.IDENTITY) // @GeneratedValue para que se genere automáticamente el id / strategy = GenerationType.IDENTITY para que se genere un valor único incremental
    @Schema(description = "Identificador único del usuario", example = "1")
    private Integer idUsuario; 

    @Column(nullable = false) // @Column para definir las columnas de la tabla / nullable = false para que no acepte valores nulos
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String apellido;

    @Column(nullable = false, unique = true)
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@ejemplo.com")
    private String correo;

    @Column(nullable = false)
    @Schema(description = "Contraseña del usuario", example = "********")
    private String password;

    @Column(nullable = false)
    @Schema(description = "Estado del usuario (activo/inactivo)", example = "true")
    private Boolean estado; // Boolean para representar el estado del usuario (activo/inactivo)

    

    @ManyToOne // @ManyToOne para indicar la relación de muchos a uno entre Usuario y Rol
    @JoinColumn(name = "id_rol", nullable = false) // @JoinColumn para especificar la columna de la tabla usuario que se usará para la relación con la tabla rol / name = "id_rol" para indicar el nombre de la columna / nullable = false para que no acepte valores nulos
    private Rol rol;
}