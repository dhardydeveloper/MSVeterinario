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

import cl.duoc.atencionClinica.model.TipoAtencion;
import cl.duoc.atencionClinica.service.TipoAtencionService;

@RestController
@RequestMapping("/api/v1/tipo-atenciones") 
@Tag(name = "Tipos de Atención",description = "Operaciones relacionadas con la gestión de tipos de atención clínica")
public class TipoAtencionController {

    @Autowired
    private TipoAtencionService tipoAtencionService; // Inyección de dependencia del servicio de TipoAtencion

    // •   Listar tipos de atención
    @GetMapping // •   http://localhost:8084/api/v1/tipo-atenciones
    @Operation(summary = "Listar tipos de atención",
               description = "Obtiene todos los tipos de atención registrados")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
                           @ApiResponse(responseCode = "204", description = "No existen tipos de atención registrados"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<TipoAtencion>> listar() {

        List<TipoAtencion> lista = tipoAtencionService.listar();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de tipos de atención
    }

    // •   Buscar tipo de atención por ID
    @GetMapping("/{id}") // •   http://localhost:8084/api/v1/tipo-atenciones/{id}
    @Operation(summary = "Buscar tipo de atención por ID",
               description = "Obtiene un tipo de atención mediante su identificador")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Tipo de atención encontrado"),
                           @ApiResponse(responseCode = "404", description = "Tipo de atención no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<TipoAtencion> buscarPorId(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(tipoAtencionService.buscarPorId(id)); // Retorna 200 OK con el tipo de atención encontrado
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra el tipo de atención con el ID proporcionado
        }
    }

    // •   Crear tipo atención
    @PostMapping // •   http://localhost:8084/api/v1/tipo-atenciones
    @Operation(summary = "Crear tipo de atención",
               description = "Registra un nuevo tipo de atención clínica")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Tipo de atención creado correctamente"),
                           @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<TipoAtencion> guardar(@RequestBody TipoAtencion tipoAtencion) {

        try {
            TipoAtencion nuevo = tipoAtencionService.guardar(tipoAtencion); 
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo); // Retorna 201 Created con el nuevo tipo de atención creado
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hubo un error en la solicitud (por ejemplo, datos inválidos)
        }
    }

    // •   Actualizar tipo de atención
    @PutMapping("/{id}") // •   http://localhost:8084/api/v1/tipo-atenciones/{id}
    @Operation(summary = "Actualizar tipo de atención",
               description = "Actualiza la información de un tipo de atención existente")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Tipo de atención actualizado correctamente"),
                           @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                           @ApiResponse(responseCode = "404", description = "Tipo de atención no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<TipoAtencion> actualizar(
            @PathVariable Integer id,
            @RequestBody TipoAtencion tipoAtencion) {

        try {
            return ResponseEntity.ok(tipoAtencionService.actualizar(id, tipoAtencion)); // Retorna 200 OK con el tipo de atención actualizado
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hubo un error en la solicitud (por ejemplo, datos inválidos)
        }
    }

    // •   Eliminar tipo de atención 
    @DeleteMapping("/{id}") // •   http://localhost:8084/api/v1/tipo-atenciones/{id}
    @Operation(summary = "Eliminar tipo de atención",
               description = "Elimina un tipo de atención del sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Tipo de atención eliminado correctamente"),
                           @ApiResponse(responseCode = "404", description = "Tipo de atención no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            tipoAtencionService.eliminar(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la eliminación fue exitosa
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si el tipo de atención no existe
        }
    }
}