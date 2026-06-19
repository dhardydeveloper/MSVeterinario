package cl.duoc.agenda.controller;

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

import cl.duoc.agenda.dto.CitaDTO;
import cl.duoc.agenda.model.Cita;
import cl.duoc.agenda.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;



@RestController
@RequestMapping("/api/v1/citas")
@Tag(name = "Citas", description = "Endpoints para gestionar citas veterinarias") 
public class CitaController {

    @Autowired
    private CitaService citaService; // Inyección de dependencia

    // •   Listar citas
    @GetMapping // http://localhost:8083/api/v1/citas
    @Operation(summary = "Listar citas", 
               description = "Retorna una lista de todas las citas registradas")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Citas encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay citas registradas"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Cita>> listar() {

        List<Cita> lista = citaService.listar(); // Llamada al servicio para obtener la lista de citas

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(lista); // 200 OK + lista de citas en el cuerpo de la respuesta
    }

    // •   Buscar cita por ID
    @GetMapping("/{id}") // http://localhost:8083/api/v1/citas/1
    @Operation(summary = "Buscar cita por ID", 
               description = "Retorna una cita por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Cita encontrada"), 
                           @ApiResponse(responseCode = "404", description = "Cita no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Cita> buscarPorId(@PathVariable Integer id) { 

        try {
            Cita cita = citaService.buscarPorId(id);
            return ResponseEntity.ok(cita); // 200 OK + cita encontrada en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found si no se encuentra la cita con el ID proporcionado
        }
    }

    // •   Ver DTO cita
    @GetMapping("/dto/{id}") // •   http://localhost:8083/api/v1/citas/dto/1
    @Operation(summary = "Obtener Cita DTO",
               description = "Obtiene la información de una cita en formato DTO mediante su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Cita encontrada correctamente"),
                           @ApiResponse(responseCode = "404", description = "No se encontró una cita con el ID proporcionado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})    
    public ResponseEntity<CitaDTO> obtenerDTO(@PathVariable Integer id) {

        try {
            CitaDTO dto = citaService.obtenerCitaDTO(id);
            return ResponseEntity.ok(dto); // 200 OK + DTO de la cita encontrada en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found si no se encuentra la cita con el ID proporcionado
        }
    }

    // •   Buscar cita por mascota
    @GetMapping("/mascota/{idMascota}") // http://localhost:8083/api/v1/citas/mascota/1
    @Operation(summary = "Buscar cita por mascota", 
               description = "Retorna una lista de citas para una mascota específica")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Citas encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay citas registradas para la mascota"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Cita>> buscarPorMascota(@PathVariable Integer idMascota) {

        List<Cita> lista = citaService.buscarPorMascota(idMascota);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si no se encuentran citas para la mascota con el ID proporcionado
        }

        return ResponseEntity.ok(lista); // 200 OK + lista de citas para la mascota con el ID proporcionado en el cuerpo de la respuesta
    }

    // •   Buscar cita por veterinario
    @GetMapping("/veterinario/{idVeterinario}") // http://localhost:8083/api/v1/citas/veterinario/1
    @Operation(summary = "Buscar cita por veterinario", 
               description = "Retorna una lista de citas por su ID de veterinario")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Citas encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay citas registradas para el veterinario"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Cita>> buscarPorVeterinario(@PathVariable Integer idVeterinario) { 

        List<Cita> lista = citaService.buscarPorVeterinario(idVeterinario);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si no se encuentran citas para el veterinario con el ID proporcionado
        }

        return ResponseEntity.ok(lista); // 200 OK + lista de citas para el veterinario con el ID proporcionado en el cuerpo de la respuesta
    }

    // •   Crear cita
    @PostMapping // •   http://localhost:8083/api/v1/citas
    @Operation(summary = "Crear cita", 
               description = "Crea una nueva cita")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Cita creada correctamente"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Cita> guardar(@RequestBody Cita cita) {

        try {
            Cita nuevaCita = citaService.guardar(cita); 
            return ResponseEntity.ok(nuevaCita); // 200 OK + nueva cita creada en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request si hay un error al guardar la cita (por ejemplo, datos inválidos en el cuerpo de la solicitud)
        }
    }

    // •   Actualizar cita
    @PutMapping("/{id}") // http://localhost:8083/api/v1/citas/1
    @Operation(summary = "Actualizar cita", 
               description = "Actualiza una cita existente")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Cita actualizada correctamente"), 
                           @ApiResponse(responseCode = "404", description = "Cita no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Cita> actualizar(
            @PathVariable Integer id,
            @RequestBody Cita cita) {

        try {
            Cita citaActualizada = citaService.actualizar(id, cita);
            return ResponseEntity.ok(citaActualizada); // 200 OK + cita actualizada en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found si no se encuentra la cita con el ID proporcionado para actualizar
        }
    }

    // •   Eliminar cita
    @DeleteMapping("/{id}") // http://localhost:8083/api/v1/citas/1
    @Operation(summary = "Eliminar cita", 
               description = "Elimina una cita existente buscada por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Cita eliminada exitosamente"), 
                           @ApiResponse(responseCode = "404", description = "Cita no encontrada por el ID"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            citaService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content si la eliminación fue exitosa

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found si no se encuentra la cita con el ID proporcionado para eliminar
        }
    }
}