package cl.duoc.veterinario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.veterinario.model.Especialidad;
import cl.duoc.veterinario.service.EspecialidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/especialidades")
@Tag(name = "Especialidades", description = "Endpoints para gestionar las especialidades de los veterinarios")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService; // Inyección de dependencia del servicio de especialidades

    // •   Listar especialidades
    @GetMapping // •  http://localhost:8082/api/v1/especialidades 
    @Operation(summary = "Listar especialidades",
               description = "Retorna una lista de todas las especialidades registradas en el sistema. Si no hay especialidades")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de especialidades retornada exitosamente"),
                           @ApiResponse(responseCode = "204", description = "No hay especialidades registradas"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
               public ResponseEntity<List<Especialidad>> listar() {

        List<Especialidad> lista = especialidadService.listar();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista de especialidades está vacía, indicando que no hay contenido en el cuerpo de la respuesta
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de especialidades en el cuerpo de la respuesta
    }

    // •   Buscar especialidad por ID
    @GetMapping("/{id}") // •  http://localhost:8082/api/v1/especialidades/{id}
    @Operation(summary = "Buscar especialidad por ID",
               description = "Retorna una especialidad específica según su ID. Si no se encuentra, retorna un código de estado 404 Not Found.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Especialidad encontrada exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Especialidad no encontrada"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Especialidad> buscarPorId(@PathVariable ("id") Integer id) {

        try {
            Especialidad especialidad = especialidadService.buscarPorId(id);
            return ResponseEntity.ok(especialidad); // Retorna 200 OK con la especialidad encontrada en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra la especialidad o si ocurre algún error
        }
    }

    // •  Crear especialidad
    @PostMapping // •  http://localhost:8082/api/v1/especialidades
    @Operation(summary = "Crear especialidad",
               description = "Crea una nueva especialidad en el sistema. Retorna la especialidad creada.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Especialidad creada exitosamente"),
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Especialidad> guardar(@RequestBody Especialidad especialidad) {

        Especialidad nuevaEspecialidad = especialidadService.guardar(especialidad); // Llama al servicio para guardar la nueva especialidad y obtiene la especialidad guardada (con ID generado)

        return ResponseEntity.ok(nuevaEspecialidad); // Retorna 200 OK con la nueva especialidad guardada en el cuerpo de la respuesta
    }

    // •   Actualizar especialidad
    @PutMapping("/{id}") // •  http://localhost:8082/api/v1/especialidades/{id}
    @Operation(summary = "Actualizar especialidad",
               description = "Actualiza una especialidad existente en el sistema. Retorna la especialidad actualizada.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Especialidad actualizada exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Especialidad no encontrada"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Especialidad> actualizar(
            @PathVariable ("id") Integer id,
            @RequestBody Especialidad especialidad) {

        try {
            Especialidad especialidadActualizada = especialidadService.actualizar(id, especialidad);
            return ResponseEntity.ok(especialidadActualizada); // Retorna 200 OK con la especialidad actualizada en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra la especialidad a actualizar o si ocurre algún error
        }
    }

    // •   Eliminar especialidad
    @DeleteMapping("/{id}") // •  http://localhost:8082/api/v1/especialidades/{id}
    @Operation(summary = "Eliminar especialidad",
               description = "Elimina una especialidad existente en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Especialidad eliminada exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Especialidad no encontrada"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable ("id") Integer id) {

        try {
            especialidadService.eliminar(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la eliminación fue exitosa, indicando que no hay contenido en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra la especialidad a eliminar o si ocurre algún error
        }
    }
}