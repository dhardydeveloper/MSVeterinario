package cl.duoc.atencionClinica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.atencionClinica.model.TipoAtencion;
import cl.duoc.atencionClinica.repository.TipoAtencionRepository;

@Service
public class TipoAtencionService {

    @Autowired
    private TipoAtencionRepository tipoAtencionRepository; // Inyección de dependencia del repositorio de TipoAtencion para acceder a las operaciones de base de datos relacionadas con la entidad TipoAtencion.

    // •   Listar tipos de atención  | utilizando el método findAll() del repositorio de TipoAtencion para obtener una lista completa de las entidades TipoAtencion almacenadas.
    public List<TipoAtencion> listar() {
        return tipoAtencionRepository.findAll();
    }

    // •   Buscar tipo de atención por ID
    public TipoAtencion buscarPorId(Integer id) {
        return tipoAtencionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de atención no encontrado con id: " + id));
    }

    // Método para guardar un nuevo tipo de atención en la base de datos, que primero valida la información del tipo de atención utilizando el método privado validarTipoAtencion() para asegurarse de que los datos sean correctos y completos antes de llamar al método save() del repositorio de TipoAtencion para persistir la entidad en la base de datos. Si la validación falla, se lanzará una excepción con un mensaje de error adecuado.
    public TipoAtencion guardar(TipoAtencion tipoAtencion) {
        validarTipoAtencion(tipoAtencion);
        return tipoAtencionRepository.save(tipoAtencion);
    }

    // •   Actualizar tipo de atención
    public TipoAtencion actualizar(Integer id, TipoAtencion tipoAtencion) {

        TipoAtencion tipoExistente = buscarPorId(id);

        validarTipoAtencion(tipoAtencion);

        tipoExistente.setNombreTipo(tipoAtencion.getNombreTipo());
        tipoExistente.setDescripcion(tipoAtencion.getDescripcion());
        tipoExistente.setPrecioBase(tipoAtencion.getPrecioBase());

        return tipoAtencionRepository.save(tipoExistente);
    }

    // •   Eliminar tipo de atención   |  que primero verifica si el tipo de atención existe utilizando el método existsById() del repositorio de TipoAtencion. Si el tipo de atención no existe, se lanza una excepción con un mensaje de error adecuado. Si el tipo de atención existe, se llama al método deleteById() del repositorio de TipoAtencion para eliminar la entidad de la base de datos.
    public void eliminar(Integer id) {

        if (!tipoAtencionRepository.existsById(id)) {
            throw new RuntimeException("Tipo de atención no encontrado con id: " + id);
        }

        tipoAtencionRepository.deleteById(id);
    }

    //  Método para validar la información de un tipo de atención antes de guardarlo o actualizarlo, asegurándose de que los campos obligatorios (nombre, descripción, precio base) estén presentes y sean válidos. Si alguna validación falla, se lanza una excepción con un mensaje de error adecuado para informar al usuario sobre el problema con los datos del tipo de atención.
    private void validarTipoAtencion(TipoAtencion tipoAtencion) {

        if (tipoAtencion.getNombreTipo() == null || tipoAtencion.getNombreTipo().isBlank()) {
            throw new RuntimeException("El nombre del tipo de atención es obligatorio");
        }

        if (tipoAtencion.getDescripcion() == null || tipoAtencion.getDescripcion().isBlank()) {
            throw new RuntimeException("La descripción es obligatoria");
        }

        if (tipoAtencion.getPrecioBase() == null || tipoAtencion.getPrecioBase() < 0) {
            throw new RuntimeException("El precio base debe ser mayor o igual a 0");
        }
    }
}