package cl.duoc.usuario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.usuario.dto.UsuarioDTO;
import cl.duoc.usuario.model.Usuario;
import cl.duoc.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController // Anotación para indicar que esta clase es un controlador REST, lo que permite manejar solicitudes HTTP y enviar respuestas en formato JSON u otros formatos
@RequestMapping("/api/v1/usuarios") // Ruta base para todas las operaciones relacionadas con usuarios, por ejemplo, /api/v1/usuarios para listar o crear usuarios, /api/v1/usuarios/{id} para buscar, actualizar o eliminar un usuario por su ID
@Tag(name = "Usuario", description = "Endpoints para gestionar los usuarios del sistema")
public class UsuarioController {

    
    @Autowired
    private UsuarioService usuarioService;

    

    // •  Listar usuarios
    @GetMapping // •  http://localhost:8080/api/v1/usuarios
    @Operation(summary = "Listar Usuarios", 
               description = "Retorna una lista de todos los Usuarios registrados")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Usuarios encontrados"), 
                           @ApiResponse(responseCode = "204", description = "No hay Usuarios registrados"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Usuario>> listar() {

        List<Usuario> lista = usuarioService.listar();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si la lista de usuarios está vacía, respondemos con 204 No Content, lo que indica que la solicitud se procesó correctamente pero no hay contenido para devolver
        }

        return ResponseEntity.ok(lista); // Si la lista de usuarios no está vacía, respondemos con 200 OK y la lista de usuarios en el cuerpo de la respuesta
    }

    // •  Buscar usuario por id
    @GetMapping("/{id}") // •  http://localhost:8080/api/v1/usuarios/{id}
    @Operation(summary = "Buscar Usuario por ID", 
               description = "Retorna un Usuario por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Usuario encontrado"), 
                           @ApiResponse(responseCode = "404", description = "Usuario no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Usuario> buscarPorId(@PathVariable ("id") Integer id) { //@PathVariable se utiliza para extraer el valor del ID de la URL, por ejemplo, /api/v1/usuarios/5 extraerá el valor 5 como ID

        // El método buscarPorId puede lanzar una excepción si no encuentra el usuario con el ID proporcionado
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(usuario);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // •  Buscar usuario por correo electrónico
    @GetMapping("/correo") // •  http://localhost:8080/api/v1/usuarios/correo?correo=camila@gmail.com
    @Operation(summary = "Buscar Usuario por Correo Electrónico", 
               description = "Retorna un Usuario por su correo electrónico")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Usuario encontrado"), 
                           @ApiResponse(responseCode = "404", description = "Usuario no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Usuario> buscarPorCorreo(@RequestParam("correo") String correo) {

        try { 
            Usuario usuario = usuarioService.buscarPorCorreo(correo);
            return ResponseEntity.ok(usuario);

        } catch (Exception e) { // Si ocurre una excepción (por ejemplo, usuario no encontrado), respondemos con 404 Not Found
            return ResponseEntity.notFound().build(); // build() se utiliza para construir la respuesta HTTP sin cuerpo, solo con el código de estado
        }
    }

    // •  Ver DTO
    @GetMapping("/dto/{id}") // •  http://localhost:8080/api/v1/usuarios/dto/1
    @Operation(summary = "Obtener Usuario DTO por ID", 
               description = "Retorna un UsuarioDTO por su ID, que incluye información adicional como el nombre del rol")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "UsuarioDTO encontrado"), 
                           @ApiResponse(responseCode = "404", description = "UsuarioDTO no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<UsuarioDTO> obtenerDTO(@PathVariable("id") Integer id) { 

        try {
            UsuarioDTO dto = usuarioService.obtenerUsuarioDTO(id); 
            return ResponseEntity.ok(dto); 

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // •  Crear usuario 
    @PostMapping // •  http://localhost:8080/api/v1/usuarios
    @Operation(summary = "Crear Usuario", 
               description = "Crea un nuevo Usuario con los datos proporcionados")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"), 
                           @ApiResponse(responseCode = "400", description = "Datos del usuario inválidos"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Usuario> guardar(@RequestBody Usuario usuario) { 

        // El método guardar puede lanzar una excepción si los datos del usuario no son válidos o si ocurre algún error durante la creación
        try {
            Usuario nuevoUsuario = usuarioService.guardar(usuario); 
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario); // Si el usuario se crea correctamente, respondemos con 201 Created y el nuevo usuario en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Si ocurre una excepción (por ejemplo, datos inválidos), respondemos con 400 Bad Request
        }
    }

    // •  Actualizar usuario
    @PutMapping("/{id}") // •  http://localhost:8080/api/v1/usuarios/{id}
    @Operation(summary = "Actualizar Usuario", 
               description = "Actualiza los datos de un Usuario existente buscado por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"), 
                           @ApiResponse(responseCode = "404", description = "Usuario no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Usuario> actualizar( 
            @PathVariable("id") Integer id,
            @RequestBody Usuario usuario) { // @RequestBody se utiliza para indicar que el objeto Usuario se debe construir a partir del cuerpo de la solicitud HTTP, lo que permite enviar los datos del usuario en formato JSON o similar

            
        try { 
            Usuario usuarioActualizado = usuarioService.actualizar(id, usuario); 
            return ResponseEntity.ok(usuarioActualizado); // Si el usuario se actualiza correctamente, respondemos con 200 OK y el usuario actualizado en el cuerpo de la respuesta

        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Si ocurre una excepción (por ejemplo, datos inválidos o usuario no encontrado), respondemos con 400 Bad Request
        }
    }

    // •  Eliminar un usuario 
    @DeleteMapping("/{id}") // •  http://localhost:8080/api/v1/usuarios/{id}
    @Operation(summary = "Eliminar Usuario", 
               description = "Elimina un Usuario existente seleccionado por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"), 
                           @ApiResponse(responseCode = "404", description = "Usuario no encontrado, no se pudo eliminar"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) {

        try {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}