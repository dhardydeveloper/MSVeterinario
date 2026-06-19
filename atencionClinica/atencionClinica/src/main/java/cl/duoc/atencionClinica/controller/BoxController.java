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

import cl.duoc.atencionClinica.model.Box;
import cl.duoc.atencionClinica.service.BoxService;

@RestController
@RequestMapping("/api/v1/boxes")
@Tag(name = "Boxes", description = "Operaciones relacionadas con la gestión de boxes clínicos")
public class BoxController {

    @Autowired
    private BoxService boxService;

     // •   Listar boxes 
    @GetMapping // •   http://localhost:8084/api/v1/boxes
    @Operation(summary = "Listar boxes",
               description = "Obtiene todos los boxes registrados")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
                           @ApiResponse(responseCode = "204", description = "No existen boxes registrados"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Box>> listar() {

        List<Box> lista = boxService.listar();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista de boxes está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de boxes
    }

    // •   Buscar box por ID
    @GetMapping("/{id}") // •   http://localhost:8084/api/v1/boxes/{id}
    @Operation(summary = "Buscar box por ID",
               description = "Obtiene un box mediante su identificador")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Box encontrado"),
                           @ApiResponse(responseCode = "404", description = "Box no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Box> buscarPorId(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(boxService.buscarPorId(id)); // Retorna 200 OK con el box encontrado
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra el box con el ID proporcionado
        }
    }

    // •   Crear box
    @PostMapping // •   http://localhost:8084/api/v1/boxes
    @Operation(summary = "Crear box",
               description = "Registra un nuevo box clínico")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Box creado correctamente"),
                           @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Box> guardar(@RequestBody Box box) {

        try {
            Box nuevo = boxService.guardar(box);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo); // Retorna 201 Created con el nuevo box creado
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hubo un error en la solicitud (por ejemplo, datos inválidos)
        }
    }

    // •   Actualizar box 
    @PutMapping("/{id}") // •   http://localhost:8084/api/v1/boxes/{id}
    @Operation(summary = "Actualizar box",
               description = "Actualiza la información de un box existente")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Box actualizado correctamente"),
                           @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                           @ApiResponse(responseCode = "404", description = "Box no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Box> actualizar(
            @PathVariable Integer id,
            @RequestBody Box box) {

        try {
            return ResponseEntity.ok(boxService.actualizar(id, box)); // Retorna 200 OK con el box actualizado
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hubo un error en la solicitud (por ejemplo, datos inválidos)
        }
    }

    // •   Eliminar box 
    @DeleteMapping("/{id}") // •   http://localhost:8084/api/v1/boxes/{id}
    @Operation(summary = "Eliminar box",
               description = "Elimina un box del sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Box eliminado correctamente"),
                           @ApiResponse(responseCode = "404", description = "Box no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            boxService.eliminar(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la eliminación fue exitosa
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si el box no existe
        }
    }
}