package cl.duoc.fichaclinica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.fichaclinica.model.Medicamento;
import cl.duoc.fichaclinica.repository.MedicamentoRepository;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository; // Inyectar el repositorio de medicamentos utilizando la anotación @Autowired para poder acceder a los métodos de acceso a datos proporcionados por Spring Data JPA


    // •   Listar medicamentos
    public List<Medicamento> listar() {
        return medicamentoRepository.findAll();
    }

    // •   Buscar medicamento por ID
    public Medicamento buscarPorId(Integer id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado con id: " + id));
    }

    // •   Crear medicamento
    public Medicamento guardar(Medicamento medicamento) {

        validarMedicamento(medicamento);

        return medicamentoRepository.save(medicamento);
    }

    // •   Actualizar medicamento 
    public Medicamento actualizar(Integer id, Medicamento medicamento) {

        Medicamento medicamentoExistente = buscarPorId(id); // Buscar el medicamento existente por su ID utilizando el método buscarPorId, lanzando una excepción si no se encuentra un medicamento con el ID proporcionado

        validarMedicamento(medicamento); // Validar los datos del medicamento utilizando el método validarMedicamento, lanzando una excepción si no se pueden validar los datos

        // Actualizar los campos del medicamento existente con los datos del medicamento proporcionado
        medicamentoExistente.setNombreMedicamento(medicamento.getNombreMedicamento());
        medicamentoExistente.setDescripcion(medicamento.getDescripcion());
        medicamentoExistente.setDosisRecomendada(medicamento.getDosisRecomendada());

        return medicamentoRepository.save(medicamentoExistente); // Guardar el medicamento actualizado en la base de datos utilizando el método save proporcionado por Spring Data JPA, que actualiza la entidad si ya existe o la crea si no existe
    }

    // •   Eliminar medicamento
    public void eliminar(Integer id) {

        if (!medicamentoRepository.existsById(id)) {
            throw new RuntimeException("Medicamento no encontrado con id: " + id);
        }

        medicamentoRepository.deleteById(id);
    }

    // Validar los datos de un medicamento para asegurarse de que se están proporcionando los datos básicos necesarios para crear o actualizar un medicamento, como el nombre del medicamento, la descripción y la dosis recomendada, lanzando una excepción si alguno de estos datos no se ha proporcionado o es inválido
    private void validarMedicamento(Medicamento medicamento) {

        if (medicamento.getNombreMedicamento() == null || medicamento.getNombreMedicamento().isBlank()) {
            throw new RuntimeException("El nombre del medicamento es obligatorio");
        }

        if (medicamento.getDescripcion() == null || medicamento.getDescripcion().isBlank()) {
            throw new RuntimeException("La descripción del medicamento es obligatoria");
        }

        if (medicamento.getDosisRecomendada() == null || medicamento.getDosisRecomendada().isBlank()) {
            throw new RuntimeException("La dosis recomendada es obligatoria");
        }
    }
}