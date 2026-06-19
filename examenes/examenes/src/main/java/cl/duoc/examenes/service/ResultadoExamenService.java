package cl.duoc.examenes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.examenes.model.OrdenExamen;
import cl.duoc.examenes.model.ResultadoExamen;
import cl.duoc.examenes.repository.OrdenExamenRepository;
import cl.duoc.examenes.repository.ResultadoExamenRepository;

@Service
public class ResultadoExamenService {

    @Autowired
    private ResultadoExamenRepository resultadoExamenRepository; // Inyección de la dependencia del repositorio de resultados de examen para realizar operaciones de acceso a datos

    @Autowired
    private OrdenExamenRepository ordenExamenRepository; // Inyección de la dependencia del repositorio de órdenes de examen para validar la existencia de la orden de examen asociada al resultado de examen


    // •   Listar resultados de examenes
    public List<ResultadoExamen> listar() {
        return resultadoExamenRepository.findAll(); // findAll() devuelve una lista de todos los registros de la entidad ResultadoExamen en la base de datos
    }

    // •   Buscar resultado de examen por ID
    public ResultadoExamen buscarPorId(Integer id) {
        return resultadoExamenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resultado de examen no encontrado con id: " + id));
    }

    // •   Buscar resultados de examen asociados a una orden de examen específica por ID 
    public List<ResultadoExamen> buscarPorOrden(Integer idOrdenExamen) {
        return resultadoExamenRepository.findByOrdenExamenIdOrdenExamen(idOrdenExamen);
    }

    // •   Crear resultado
    public ResultadoExamen guardar(ResultadoExamen resultadoExamen) {

        validarResultado(resultadoExamen); // Validar que el resultado de examen tenga los datos obligatorios y que la orden de examen asociada sea válida

        OrdenExamen ordenExistente = obtenerOrden(resultadoExamen); // Obtener la orden de examen existente asociada al resultado de examen

        resultadoExamen.setOrdenExamen(ordenExistente); // Establecer la orden de examen existente en el resultado de examen para asegurar la integridad referencial

        return resultadoExamenRepository.save(resultadoExamen); // Guardar el resultado de examen en la base de datos utilizando el repositorio, si se guarda correctamente se devuelve el resultado de examen guardado con su ID generado por la base de datos
    }

    // •   Actualizar resultado de examen
    public ResultadoExamen actualizar(Integer id, ResultadoExamen resultadoExamen) {

        ResultadoExamen resultadoExistente = buscarPorId(id); // Buscar el resultado de examen existente por su ID, si no se encuentra se lanza una excepción con un mensaje descriptivo del error

        validarResultado(resultadoExamen); // Validar que el resultado de examen tenga los datos obligatorios y que la orden de examen asociada sea válida

        OrdenExamen ordenExistente = obtenerOrden(resultadoExamen); // Obtener la orden de examen existente asociada al resultado de examen

        // Actualizar los campos del resultado de examen existente con los datos del resultado de examen recibido como parámetro
        resultadoExistente.setOrdenExamen(ordenExistente);
        resultadoExistente.setResultado(resultadoExamen.getResultado());
        resultadoExistente.setObservacion(resultadoExamen.getObservacion());
        resultadoExistente.setFechaResultado(resultadoExamen.getFechaResultado());

        return resultadoExamenRepository.save(resultadoExistente); // Guardar el resultado de examen actualizado
    }

    // •   Eliminar resultado de examen
    public void eliminar(Integer id) {

        if (!resultadoExamenRepository.existsById(id)) {
            throw new RuntimeException("Resultado de examen no encontrado con id: " + id);
        }

        resultadoExamenRepository.deleteById(id); // Eliminar el resultado de examen de la base de datos utilizando el repositorio, si se elimina correctamente no se devuelve ningún valor
    }

    // Validar que el resultado de examen tenga los datos obligatorios y que la orden de examen asociada sea válida, si no se cumple alguna validación se lanza una excepción con un mensaje descriptivo del error
    private void validarResultado(ResultadoExamen resultadoExamen) {

        if (resultadoExamen.getOrdenExamen() == null || resultadoExamen.getOrdenExamen().getIdOrdenExamen() == null) {
            throw new RuntimeException("Debe indicar una orden de examen válida");
        }

        if (resultadoExamen.getResultado() == null || resultadoExamen.getResultado().isBlank()) {
            throw new RuntimeException("El resultado es obligatorio");
        }

        if (resultadoExamen.getFechaResultado() == null) {
            throw new RuntimeException("La fecha del resultado es obligatoria");
        }
    }

    // Validar que la orden de examen asociada al resultado de examen exista en la base de datos y obtener la orden de examen existente, si no se encuentra se lanza una excepción con un mensaje descriptivo del error
    private OrdenExamen obtenerOrden(ResultadoExamen resultadoExamen) {

        Integer idOrden = resultadoExamen.getOrdenExamen().getIdOrdenExamen(); // Obtener el ID de la orden de examen asociada al resultado de examen

        return ordenExamenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden de examen no encontrada con id: " + idOrden)); // Buscar la orden de examen por su ID utilizando el repositorio, si se encuentra se devuelve la orden de examen existente, si no se encuentra se lanza una excepción con un mensaje descriptivo del error
    }
}