package cl.duoc.examenes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.examenes.dto.OrdenExamenDTO;
import cl.duoc.examenes.model.OrdenExamen;
import cl.duoc.examenes.service.OrdenExamenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/ordenes-examen") 
@Tag(name = "Orden Examen", description = "Controlador para gestionar las órdenes de examen")
public class OrdenExamenController {

    @Autowired
    private OrdenExamenService ordenExamenService; // Inyección de dependencia del servicio


    // •   Listar órdenes de examenes
    @GetMapping // •   http://localhost:8085/api/v1/ordenes-examen
    @Operation(summary = "Listar órdenes de examen", 
        description = "Obtiene una lista de todas las órdenes de examen registradas en el sistema.")   
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ficha clinica encontrada"), 
                           @ApiResponse(responseCode = "204", description = "No hay ficha clinica registradas"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
        public ResponseEntity<List<OrdenExamen>> listar() {

        List<OrdenExamen> lista = ordenExamenService.listar();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de órdenes de examen
    }

    // •   Buscar orden de examen por ID
    @GetMapping("/{id}") // •   http://localhost:8085/api/v1/ordenes-examen/{id}
    @Operation(summary = "Buscar orden de examen por ID", 
        description = "Obtiene una orden de examen específica utilizando su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Orden de examen encontrada"), 
                           @ApiResponse(responseCode = "404", description = "Orden de examen no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<OrdenExamen> buscarPorId(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(ordenExamenService.buscarPorId(id)); // Retorna 200 OK con la orden de examen encontrada
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra la orden de examen
        }
    }

    // •   Ver DTO orden
    @Operation(summary = "Obtener DTO de orden de examen", 
        description = "Obtiene el DTO de una orden de examen específica utilizando su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "DTO de orden de examen encontrado"), 
                           @ApiResponse(responseCode = "404", description = "DTO de orden de examen no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @GetMapping("/dto/{id}") // •   http://localhost:8085/api/v1/ordenes-examen/dto/1
    public ResponseEntity<OrdenExamenDTO> obtenerDTO(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(ordenExamenService.obtenerOrdenExamenDTO(id)); // Retorna 200 OK con el DTO de la orden de examen encontrada
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra la orden de examen
        }
    }

    // •   Buscar ordenes de examen por ID de atención / mascota / veterinario
    @GetMapping("/atencion/{idAtencion}") // •   http://localhost:8085/api/v1/ordenes-examen/atencion/1
    @Operation(summary = "Buscar órdenes de examen por ID de atención", 
        description = "Obtiene una lista de órdenes de examen asociadas a una atención específica utilizando su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Órdenes de examen encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay órdenes de examen para la atención especificada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<OrdenExamen>> buscarPorAtencion(@PathVariable Integer idAtencion) {

        List<OrdenExamen> lista = ordenExamenService.buscarPorAtencion(idAtencion); // Retorna 200 OK con la lista de órdenes de examen encontradas

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de órdenes de examen encontradas
    }

    @GetMapping("/mascota/{idMascota}") // •   http://localhost:8085/api/v1/ordenes-examen/mascota/1
    @Operation(summary = "Buscar órdenes de examen por ID de mascota", 
        description = "Obtiene una lista de órdenes de examen asociadas a una mascota específica utilizando su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Órdenes de examen encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay órdenes de examen para la mascota especificada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<OrdenExamen>> buscarPorMascota(@PathVariable Integer idMascota) {

        List<OrdenExamen> lista = ordenExamenService.buscarPorMascota(idMascota); // Retorna 200 OK con la lista de órdenes de examen encontradas

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de órdenes de examen encontradas
    }

    @GetMapping("/veterinario/{idVeterinario}") // •   http://localhost:8085/api/v1/ordenes-examen/veterinario/1
    @Operation(summary = "Buscar órdenes de examen por ID de veterinario", 
        description = "Obtiene una lista de órdenes de examen asociadas a un veterinario específico utilizando su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Órdenes de examen encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay órdenes de examen para el veterinario especificado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<OrdenExamen>> buscarPorVeterinario(@PathVariable Integer idVeterinario) {

        List<OrdenExamen> lista = ordenExamenService.buscarPorVeterinario(idVeterinario); // Retorna 200 OK con la lista de órdenes de examen encontradas

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de órdenes de examen encontradas
    }

    // •   Crear orden de examen
    @PostMapping // •   http://localhost:8085/api/v1/ordenes-examen
    @Operation(summary = "Crear orden de examen", 
        description = "Crea una nueva orden de examen y la retorna.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Orden de examen creada"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<OrdenExamen> guardar(@RequestBody OrdenExamen ordenExamen) {

        try {
            OrdenExamen nueva = ordenExamenService.guardar(ordenExamen); 
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva); // Retorna 201 Created con la nueva orden de examen creada
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hay un error al crear la orden de examen
        }
    }

    // •   Actualizar orden de examen 
    @PutMapping("/{id}") // •   http://localhost:8085/api/v1/ordenes-examen/{id}
    @Operation(summary = "Actualizar orden de examen", 
        description = "Actualiza una orden de examen existente y la retorna.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Orden de examen actualizada"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "404", description = "Orden de examen no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<OrdenExamen> actualizar(
            @PathVariable Integer id,
            @RequestBody OrdenExamen ordenExamen) {

        try {
            return ResponseEntity.ok(ordenExamenService.actualizar(id, ordenExamen)); // Retorna 200 OK con la orden de examen actualizada
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hay un error al actualizar la orden de examen
        }
    }

    // •   Eliminar orden de examen 
    @DeleteMapping("/{id}") // •   http://localhost:8085/api/v1/ordenes-examen/{id}
    @Operation(summary = "Eliminar orden de examen", 
        description = "Elimina una orden de examen existente utilizando su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Orden de examen eliminada"), 
                           @ApiResponse(responseCode = "404", description = "Orden de examen no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            ordenExamenService.eliminar(id); 
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la orden de examen se eliminó correctamente
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra la orden de examen para eliminar
        }
    }
}