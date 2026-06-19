package cl.duoc.veterinario.controller;

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

import cl.duoc.veterinario.dto.VeterinarioDTO;
import cl.duoc.veterinario.model.Veterinario;
import cl.duoc.veterinario.service.VeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/veterinarios")
@Tag(name = "Veterinarios", description = "Endpoints para gestionar los veterinarios del sistema")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService; // Inyección de dependencia del servicio de veterinarios

    // •   Listar veterinarios
    @GetMapping  // •   http://localhost:8082/api/v1/veterinarios
    @Operation(summary = "Listar veterinarios",
               description = "Retorna una lista de todos los veterinarios del sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de veterinarios obtenida exitosamente"),
                           @ApiResponse(responseCode = "204", description = "No hay veterinarios para mostrar"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Veterinario>> listar() {

        List<Veterinario> lista = veterinarioService.listar(); // Llama al servicio para obtener la lista de veterinarios

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la lista está vacía
        }

        return ResponseEntity.ok(lista); // Retorna 200 OK con la lista de veterinarios en el cuerpo de la respuesta
    }

    // •   Buscar veterinario por ID
    @GetMapping("/{id}") // •   http://localhost:8082/api/v1/veterinarios/{id}
    @Operation(summary = "Buscar veterinario por ID",
               description = "Retorna un veterinario específico según su ID. Si no se encuentra, retorna un código de estado 404 Not Found.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Veterinario encontrado exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Veterinario no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Veterinario> buscar(@PathVariable("id") Integer id) { // El @PathVariable indica que el valor del ID se obtiene de la URL

        try {
            Veterinario veterinario = veterinarioService.buscarPorId(id);
            return ResponseEntity.ok(veterinario); // Retorna 200 OK con el veterinario encontrado en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra el veterinario o si ocurre algún error
        }
    }

    // •   Buscar veterinario por RUT
    @GetMapping("/rut/{rut}") // •  http://localhost:8082/api/v1/veterinarios/rut/11111111-1
    @Operation(summary = "Buscar veterinario por RUT",
               description = "Retorna un veterinario específico según su RUT. Si no se encuentra, retorna un código de estado 404 Not Found.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Veterinario encontrado exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Veterinario no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Veterinario> buscarPorRut(@PathVariable("rut") String rut) { // El @PathVariable indica que el valor del RUT se obtiene de la URL

        try {
            Veterinario veterinario = veterinarioService.buscarPorRut(rut);
            return ResponseEntity.ok(veterinario);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // •   Ver DTO veterinario
    @GetMapping("/dto/{id}") // •   http://localhost:8082/api/v1/veterinarios/dto/1
    @Operation(summary = "Obtener DTO de veterinario",
               description = "Retorna el DTO de un veterinario específico según su ID. Si no se encuentra, retorna un código de estado 404 Not Found.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "DTO del veterinario encontrado exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Veterinario no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<VeterinarioDTO> obtenerVeterinarioDTO(@PathVariable Integer id) {

        try {
            Veterinario veterinario = veterinarioService.buscarPorId(id);

            // Crear un DTO y mapear los datos del veterinario
            VeterinarioDTO dto = new VeterinarioDTO();

            dto.setId(veterinario.getId());
            dto.setNombre(veterinario.getNombre());
            dto.setEspecialidad(veterinario.getEspecialidad().getNombre());

            return ResponseEntity.ok(dto); // Retorna 200 OK con el DTO del veterinario en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra el veterinario o si ocurre algún error
        }
    }

    // •   Crear veterinario
    @PostMapping // •   http://localhost:8082/api/v1/veterinarios
    @Operation(summary = "Crear veterinario",
               description = "Crea un nuevo veterinario en el sistema. Retorna el veterinario creado.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Veterinario creado exitosamente"),
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Veterinario> guardar(@RequestBody Veterinario veterinario) { // El @RequestBody indica que el objeto veterinario se obtiene del cuerpo de la solicitud

        try {
            Veterinario nuevoVeterinario = veterinarioService.guardar(veterinario);
            return ResponseEntity.ok(nuevoVeterinario); // Retorna 200 OK con el nuevo veterinario guardado en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si ocurre algún error al guardar el veterinario (por ejemplo, datos inválidos)
        }
    }

    // •   Actualizar veterinario
    @PutMapping("/{id}") // •   http://localhost:8082/api/v1/veterinarios/dto/{id}
    @Operation(summary = "Actualizar veterinario",
               description = "Actualiza un veterinario existente en el sistema. Retorna el veterinario actualizado.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Veterinario actualizado exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Veterinario no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Veterinario> actualizar(
            @PathVariable("id") Integer id, // El @PathVariable indica que el valor del ID se obtiene de la URL
            @RequestBody Veterinario veterinario) { // El @RequestBody indica que el objeto veterinario se obtiene del cuerpo de la solicitud

        try {
            Veterinario veterinarioActualizado = veterinarioService.actualizar(id, veterinario);
            return ResponseEntity.ok(veterinarioActualizado); // Retorna 200 OK con el veterinario actualizado en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra el veterinario a actualizar o si ocurre algún error
        }
    }

    // •   Eliminar veterinario
    @DeleteMapping("/{id}") // •   http://localhost:8082/api/v1/veterinarios/dto/{id}
    @Operation(summary = "Eliminar veterinario",
               description = "Elimina un veterinario existente en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Veterinario eliminado exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Veterinario no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) {

        try {
            veterinarioService.eliminar(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si la eliminación fue exitosa, indicando que no hay contenido en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra el veterinario a eliminar o si ocurre algún error
        }
    }
}