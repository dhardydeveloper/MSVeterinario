package cl.duoc.fichaclinica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.fichaclinica.dto.FichaClinicaDTO;
import cl.duoc.fichaclinica.model.FichaClinica;
import cl.duoc.fichaclinica.service.FichaClinicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/fichas-clinicas")
@Tag(name = "Ficha Clínica", description = "Operaciones relacionadas con las fichas clínicas de las mascotas")
public class FichaClinicaController {

    @Autowired
    private FichaClinicaService fichaClinicaService; // Inyección de dependencia del servicio

   

    // •   Listar fichas clínicas
    @GetMapping // •   http://localhost:8086/api/v1/fichas-clinicas
    @Operation(summary = "Listar fichas clínicas", 
    description = "Obtiene una lista de todas las fichas clínicas registradas en el sistema")    
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ficha clinica encontrada"), 
                           @ApiResponse(responseCode = "204", description = "No hay ficha clinica registradas"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<FichaClinica>> listar() {

        List<FichaClinica> lista = fichaClinicaService.listar(); // Llamada al servicio para obtener la lista de fichas clínicas

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si la lista está vacía, se devuelve un código 204 No Content
        }

        return ResponseEntity.ok(lista); // Si la lista no está vacía, se devuelve un código 200 OK con la lista de fichas clínicas en el cuerpo de la respuesta
    }

    // •   Buscar ficha clínica por ID
    @GetMapping("/{id}") // •   http://localhost:8086/api/v1/fichas-clinicas/{id}
    @Operation(summary = "Buscar ficha clínica por ID", 
    description = "Obtiene una ficha clínica específica utilizando su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ficha clinica encontrada"), 
                           @ApiResponse(responseCode = "404", description = "Ficha clinica no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<FichaClinica> buscarPorId(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(fichaClinicaService.buscarPorId(id)); // Si se encuentra la ficha clínica, se devuelve un código 200 OK con la ficha clínica en el cuerpo de la respuesta
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si no se encuentra la ficha clínica, se devuelve un código 404 Not Found
        }
    }

    // •   DTO ficha clínica por ID
    @GetMapping("/dto/{id}") // •   http://localhost:8086/api/v1/fichas-clinicas/dto/1
    public ResponseEntity<FichaClinicaDTO> obtenerDTO(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(fichaClinicaService.obtenerFichaDTO(id)); // Si se encuentra la ficha clínica, se devuelve un código 200 OK con el DTO de ficha clínica en el cuerpo de la respuesta
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si no se encuentra la ficha clínica, se devuelve un código 404 Not Found
        }
    }

    // •   Buscar fichas clínicas por ID mascota
    @GetMapping("/mascota/{idMascota}") // •   http://localhost:8086/api/v1/fichas-clinicas/mascota/{id}
    @Operation(summary = "Buscar fichas clínicas por ID de mascota", 
    description = "Obtiene una lista de fichas clínicas asociadas a una mascota específica utilizando su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Fichas clínicas encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay fichas clínicas registradas para la mascota"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<FichaClinica>> buscarPorMascota(@PathVariable Integer idMascota) {

        List<FichaClinica> lista = fichaClinicaService.buscarPorMascota(idMascota); // Llamada al servicio para obtener la lista de fichas clínicas asociadas a la mascota con el ID proporcionado

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si la lista está vacía, se devuelve un código 204 No Content
        }

        return ResponseEntity.ok(lista); // Si la lista no está vacía, se devuelve un código 200 OK con la lista de fichas clínicas en el cuerpo de la respuesta
    }

    // •   Crear ficha
    @PostMapping // •   http://localhost:8086/api/v1/fichas-clinicas
    @Operation(summary = "Crear ficha clínica", 
    description = "Crea una nueva ficha clínica para una mascota utilizando los datos proporcionados en el cuerpo de la solicitud")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Ficha  clínica creada"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<FichaClinica> guardar(@RequestBody FichaClinica fichaClinica) {

        try {
            FichaClinica nueva = fichaClinicaService.guardar(fichaClinica); 
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva); // Si la ficha clínica se crea correctamente, se devuelve un código 201 Created con la ficha clínica creada en el cuerpo de la respuesta
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Si hay un error al crear la ficha clínica, se devuelve un código 400 Bad Request
        }
    }

    // •   Actualizar ficha clínica
    @PutMapping("/{id}") // •   http://localhost:8086/api/v1/fichas-clinicas/{id}
    @Operation(summary = "Actualizar ficha clínica", 
    description = "Actualiza una ficha clínica existente utilizando su ID y los datos proporcionados en el cuerpo de la solicitud")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ficha clínica actualizada"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "404", description = "Ficha clínica no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<FichaClinica> actualizar(
            @PathVariable Integer id,
            @RequestBody FichaClinica fichaClinica) {

        try {
            return ResponseEntity.ok(fichaClinicaService.actualizar(id, fichaClinica)); // Si la ficha clínica se actualiza correctamente, se devuelve un código 200 OK con la ficha clínica actualizada en el cuerpo de la respuesta
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Si hay un error al actualizar la ficha clínica, se devuelve un código 400 Bad Request
        }
    }

    // •   Eliminar ficha clínica 
    @DeleteMapping("/{id}") // •   http://localhost:8086/api/v1/fichas-clinicas/{id}
    @Operation(summary = "Eliminar ficha clínica", 
    description = "Elimina una ficha clínica existente utilizando su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Ficha clínica eliminada"), 
                           @ApiResponse(responseCode = "404", description = "Ficha clínica no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            fichaClinicaService.eliminar(id);
            return ResponseEntity.noContent().build(); // Si la ficha clínica se elimina correctamente, se devuelve un código 204 No Content
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si no se encuentra la ficha clínica, se devuelve un código 404 Not Found
        }
    }
}