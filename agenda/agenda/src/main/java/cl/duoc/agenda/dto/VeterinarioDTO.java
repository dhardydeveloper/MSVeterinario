package cl.duoc.agenda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VeterinarioDTO {

    private Integer id;
    private String nombre;
    private String especialidad;
}