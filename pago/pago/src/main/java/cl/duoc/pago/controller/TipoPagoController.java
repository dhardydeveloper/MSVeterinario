package cl.duoc.pago.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.pago.model.TipoPago;
import cl.duoc.pago.service.TipoPagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/tipo-pagos")
@Tag(name = "TipoPago", description = "Endpoints para gestionar tipos de pago")
public class TipoPagoController {

    @Autowired
    private TipoPagoService tipoPagoService;

    // •  Listar tipo pagos
    @GetMapping // •  http://localhost:8087/api/v1/tipo-pagos
    @Operation(summary = "Listar tipos de pago", 
               description = "Retorna una lista de todos los tipos de pago disponibles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de tipos de pago obtenida exitosamente"),
                           @ApiResponse(responseCode = "204", description = "No se encontraron tipos de pago registrados"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<List<TipoPago>> listar() {

        List<TipoPago> lista = tipoPagoService.listar();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    // •  Buscar tipo pago por ID
    @GetMapping("/{id}") // •  http://localhost:8087/api/v1/tipo-pagos/{id}
    @Operation(summary = "Buscar tipo de pago por ID", 
               description = "Retornaun tipo de pago específico por su identificador (ID)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Tipo de pago obtenido exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Tipo de pago no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<TipoPago> buscarPorId(@PathVariable Integer id) {

        try {
            TipoPago tipoPago = tipoPagoService.buscarPorId(id);
            return ResponseEntity.ok(tipoPago);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // •  Crear tipo pago
    @PostMapping // •  http://localhost:8087/api/v1/tipo-pagos
    @Operation(summary = "Crear tipo de pago", 
               description = "Permite crear un nuevo tipo de pago en el sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Tipo de pago creado exitosamente"),
                           @ApiResponse(responseCode = "400", description = "Datos de tipo de pago inválidos"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<TipoPago> guardar(@RequestBody TipoPago tipoPago) {

        try {
            TipoPago nuevoTipoPago = tipoPagoService.guardar(tipoPago);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTipoPago);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // •  Actualizar tipo pago
    @PutMapping("/{id}") // •  http://localhost:8087/api/v1/tipo-pagos/{id}
    @Operation(summary = "Actualizar tipo de pago", 
               description = "Permite actualizar un tipo de pago existente en el sistema buscando por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Tipo de pago actualizado exitosamente"),
                           @ApiResponse(responseCode = "400", description = "Datos de tipo de pago inválidos"),
                           @ApiResponse(responseCode = "404", description = "Tipo de pago no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<TipoPago> actualizar(
            @PathVariable Integer id,
            @RequestBody TipoPago tipoPago) {

        try {
            TipoPago tipoPagoActualizado = tipoPagoService.actualizar(id, tipoPago);
            return ResponseEntity.ok(tipoPagoActualizado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // •  Eliminar tipo pago
    @DeleteMapping("/{id}") // •  http://localhost:8087/api/v1/tipo-pagos/{id}
    @Operation(summary = "Eliminar tipo de pago", 
               description = "Permite eliminar un tipo de pago existente en el sistema buscando por su ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Tipo de pago eliminado exitosamente"),
                           @ApiResponse(responseCode = "404", description = "Tipo de pago no encontrado"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            tipoPagoService.eliminar(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}