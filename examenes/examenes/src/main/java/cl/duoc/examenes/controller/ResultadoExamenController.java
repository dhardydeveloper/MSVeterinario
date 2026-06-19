package cl.duoc.examenes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.examenes.model.ResultadoExamen;
import cl.duoc.examenes.service.ResultadoExamenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/resultados-examen")
@Tag(name = "Resultado Examen", description = "Controlador para gestionar los resultados de examen")
public class ResultadoExamenController {

    @Autowired
    private ResultadoExamenService resultadoExamenService; // Inyección de dependencia del servicio



    // •   Listar resultados de examenes
    @GetMapping // •   http://localhost:8085/api/v1/resultados-examen
    @Operation(summary = "Listar resultados de examen", 
        description = "Obtiene una lista de todos los resultados de examen registrados en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ficha clinica encontrada"), 
                           @ApiResponse(responseCode = "204", description = "No hay ficha clinica registradas"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<ResultadoExamen>> listar() { 

        List<ResultadoExamen> lista = resultadoExamenService.listar(); // Retorna 200 OK con la lista de resultados de examen encontrados

        if (lista.isEmpty()) { 
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de resultados de examen encontrados
    }

    // •   Buscar resultado de examen por ID
    @GetMapping("/{id}") // •   http://localhost:8085/api/v1/resultados-examen/{id}
    @Operation(summary = "Buscar resultado de examen por ID", 
        description = "Obtiene un resultado de examen específico utilizando su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Resultado de examen encontrado"), 
                           @ApiResponse(responseCode = "404", description = "Resultado de examen no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<ResultadoExamen> buscarPorId(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(resultadoExamenService.buscarPorId(id)); // Retorna 200 OK con el resultado de examen encontrado
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra el resultado de examen
        }
    }

    // •   Buscar resultados de examen por ID de orden de examen
    
    @GetMapping("/orden/{idOrdenExamen}") // •   http://localhost:8085/api/v1/resultados-examen/orden/1
    
    @Operation(summary = "Buscar resultados de examen por ID de orden", 
        description = "Obtiene una lista de resultados de examen asociados a una orden específica.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Resultados de examen encontrados"), 
                           @ApiResponse(responseCode = "204", description = "No hay resultados de examen para la orden especificada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<ResultadoExamen>> buscarPorOrden(@PathVariable Integer idOrdenExamen) {

        List<ResultadoExamen> lista = resultadoExamenService.buscarPorOrden(idOrdenExamen);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de resultados de examen encontrados
    }

    // •   Crear resultado
    @PostMapping // •   http://localhost:8085/api/v1/resultados-examen
    @Operation(summary = "Crear resultado de examen", 
        description = "Crea un nuevo resultado de examen y lo retorna.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Resultado de examen creado"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<ResultadoExamen> guardar(@RequestBody ResultadoExamen resultadoExamen) {

        try {
            ResultadoExamen nuevo = resultadoExamenService.guardar(resultadoExamen);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo); // Retorna 201 Created con el nuevo resultado de examen creado
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hay un error al crear el resultado de examen
        }
    }

    // •   Actualizar resultado de examen
    @PutMapping("/{id}") // •   http://localhost:8085/api/v1/resultados-examen/{id}
    @Operation(summary = "Actualizar resultado de examen", 
        description = "Actualiza un resultado de examen existente y lo retorna.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Resultado de examen actualizado"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "404", description = "Resultado de examen no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<ResultadoExamen> actualizar(
            @PathVariable Integer id,
            @RequestBody ResultadoExamen resultadoExamen) {

        try {
            return ResponseEntity.ok(resultadoExamenService.actualizar(id, resultadoExamen)); // Retorna 200 OK con el resultado de examen actualizado
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hay un error al actualizar el resultado de examen
        }
    }

    // •   Eliminar resultado de examen
    @DeleteMapping("/{id}") // •   http://localhost:8085/api/v1/resultados-examen/{id}
    @Operation(summary = "Eliminar resultado de examen", 
        description = "Elimina un resultado de examen existente utilizando su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Resultado de examen eliminado"), 
                           @ApiResponse(responseCode = "404", description = "Resultado de examen no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            resultadoExamenService.eliminar(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si el resultado de examen se eliminó correctamente
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra el resultado de examen para eliminar
        }
    }
}