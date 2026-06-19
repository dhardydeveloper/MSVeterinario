package cl.duoc.pago.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.pago.dto.PagoDTO;
import cl.duoc.pago.model.Pago;
import cl.duoc.pago.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/pagos")
@Tag(name = "Pago", description = "Endpoints para gestionar pagos") 
public class PagoController {

    @Autowired
    private PagoService pagoService;


    // •   Listar pagos
    @GetMapping // •   http://localhost:8087/api/v1/pagos
    @Operation(summary = "Listar pagos", 
               description = "Retorna una lista de todos los pagos registrados en el sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente"),
                           @ApiResponse(responseCode = "204", description = "No se encontraron pagos registrados"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Pago>> listar() {

        List<Pago> lista = pagoService.listar();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    // •   Buscar pago por ID
    @GetMapping("/{id}") // •   http://localhost:8087/api/v1/pagos/{id}
    @Operation(summary = "Buscar pago por ID", 
               description = "Retorna un pago específico por su identificador")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Pago encontrado exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Pago> buscarPorId(@PathVariable Integer id) {

        try {
            Pago pago = pagoService.buscarPorId(id);
            return ResponseEntity.ok(pago);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // •   Ver DTO pago
    @GetMapping("/dto/{id}") // •   http://localhost:8087/api/v1/pagos/dto/1
    @Operation(summary = "Obtener DTO de pago", 
               description = "Retorna un objeto DTO con información detallada de un pago específico buscando por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "DTO de pago obtenido exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<PagoDTO> obtenerDTO(@PathVariable Integer id) {

        try {
            PagoDTO dto = pagoService.obtenerPagoDTO(id);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // •   Busca pagos por ID atención clínica.
    @GetMapping("/atencion/{idAtencion}") // •   http://localhost:8087/api/v1/pagos/atencion/1
    @Operation(summary = "Buscar pagos por ID de atención clínica", 
               description = "Retorna una lista de pagos asociados a una atención clínica específica buscando por el ID de la atención")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente"),
                           @ApiResponse(responseCode = "204", description = "No se encontraron pagos para la atención clínica especificada"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Pago>> buscarPorAtencion(@PathVariable Integer idAtencion) {

        List<Pago> lista = pagoService.buscarPorAtencion(idAtencion);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    // •   Busca pagos por estado de pago.
    @GetMapping("/estado/{estadoPago}") // •   http://localhost:8087/api/v1/pagos/estado/PAGADO
    @Operation(summary = "Buscar pagos por estado de pago", 
               description = "Retorna una lista de pagos según su estado de pago (PAGADO, PENDIENTE, ANULADO, etc.) buscando por el estado como parámetro")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente"),
                           @ApiResponse(responseCode = "204", description = "No se encontraron pagos para el estado especificado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<Pago>> buscarPorEstado(@PathVariable String estadoPago) {

        List<Pago> lista = pagoService.buscarPorEstado(estadoPago);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    // •   Crear pago
    @PostMapping // •   http://localhost:8087/api/v1/pagos
    @Operation(summary = "Crear pago", 
               description = "Permite crear un nuevo pago en el sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Pago creado exitosamente"),
                           @ApiResponse(responseCode = "400", description = "Datos de pago inválidos"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Pago> guardar(@RequestBody Pago pago) {

        try {
            Pago nuevoPago = pagoService.guardar(pago);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPago);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // •   Actualizar pago
    @PutMapping("/{id}") // •   http://localhost:8087/api/v1/pagos/{id}
    @Operation(summary = "Actualizar pago", 
               description = "Permite actualizar un pago existente en el sistema buscando por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente"),
                           @ApiResponse(responseCode = "400", description = "Datos de pago inválidos"),
                           @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Pago> actualizar(
            @PathVariable Integer id,
            @RequestBody Pago pago) {

        try {
            Pago pagoActualizado = pagoService.actualizar(id, pago);
            return ResponseEntity.ok(pagoActualizado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // •   Eliminar pago
    @DeleteMapping("/{id}") // •   http://localhost:8087/api/v1/pagos/{id}
    @Operation(summary = "Eliminar pago", 
               description = "Permite eliminar un pago existente en el sistema buscando por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Pago eliminado exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            pagoService.eliminar(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}