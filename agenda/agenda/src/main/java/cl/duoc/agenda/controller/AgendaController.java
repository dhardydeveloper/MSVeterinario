package cl.duoc.agenda.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.agenda.dto.AgendaDTO;
import cl.duoc.agenda.model.Agenda;
import cl.duoc.agenda.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/v1/agendas")
@Tag(name = "Agendas", description = "Endpoints para gestionar agendas veterinarias") 
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    // •   Listar agendas
    @GetMapping // •   http://localhost:8083/api/v1/agendas
    @Operation(summary = "Listar agendas", 
               description = "Retorna una lista de todas las agendas registradas")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Agendas encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay agendas registradas"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Agenda>> listar() {

        List<Agenda> lista = agendaService.listar();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si no hay agendas para listar
        }

        return ResponseEntity.ok(lista); // 200 OK + lista de agendas en el cuerpo de la respuesta
    }

    // •   Buscar agenda por ID
    @GetMapping("/{id}") // •   http://localhost:8083/api/v1/agendas/1
    @Operation(summary = "Buscar agenda por ID", 
               description = "Retorna una agenda por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Agenda encontrada"), 
                           @ApiResponse(responseCode = "404", description = "Agenda no encontrada"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Agenda> buscarPorId(@PathVariable Integer id) {

        try {
            Agenda agenda = agendaService.buscarPorId(id);
            return ResponseEntity.ok(agenda); // 200 OK + agenda encontrada en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found si no se encuentra la agenda con el ID proporcionado
        }
    }

    // •   DTO agenda por ID
    @GetMapping("/dto/{id}") // •   http://localhost:8083/api/v1/agendas/dto/1
    @Operation(summary = "Obtener Agenda DTO",
               description = "Obtiene la información de una agenda en formato DTO mediante su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Agenda encontrada correctamente"),
                           @ApiResponse(responseCode = "404", description = "No se encontró una agenda con el ID proporcionado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})    
    public ResponseEntity<AgendaDTO> obtenerDTO(@PathVariable Integer id) {

        try {
            AgendaDTO dto = agendaService.obtenerAgendaDTO(id);
            return ResponseEntity.ok(dto); // 200 OK + DTO de la agenda encontrada en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found si no se encuentra la agenda con el ID proporcionado
        }
    }

    // •   Buscar agenda por ID veterinario
    @GetMapping("/veterinario/{idVeterinario}") // •   http://localhost:8083/api/v1/agendas/veterinario/1
    @Operation(summary = "Buscar agenda por ID veterinario", 
               description = "Retorna una lista de agendas para un veterinario específico")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Agendas encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay agendas registradas para el veterinario"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Agenda>> buscarPorVeterinario(@PathVariable Integer idVeterinario) {

        List<Agenda> lista = agendaService.buscarPorVeterinario(idVeterinario);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si no se encuentran agendas para el veterinario con el ID proporcionado
        }

        return ResponseEntity.ok(lista); // 200 OK + lista de agendas para el veterinario con el ID proporcionado en el cuerpo de la respuesta
    }

    // •   Crear agenda
    @PostMapping // •   http://localhost:8083/api/v1/agendas
    @Operation(summary = "Crear agenda", 
               description = "Crea una nueva agenda con los datos proporcionados en el cuerpo de la solicitud")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Agenda creada exitosamente"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Agenda> guardar(@RequestBody Agenda agenda) {

        try {
            Agenda nuevaAgenda = agendaService.guardar(agenda);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAgenda); // 201 Created + nueva agenda creada en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request si hay un error al guardar la agenda (por ejemplo, datos inválidos en el cuerpo de la solicitud)
        }
    }

    // •   Actualizar agenda
    @PutMapping("/{id}") // •   http://localhost:8083/api/v1/agendas/1
    @Operation(summary = "Actualizar agenda", 
               description = "Actualiza los datos de una agenda existente buscada por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Agenda actualizada exitosamente"), 
                           @ApiResponse(responseCode = "404", description = "Agenda no encontrada por el ID"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Agenda> actualizar(
            @PathVariable Integer id,
            @RequestBody Agenda agenda) {

        try {
            Agenda agendaActualizada = agendaService.actualizar(id, agenda);
            return ResponseEntity.ok(agendaActualizada); // 200 OK + agenda actualizada en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request si hay un error al actualizar la agenda (por ejemplo, datos inválidos en el cuerpo de la solicitud)
        }
    }

    // •   Eliminar agenda
    @DeleteMapping("/{id}") // •   http://localhost:8083/api/v1/agendas/1
    @Operation(summary = "Eliminar agenda", 
               description = "Elimina una agenda existente buscada por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Agenda eliminada exitosamente"), 
                           @ApiResponse(responseCode = "404", description = "Agenda no encontrada por el ID"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            agendaService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content si la eliminación fue exitosa

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found si no se encuentra la agenda con el ID proporcionado para eliminar
        }
    }
}