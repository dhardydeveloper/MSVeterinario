package cl.duoc.usuario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rol")
@Schema(description = "Entidad que representa un rol de usuario en el sistema")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del rol", example = "1")
    private Integer idRol;

    @Column(nullable = false)
    @Schema(description = "Nombre del rol", example = "Administrador")
    private String nombreRol;

    @Column(nullable = false)
    @Schema(description = "Descripción del rol", example = "Rol con permisos de administración")
    private String descripcion;
}