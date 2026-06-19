package cl.duoc.usuario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.usuario.model.Rol;
import cl.duoc.usuario.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/roles") // Ruta base para todas las operaciones relacionadas con roles
@Tag(name = "Rol", description = "Endpoints para gestionar los roles de los usuarios")
public class RolController {

    
    @Autowired // Inyección de dependencia para el servicio de roles
    private RolService rolService; 

    

    // •  Listar roles
    @GetMapping // // •  http://localhost:8080/api/v1/roles
    @Operation(summary = "Listar Roles", 
               description = "Retorna una lista de todos los Roles registrados")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Rol de usuarios encontrados"), 
                           @ApiResponse(responseCode = "204", description = "No hay Rol registrados"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Rol>> listar() { // esta linea define un endpoint GET para listar todos los roles, y devuelve una ResponseEntity que contiene una lista de objetos Rol

        List<Rol> lista = rolService.listar(); // Obtener la lista de roles desde el service 

        if (lista.isEmpty()) { // No hay roles registrados
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }

        return ResponseEntity.ok(lista); // HTTP 200 OK con la lista de roles en el cuerpo
    }

    // •  Buscar rol por ID
    @GetMapping("/{id}") // •  http://localhost:8080/api/v1/roles/{id}
    @Operation(summary = "Buscar Rol por ID", 
               description = "Retorna un Rol por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Rol encontrado"), 
                           @ApiResponse(responseCode = "404", description = "Rol no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Rol> buscarPorId(@PathVariable ("id") Integer id) { 

        try {
            Rol rol = rolService.buscarPorId(id); 
            return ResponseEntity.ok(rol); // HTTP 200 OK con el rol encontrado en el cuerpo

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // HTTP 404 Not Found si no se encuentra el rol con el ID proporcionado
        }
    }

    // •  Crear rol 
    @PostMapping // •  http://localhost:8080/api/v1/roles
    @Operation(summary = "Crear Rol", 
               description = "Crea un nuevo Rol de usuario con los datos proporcionados")
    public ResponseEntity<Rol> guardar(@RequestBody Rol rol) { // @RequestBody para crear un nuevo rol a partir de un JSON

        try {
            Rol nuevoRol = rolService.guardar(rol); // Guardar el nuevo rol a través del service
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol); // HTTP 201 Created con el rol creado en el cuerpo

        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // HTTP 400 Bad Request si ocurre un error al guardar el rol
        }
    }

    // •  Actualizar rol 
    @PutMapping("/{id}") // •  http://localhost:8080/api/v1/roles/{id}
    @Operation(summary = "Actualizar Rol", 
               description = "Actualiza los datos de un Rol existente buscado por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente por ID"), 
                           @ApiResponse(responseCode = "404", description = "Rol no encontrado por ID"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Rol> actualizar( 
            @PathVariable ("id") Integer id, // @PathVariable para extraer el ID de la URL
            @RequestBody Rol rol) { // @RequestBody para extraer el rol actualizado del cuerpo de la solicitud

        
        try {
            Rol rolActualizado = rolService.actualizar(id, rol);
            return ResponseEntity.ok(rolActualizado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // •  Eliminar rol 
    @DeleteMapping("/{id}") // // •  http://localhost:8080/api/v1/roles/{id}
    @Operation(summary = "Eliminar Rol", 
               description = "Elimina un Rol de usuario existente seleccionado por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Rol eliminado exitosamente"), 
                           @ApiResponse(responseCode = "404", description = "Rol no encontrado por ID, no se pudo eliminar"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable ("id") Integer id) { 

        try {
            rolService.eliminar(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}