package cl.duoc.examenes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.examenes.model.TipoExamen;
import cl.duoc.examenes.service.TipoExamenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/tipo-examenes")
@Tag(name = "Tipo Examen", description = "Controlador para gestionar los tipos de examen")
public class TipoExamenController {

    @Autowired
    private TipoExamenService tipoExamenService; // Inyección de dependencia del servicio

    

    // •   Listar tipos de examenes
    @GetMapping // •   http://localhost:8085/api/v1/tipo-examenes
    @Operation(summary = "Listar tipos de examen", 
        description = "Obtiene una lista de todos los tipos de examen registrados en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Tipos de examen encontrados"), 
                           @ApiResponse(responseCode = "204", description = "No hay Tipos de examen para la orden especificada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<TipoExamen>> listar() {

        List<TipoExamen> lista = tipoExamenService.listar(); 

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de tipos de examen encontrados
    }

    // •   Buscar tipo de examen por ID
    @GetMapping("/{id}") // •   http://localhost:8085/api/v1/tipo-examenes/{id}
    @Operation(summary = "Buscar tipo de examen por ID", 
        description = "Obtiene un tipo de examen específico utilizando su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Tipo de examen encontrado"), 
                           @ApiResponse(responseCode = "404", description = "Tipo de examen no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<TipoExamen> buscarPorId(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(tipoExamenService.buscarPorId(id)); // Retorna 200 OK con el tipo de examen encontrado
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra el tipo de examen
        }
    }
    
    // •   Crear tipo examen
    @PostMapping // •   http://localhost:8085/api/v1/tipo-examenes
    @Operation(summary = "Crear tipo de examen", 
        description = "Crea un nuevo tipo de examen y lo retorna.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Tipo de examen creado"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<TipoExamen> guardar(@RequestBody TipoExamen tipoExamen) {

        try {
            TipoExamen nuevo = tipoExamenService.guardar(tipoExamen);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo); // Retorna 201 Created con el nuevo tipo de examen creado
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hay un error al crear el tipo de examen
        }
    }

    // •   Actualizar tipo de examen 
    @PutMapping("/{id}") // •   http://localhost:8085/api/v1/tipo-examenes/{id}
    @Operation(summary = "Actualizar tipo de examen", 
        description = "Actualiza un tipo de examen existente y lo retorna.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Tipo de examen actualizado"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "404", description = "Tipo de examen no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<TipoExamen> actualizar(
            @PathVariable Integer id,
            @RequestBody TipoExamen tipoExamen) {

        try {
            return ResponseEntity.ok(tipoExamenService.actualizar(id, tipoExamen)); // Retorna 200 OK con el tipo de examen actualizado
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hay un error al actualizar el tipo de examen
        }
    }

    // •   Eliminar un tipo de examen
    @DeleteMapping("/{id}") // •   http://localhost:8085/api/v1/tipo-examenes/{id}
    @Operation(summary = "Eliminar tipo de examen", 
        description = "Elimina un tipo de examen existente utilizando su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Tipo de examen eliminado"), 
                           @ApiResponse(responseCode = "404", description = "Tipo de examen no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            tipoExamenService.eliminar(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si el tipo de examen se eliminó correctamente
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra el tipo de examen para eliminar
        }
    }
}