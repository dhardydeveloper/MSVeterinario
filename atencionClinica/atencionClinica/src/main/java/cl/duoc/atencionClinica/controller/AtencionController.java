package cl.duoc.atencionClinica.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.atencionClinica.dto.AtencionDTO;
import cl.duoc.atencionClinica.model.Atencion;
import cl.duoc.atencionClinica.service.AtencionService;

@RestController
@RequestMapping("/api/v1/atenciones")
@Tag(name = "Atenciones Clínicas", description = "Operaciones relacionadas con la gestión de atenciones clínicas")
public class AtencionController {

    @Autowired
    private AtencionService atencionService;

    // •   Listar atenciones
    @GetMapping // •   http://localhost:8084/api/v1/atenciones
    @Operation(summary = "Listar atenciones", 
               description = "Retorna una lista de todas las atenciones registradas")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Atenciones encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay atenciones registradas"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Atencion>> listar() {

        List<Atencion> lista = atencionService.listar();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista de atenciones está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de atenciones
    }

    // •   Buscar atención por ID
    @GetMapping("/{id}") // •   http://localhost:8084/api/v1/atenciones/1
    @Operation(summary = "Buscar atención por ID", 
               description = "Retorna una atención por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Atención encontrada"), 
                           @ApiResponse(responseCode = "404", description = "Atención no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Atencion> buscarPorId(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(atencionService.buscarPorId(id)); // Retorna 200 OK con la atención encontrada
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra la atención con el ID proporcionado
        }
    }

    // •   Ver DTO atención
    @GetMapping("/dto/{id}") // •   http://localhost:8084/api/v1/atenciones/dto/1
    @Operation(summary = "Obtener atención DTO", 
               description = "Obtiene la información completa de una atención clínica en formato DTO mediante su identificador")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Atención encontrada correctamente"), 
                           @ApiResponse(responseCode = "404", description = "Atención no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<AtencionDTO> obtenerDTO(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(atencionService.obtenerAtencionDTO(id)); // Retorna 200 OK con la atención en formato DTO encontrada
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra la atención con el ID proporcionado
        }
    }

    // •   Buscar atencion por ID de cita
    @GetMapping("/cita/{idCita}") // •   http://localhost:8084/api/v1/atenciones/cita/1
    @Operation(summary = "Buscar atenciones por cita",
               description = "Obtiene todas las atenciones asociadas a una cita específica")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Atenciones encontradas"),
                           @ApiResponse(responseCode = "204", description = "No existen atenciones para la cita indicada"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Atencion>> buscarPorCita(@PathVariable Integer idCita) {

        List<Atencion> lista = atencionService.buscarPorCita(idCita);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista de atenciones está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de atenciones
    }

    // •   Buscar atencione por ID de mascota
    @GetMapping("/mascota/{idMascota}") // •   http://localhost:8084/api/v1/atenciones/mascota/1
    @Operation(summary = "Buscar atenciones por mascota",
               description = "Obtiene todas las atenciones asociadas a una mascota")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Atenciones encontradas"),
                           @ApiResponse(responseCode = "204", description = "No existen atenciones para la mascota indicada"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Atencion>> buscarPorMascota(@PathVariable Integer idMascota) {

        List<Atencion> lista = atencionService.buscarPorMascota(idMascota);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista de atenciones está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de atenciones
    }

    // •   Buscar atencione por ID de veterinario
    @GetMapping("/veterinario/{idVeterinario}") // •   http://localhost:8084/api/v1/atenciones/veterinario/1
    @Operation(summary = "Buscar atenciones por veterinario",
               description = "Obtiene todas las atenciones realizadas por un veterinario")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Atenciones encontradas"),
                           @ApiResponse(responseCode = "204", description = "No existen atenciones para el veterinario indicado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Atencion>> buscarPorVeterinario(@PathVariable Integer idVeterinario) {

        List<Atencion> lista = atencionService.buscarPorVeterinario(idVeterinario);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista de atenciones está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de atenciones
    }

    // •   Crear atención
    @PostMapping // •   http://localhost:8084/api/v1/atenciones
    @Operation(summary = "Crear atención", 
               description = "Crea una nueva atención con los datos proporcionados en el cuerpo de la solicitud")
    public ResponseEntity<Atencion> guardar(@RequestBody Atencion atencion) {

        try {
            Atencion nueva = atencionService.guardar(atencion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva); // Retorna 201 Created con la nueva atención creada
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hubo un error en la solicitud (por ejemplo, datos inválidos)
        }
    }

    // •   Actualizar atención
    @PutMapping("/{id}") // •   http://localhost:8084/api/v1/atenciones/{id}
    @Operation(summary = "Actualizar atención", 
               description = "Actualiza los datos de una atención clínica existente buscada por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Atención actualizada exitosamente por ID"), 
                           @ApiResponse(responseCode = "404", description = "Atención no encontrada por ID"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Atencion> actualizar(
            @PathVariable Integer id,
            @RequestBody Atencion atencion) {

        try {
            return ResponseEntity.ok(atencionService.actualizar(id, atencion)); // Retorna 200 OK con la atención actualizada
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hubo un error en la solicitud (por ejemplo, datos inválidos)
        }
    }

    // •   Eliminar atención 
    @DeleteMapping("/{id}") // •   http://localhost:8084/api/v1/atenciones/{id}
    @Operation(summary = "Eliminar atención", 
               description = "Elimina una atención clínica existente seleccionada por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Atención eliminada exitosamente"), 
                           @ApiResponse(responseCode = "404", description = "Atención no encontrada por ID, no se pudo eliminar"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            atencionService.eliminar(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la eliminación fue exitosa
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si la atención no existe
        }
    }
}