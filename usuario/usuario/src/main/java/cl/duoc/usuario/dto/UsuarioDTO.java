package cl.duoc.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {

    // Campos del DTO para representar la información del usuario junto con el nombre del rol
    private Integer idUsuario; 

    private String nombre;
    private String apellido;
    private String correo;
    private Boolean estado; // si es estado activo es true, si es inactivo es false

    private Integer idRol;
    private String nombreRol;
}