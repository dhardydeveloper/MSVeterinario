package cl.duoc.fichaclinica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.fichaclinica.model.Medicamento;
import cl.duoc.fichaclinica.service.MedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/medicamentos")
@Tag(name = "Medicamento", description = "Operaciones relacionadas con los medicamentos utilizados en las fichas clínicas de las mascotas") 
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService; // Inyección de dependencia del servicio

    

    // •   Listar medicamentos
    @GetMapping // •   http://localhost:8086/api/v1/medicamentos
    @Operation(summary = "Listar medicamentos", 
    description = "Obtiene una lista de todos los medicamentos registrados en el sistema") 
    public ResponseEntity<List<Medicamento>> listar() {

        List<Medicamento> lista = medicamentoService.listar();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si la lista está vacía, se devuelve un código 204 No Content
        }

        return ResponseEntity.ok(lista); // Si la lista no está vacía, se devuelve un código 200 OK con la lista de medicamentos en el cuerpo de la respuesta
    }

    // •   Buscar medicamento por ID
    @GetMapping("/{id}") // •   http://localhost:8086/api/v1/medicamentos/{id}
    @Operation(summary = "Buscar medicamento por ID", 
    description = "Obtiene un medicamento específico utilizando su ID")
    public ResponseEntity<Medicamento> buscarPorId(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(medicamentoService.buscarPorId(id)); // Si se encuentra el medicamento, se devuelve un código 200 OK con el medicamento en el cuerpo de la respuesta
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si no se encuentra el medicamento, se devuelve un código 404 Not Found
        }
    }

    // •   Crear medicamento
    @PostMapping // •   http://localhost:8086/api/v1/medicamentos
    @Operation(summary = "Crear medicamento", 
    description = "Crea un nuevo medicamento utilizando los datos proporcionados en el cuerpo de la solicitud")
    public ResponseEntity<Medicamento> guardar(@RequestBody Medicamento medicamento) {

        try {
            Medicamento nuevo = medicamentoService.guardar(medicamento); // Llamada al servicio para guardar el nuevo medicamento
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo); // Si el medicamento se crea correctamente, se devuelve un código 201 Created con el medicamento creado en el cuerpo de la respuesta
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Si hay un error al crear el medicamento, se devuelve un código 400 Bad Request
        }
    }

    // •   Actualizar medicamento 
    @PutMapping("/{id}") // •   http://localhost:8086/api/v1/medicamentos/{id}
    @Operation(summary = "Actualizar medicamento", 
    description = "Actualiza un medicamento existente utilizando su ID y los datos proporcionados en el cuerpo de la solicitud")
    public ResponseEntity<Medicamento> actualizar(
            @PathVariable Integer id,
            @RequestBody Medicamento medicamento) {

        try {
            return ResponseEntity.ok(medicamentoService.actualizar(id, medicamento)); // Si el medicamento se actualiza correctamente, se devuelve un código 200 OK con el medicamento actualizado en el cuerpo de la respuesta
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Si hay un error al actualizar el medicamento, se devuelve un código 400 Bad Request
        }
    }

    // •   Eliminar medicamento
    @DeleteMapping("/{id}") // •   http://localhost:8086/api/v1/medicamentos/{id}
    @Operation(summary = "Eliminar medicamento", 
    description = "Elimina un medicamento existente utilizando su ID")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        try {
            medicamentoService.eliminar(id);
            return ResponseEntity.noContent().build(); // Si el medicamento se elimina correctamente, se devuelve un código 204 No Content
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si no se encuentra el medicamento, se devuelve un código 404 Not Found
        }
    }
}