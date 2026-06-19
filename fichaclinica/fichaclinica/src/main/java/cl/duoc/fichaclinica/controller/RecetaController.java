package cl.duoc.fichaclinica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.fichaclinica.dto.RecetaDTO;
import cl.duoc.fichaclinica.model.Receta;
import cl.duoc.fichaclinica.service.RecetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/recetas")
@Tag(name = "Receta", description = "Operaciones relacionadas con las recetas médicas de las mascotas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService; // Inyección de dependencia del servicio

    

    // •   Listar recetas
    @GetMapping // •   http://localhost:8086/api/v1/recetas
    @Operation(summary = "Listar recetas", 
    description = "Obtiene una lista de todas las recetas médicas registradas en el sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Recetas encontrada"), 
                           @ApiResponse(responseCode = "204", description = "No hay recetas registradas"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Receta>> listar() {

        List<Receta> lista = recetaService.listar();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si la lista está vacía, se devuelve un código 204 No Content
        }

        return ResponseEntity.ok(lista); // Si la lista no está vacía, se devuelve un código 200 OK con la lista de recetas en el cuerpo de la respuesta
    }

    // •   Buscar receta por ID
    @GetMapping("/{id}") // •   http://localhost:8086/api/v1/recetas/{id}
    @Operation(summary = "Buscar receta por ID", 
    description = "Obtiene una receta médica específica utilizando su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Receta encontrada"), 
                           @ApiResponse(responseCode = "404", description = "Receta no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Receta> buscarPorId(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(recetaService.buscarPorId(id)); // Si se encuentra la receta, se devuelve un código 200 OK con la receta en el cuerpo de la respuesta
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si no se encuentra la receta, se devuelve un código 404 Not Found
        }
    }

    // •   Ver DTO receta
    @Operation(summary = "Ver DTO de receta", 
    description = "Obtiene el DTO de una receta médica específica utilizando su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Receta encontrada"), 
                           @ApiResponse(responseCode = "404", description = "Receta no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @GetMapping("/dto/{id}") // http://localhost:8086/api/v1/recetas/dto/1
    public ResponseEntity<RecetaDTO> obtenerDTO(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(recetaService.obtenerRecetaDTO(id)); // Si se encuentra la receta, se devuelve un código 200 OK con el DTO de receta en el cuerpo de la respuesta
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si no se encuentra la receta, se devuelve un código 404 Not Found
        }
    }

    // •   Buscar recetas por ID de atención
    @GetMapping("/atencion/{idAtencion}") // •   http://localhost:8086/api/v1/recetas/atencion/1
    @Operation(summary = "Buscar recetas por ID de atención", 
    description = "Obtiene una lista de recetas médicas asociadas a una atención específica utilizando su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Recetas encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay recetas registradas para la atención"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Receta>> buscarPorAtencion(@PathVariable Integer idAtencion) {

        List<Receta> lista = recetaService.buscarPorAtencion(idAtencion);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si la lista está vacía, se devuelve un código 204 No Content
        }

        return ResponseEntity.ok(lista); // Si la lista no está vacía, se devuelve un código 200 OK con la lista de recetas en el cuerpo de la respuesta
    }

    // •   Buscar recetas por ID de mascota
    @GetMapping("/mascota/{idMascota}") // •   http://localhost:8086/api/v1/recetas/mascota/1
    @Operation(summary = "Buscar recetas por ID de mascota", 
    description = "Obtiene una lista de recetas médicas asociadas a una mascota específica utilizando su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Recetas encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay recetas registradas para la mascota"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Receta>> buscarPorMascota(@PathVariable Integer idMascota) {

        List<Receta> lista = recetaService.buscarPorMascota(idMascota);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si la lista está vacía, se devuelve un código 204 No Content
        }

        return ResponseEntity.ok(lista); // Si la lista no está vacía, se devuelve un código 200 OK con la lista de recetas en el cuerpo de la respuesta
    }

    // •   Buscar recetas por ID de veterinario
    @GetMapping("/veterinario/{idVeterinario}") // •   http://localhost:8086/api/v1/recetas/veterinario/1
    @Operation(summary = "Buscar recetas por ID de veterinario", 
    description = "Obtiene una lista de recetas médicas asociadas a un veterinario específico utilizando su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Recetas encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay recetas registradas para el veterinario"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Receta>> buscarPorVeterinario(@PathVariable Integer idVeterinario) {

        List<Receta> lista = recetaService.buscarPorVeterinario(idVeterinario);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si la lista está vacía, se devuelve un código 204 No Content
        }

        return ResponseEntity.ok(lista); // Si la lista no está vacía, se devuelve un código 200 OK con la lista de recetas en el cuerpo de la respuesta
    }

    // •   Crear receta
    @PostMapping // •   http://localhost:8086/api/v1/recetas
    @Operation(summary = "Crear receta", 
    description = "Crea una nueva receta médica utilizando los datos proporcionados en el cuerpo de la solicitud")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Receta creada"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Receta> guardar(@RequestBody Receta receta) {

        try {
            Receta nueva = recetaService.guardar(receta); 
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva); // Si la receta se crea correctamente, se devuelve un código 201 Created con la receta creada en el cuerpo de la respuesta
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Si hay un error al crear la receta, se devuelve un código 400 Bad Request
        }
    }

    // •   Actualizar receta
    @PutMapping("/{id}") // •   http://localhost:8086/api/v1/recetas/{id}
    @Operation(summary = "Actualizar receta", 
    description = "Actualiza una receta médica existente utilizando su ID y los datos proporcionados en el cuerpo de la solicitud")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Receta actualizada"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Receta> actualizar(
            @PathVariable Integer id,
            @RequestBody Receta receta) {

        try {
            return ResponseEntity.ok(recetaService.actualizar(id, receta)); // Si la receta se actualiza correctamente, se devuelve un código 200 OK con la receta actualizada en el cuerpo de la respuesta
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Si hay un error al actualizar la receta, se devuelve un código 400 Bad Request
        }
    }

    // •   Eliminar receta 
    @DeleteMapping("/{id}") // •   http://localhost:8086/api/v1/recetas/{id}
    @Operation(summary = "Eliminar receta", 
    description = "Elimina una receta médica existente utilizando su ID")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            recetaService.eliminar(id);
            return ResponseEntity.noContent().build(); // Si la receta se elimina correctamente, se devuelve un código 204 No Content
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si no se encuentra la receta, se devuelve un código 404 Not Found
        }
    }
}