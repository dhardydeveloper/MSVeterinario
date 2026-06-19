package cl.duoc.examenes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.examenes.model.TipoExamen;
import cl.duoc.examenes.repository.TipoExamenRepository;

@Service
public class TipoExamenService {

    @Autowired
    private TipoExamenRepository tipoExamenRepository; // Inyección de la dependencia del repositorio de tipos de examen para realizar operaciones de acceso a datos

    
    // •   Listar tipos de examenes
    public List<TipoExamen> listar() {
        return tipoExamenRepository.findAll();
    }

    // •   Buscar tipo de examen por ID
    public TipoExamen buscarPorId(Integer id) {
        return tipoExamenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de examen no encontrado con id: " + id));
    }

    // •   Crear tipo examen
    public TipoExamen guardar(TipoExamen tipoExamen) {

        validarTipoExamen(tipoExamen); // Validar que el tipo de examen tenga los datos obligatorios, si no se cumple alguna validación se lanza una excepción con un mensaje descriptivo del error

        return tipoExamenRepository.save(tipoExamen); // Guardar el tipo de examen en la base de datos utilizando el repositorio, si se guarda correctamente se devuelve el tipo de examen guardado con su ID generado por la base de datos
    }

    // •   Actualizar tipo de examen 
    public TipoExamen actualizar(Integer id, TipoExamen tipoExamen) {

        TipoExamen tipoExistente = buscarPorId(id); // Buscar el tipo de examen existente por su ID, si no se encuentra se lanza una excepción con un mensaje descriptivo del error

        validarTipoExamen(tipoExamen); // Validar que el tipo de examen tenga los datos obligatorios, si no se cumple alguna validación se lanza una excepción con un mensaje descriptivo del error

        // Actualizar los campos del tipo de examen existente con los datos del tipo de examen recibido como parámetro
        tipoExistente.setNombreExamen(tipoExamen.getNombreExamen());
        tipoExistente.setDescripcion(tipoExamen.getDescripcion());
        tipoExistente.setPrecio(tipoExamen.getPrecio());

        return tipoExamenRepository.save(tipoExistente); // Guardar el tipo de examen actualizado
    }

    // •   Eliminar un tipo de examen
    public void eliminar(Integer id) {

        if (!tipoExamenRepository.existsById(id)) {
            throw new RuntimeException("Tipo de examen no encontrado con id: " + id);
        }

        tipoExamenRepository.deleteById(id); // Eliminar el tipo de examen de la base de datos utilizando el repositorio, si se elimina correctamente no se devuelve ningún valor
    }

    // Validar que el tipo de examen tenga los datos obligatorios, si no se cumple alguna validación se lanza una excepción con un mensaje descriptivo del error
    private void validarTipoExamen(TipoExamen tipoExamen) {

        if (tipoExamen.getNombreExamen() == null || tipoExamen.getNombreExamen().isBlank()) {
            throw new RuntimeException("El nombre del examen es obligatorio");
        }

        if (tipoExamen.getDescripcion() == null || tipoExamen.getDescripcion().isBlank()) {
            throw new RuntimeException("La descripción del examen es obligatoria");
        }

        if (tipoExamen.getPrecio() == null || tipoExamen.getPrecio() < 0) {
            throw new RuntimeException("El precio debe ser mayor o igual a cero");
        }
    }
}