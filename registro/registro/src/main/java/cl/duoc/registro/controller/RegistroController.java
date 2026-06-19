package cl.duoc.registro.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.registro.dto.RegistroDTO;
import cl.duoc.registro.model.Cliente;
import cl.duoc.registro.model.Mascota;
import cl.duoc.registro.service.RegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/registros") // Ruta base para todos los endpoints de este controlador, esto significa que todos los endpoints definidos en esta clase estarán bajo la ruta /api/v1/registros
@Tag(name = "Registro", description = "Endpoints para gestionar clientes y mascotas") 
public class RegistroController {

    @Autowired
    private RegistroService registroService; // Inyectamos el servicio de Registro para acceder a la lógica de negocio y manejar las operaciones relacionadas con clientes y mascotas


    // CLIENTE //

    // •  Listar clientes
    @GetMapping("/clientes") // •  http://localhost:8081/api/v1/registros/clientes
    @Operation(summary = "Listar clientes", 
               description = "Retorna una lista de todos los clientes registrados")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Clientes encontrados"), 
                           @ApiResponse(responseCode = "204", description = "No hay clientes registrados"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Cliente>> listarClientes() { // Endpoint para listar todos los clientes, responde a las solicitudes GET en la ruta /api/v1/registros/clientes, devuelve una ResponseEntity que contiene una lista de objetos Cliente

        List<Cliente> lista = registroService.listarClientes(); // Llamamos al método listarClientes() del servicio de Registro para obtener la lista de clientes desde la base de datos, esto devuelve una lista de objetos Cliente que representa a todos los clientes registrados en el sistema.

        if (lista.isEmpty()) { // Si la lista de clientes está vacía, es decir, no hay clientes registrados en la base de datos, respondemos con un código HTTP 204 No Content utilizando ResponseEntity.noContent().build(). Esto indica que la solicitud fue exitosa pero no hay contenido para devolver.
            return ResponseEntity.noContent().build(); // esta línea construye una respuesta HTTP con el código de estado 204 No Content, lo que indica que la solicitud fue exitosa pero no hay contenido para devolver. Esto se utiliza comúnmente cuando una consulta a la base de datos no devuelve resultados, como en este caso cuando no hay clientes registrados.
        }

        return ResponseEntity.ok(lista); // Si la lista de clientes no está vacía, respondemos con un código HTTP 200 OK utilizando ResponseEntity.ok(lista), y el cuerpo de la respuesta contiene la lista de clientes obtenida del servicio. Esto indica que la solicitud fue exitosa y se devuelve el contenido solicitado (la lista de clientes).
    }

    // •  Buscar cliente por ID
    @GetMapping("/clientes/{id}") // •  http://localhost:8081/api/v1/registros/clientes/1
    @Operation(summary = "Buscar cliente por ID", 
               description = "Retorna un cliente por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Cliente encontrado"), 
                           @ApiResponse(responseCode = "404", description = "Cliente no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Integer id) { // Endpoint para buscar un cliente por su ID, responde a las solicitudes GET en la ruta /api/v1/registros/clientes/{id}, donde {id} es un parámetro de ruta que representa el ID del cliente que queremos buscar. 

        try {
            Cliente cliente = registroService.buscarClientePorId(id);
            return ResponseEntity.ok(cliente); // Llamamos al método buscarClientePorId() del servicio de Registro para obtener el cliente con el ID proporcionado. Si el cliente existe, respondemos con un código HTTP 200 OK utilizando ResponseEntity.ok(cliente), y el cuerpo de la respuesta contiene el objeto Cliente encontrado. Esto indica que la solicitud fue exitosa y se devuelve el cliente solicitado.

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si el cliente no existe o ocurre cualquier excepción durante la búsqueda, respondemos con un código HTTP 404 Not Found utilizando ResponseEntity.notFound().build(). Esto indica que el recurso solicitado (el cliente con el ID proporcionado) no fue encontrado en la base de datos.
        }
    }

    // •  Buscar cliente por RUT
    @GetMapping("/clientes/rut/{rut}") // •  http://localhost:8081/api/v1/registros/clientes/rut/12.345.678-9
    @Operation(summary = "Buscar cliente por RUT", 
               description = "Retorna un cliente por su RUT")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Cliente encontrado por RUT"), 
                           @ApiResponse(responseCode = "404", description = "Cliente no encontrado"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Cliente> buscarClientePorRut(@PathVariable String rut) {

        try {
            Cliente cliente = registroService.buscarClientePorRut(rut);
            return ResponseEntity.ok(cliente);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // •  Crear cliente
    
    @PostMapping("/clientes") // •  http://localhost:8081/api/v1/registros/clientes
    @Operation(summary = "Crear cliente", 
               description = "Crea un nuevo cliente con los datos proporcionados en el cuerpo de la solicitud") 
    public ResponseEntity<Cliente> guardarCliente(@RequestBody Cliente cliente) {

        Cliente nuevoCliente = registroService.guardarCliente(cliente);

        return ResponseEntity.ok(nuevoCliente);
    }

    // •  Actualizar cliente
    @PutMapping("/clientes/{id}") // •  http://localhost:8081/api/v1/registros/clientes/{id}
    @Operation(summary = "Actualizar cliente", 
               description = "Actualiza los datos de un cliente existente buscado por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente por ID"), 
                           @ApiResponse(responseCode = "404", description = "Cliente no encontrado por ID"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Cliente> actualizarCliente(
            @PathVariable Integer id, // El ID del cliente que queremos actualizar, se obtiene de la ruta de la solicitud
            @RequestBody Cliente cliente) { // El objeto Cliente con los datos actualizados, se obtiene del cuerpo de la solicitud en formato JSON y se deserializa automáticamente a un objeto Cliente por Spring

        try {
            Cliente clienteActualizado = registroService.actualizarCliente(id, cliente);
            return ResponseEntity.ok(clienteActualizado); // Llamamos al método actualizarCliente() del servicio de Registro para actualizar el cliente con el ID proporcionado utilizando los datos del objeto Cliente recibido. Si la actualización es exitosa, respondemos con un código HTTP 200 OK utilizando ResponseEntity.ok(clienteActualizado), y el cuerpo de la respuesta contiene el objeto Cliente actualizado. Esto indica que la solicitud fue exitosa y se devuelve el cliente actualizado.

        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si el cliente no existe o ocurre cualquier excepción durante la actualización, respondemos con un código HTTP 404 Not Found utilizando ResponseEntity.notFound().build(). Esto indica que el recurso solicitado (el cliente con el ID proporcionado) no fue encontrado en la base de datos, por lo tanto no se pudo actualizar.
        }
    }

    // •  Eliminar cliente
    @DeleteMapping("/clientes/{id}") // •  http://localhost:8081/api/v1/registros/clientes/{id}
    @Operation(summary = "Eliminar cliente", 
               description = "Elimina un cliente existente seleccionado por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente"), 
                           @ApiResponse(responseCode = "404", description = "Cliente no encontrado por ID, no se pudo eliminar"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminarCliente(@PathVariable Integer id) {

        try {
            registroService.eliminarCliente(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }



    // MASCOTA //

    // •  Listar mascotas
    @GetMapping("/mascotas")  // •  http://localhost:8081/api/v1/registros/mascotas
    @Operation(summary = "Listar mascotas", 
               description = "Retorna una lista de todas las mascotas registradas")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Mascotas encontradas"), 
                           @ApiResponse(responseCode = "204", description = "No hay mascotas registradas"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Mascota>> listarMascotas() {

        List<Mascota> lista = registroService.listarMascotas();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    // •  Buscar mascota por ID
    @GetMapping("/mascotas/{id}") // •  http://localhost:8081/api/v1/registros/mascotas/{id}
    @Operation(summary = "Buscar mascota por ID", 
               description = "Retorna una mascota buscada por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Mascota encontrada por ID"), 
                           @ApiResponse(responseCode = "404", description = "Mascota no encontrada por el ID"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Mascota> buscarMascotaPorId(@PathVariable Integer id) {

        try {
            Mascota mascota = registroService.buscarMascotaPorId(id);
            return ResponseEntity.ok(mascota);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // •  Buscar mascotas por cliente
    @GetMapping("/mascotas/cliente/{clienteId}") // •  http://localhost:8081/api/v1/registros/mascotas/cliente/1
    @Operation(summary = "Buscar mascotas por cliente", 
               description = "Retorna una lista de mascotas asociadas a un cliente específico")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Mascotas encontradas existosamente"), 
                           @ApiResponse(responseCode = "204", description = "No hay mascotas asociadas a ese cliente"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Mascota>> buscarMascotasPorCliente(@PathVariable Integer clienteId) {

        List<Mascota> lista = registroService.buscarMascotasPorCliente(clienteId);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    // •  Buscar mascotas por nombre
    @GetMapping("/mascotas/buscar") // •  http://localhost:8081/api/v1/registros/mascotas/buscar?nombre=Firulais
    @Operation(summary = "Buscar mascotas por nombre", 
               description = "Retorna una lista de mascotas que coinciden con el nombre proporcionado")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Mascotas encontradas por su nombre"), 
                           @ApiResponse(responseCode = "204", description = "No hay mascotas que coincidan con ese nombre"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Mascota>> buscarMascotasPorNombre(@RequestParam String nombre) {

        List<Mascota> lista = registroService.buscarMascotasPorNombre(nombre);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    // •  Buscar mascota por chip
    @GetMapping("/mascotas/chip") // •  http://localhost:8081/api/v1/registros/mascotas/chip?numeroChip=CHIP12345
    @Operation(summary = "Buscar mascota por chip", 
               description = "Retorna una mascota por su número de chip único")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Mascota encontrada por su número de chip"), 
                           @ApiResponse(responseCode = "404", description = "Mascota no encontrada por el número de chip"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Mascota> buscarMascotaPorNumeroChip(@RequestParam String numeroChip) {

        try {
            Mascota mascota = registroService.buscarMascotaPorNumeroChip(numeroChip);
            return ResponseEntity.ok(mascota);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // •  Crear mascota
    @PostMapping("/mascotas") // •  http://localhost:8081/api/v1/registros/mascotas
    @Operation(summary = "Crear mascota", 
               description = "Crea una nueva mascota con los datos proporcionados en el cuerpo de la solicitud")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Mascota creada exitosamente"), 
                           @ApiResponse(responseCode = "400", description = "Solicitud inválida"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Mascota> guardarMascota(@RequestBody Mascota mascota) { 

        try {
            Mascota nuevaMascota = registroService.guardarMascota(mascota);
            return ResponseEntity.ok(nuevaMascota); // Llamamos al método guardarMascota() del servicio de Registro para guardar la nueva mascota utilizando los datos del objeto Mascota recibido. Si el guardado es exitoso, respondemos con un código HTTP 200 OK utilizando ResponseEntity.ok(nuevaMascota), y el cuerpo de la respuesta contiene el objeto Mascota recién guardado. Esto indica que la solicitud fue exitosa y se devuelve la mascota guardada.

        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Si ocurre cualquier excepción durante el guardado, respondemos con un código HTTP 400 Bad Request utilizando ResponseEntity.badRequest().build(). Esto indica que hubo un error en la solicitud, como datos inválidos o falta de información necesaria para guardar la mascota.
        }
    }

    // •  Actualizar mascota
    @PutMapping("/mascotas/{id}") // •  http://localhost:8081/api/v1/registros/mascotas/{id}
    @Operation(summary = "Actualizar mascota", 
               description = "Actualiza los datos de una mascota existente buscada por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Mascota actualizada exitosamente"), 
                           @ApiResponse(responseCode = "404", description = "Mascota no encontrada por el ID"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Mascota> actualizarMascota(
            @PathVariable Integer id,
            @RequestBody Mascota mascota) {

        try {
            Mascota mascotaActualizada = registroService.actualizarMascota(id, mascota);
            return ResponseEntity.ok(mascotaActualizada);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // •  Eliminar mascota
    @DeleteMapping("/mascotas/{id}") // •  http://localhost:8081/api/v1/registros/mascotas/{id}
    @Operation(summary = "Eliminar mascota", 
               description = "Elimina una mascota existente buscada por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Mascota eliminada exitosamente"), 
                           @ApiResponse(responseCode = "404", description = "Mascota no encontrada por el ID, no se pudo eliminar"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminarMascota(@PathVariable Integer id) {

        try {
            registroService.eliminarMascota(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }



    // DTO CLIENTE + MASCOTA //

    // •  Ver DTO 
    @GetMapping("/dto/mascota/{idMascota}") // •  http://localhost:8081/api/v1/registros/dto/mascota/1
    @Operation(summary = "Obtener registro DTO", 
               description = "Retorna un DTO con los datos del cliente y su mascota asociada buscando por el ID de la mascota")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Registro DTO encontrado exitosamente"), 
                           @ApiResponse(responseCode = "404", description = "Registro DTO no encontrado por el ID de la mascota"), 
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<RegistroDTO> obtenerRegistroDTO(@PathVariable Integer idMascota) {

        try {
            RegistroDTO dto = registroService.obtenerRegistroDTO(idMascota);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}