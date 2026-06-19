package cl.duoc.atencionClinica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.atencionClinica.model.Box;
import cl.duoc.atencionClinica.repository.BoxRepository;

@Service
public class BoxService {

    @Autowired
    private BoxRepository boxRepository; // Inyección de dependencia del repositorio de Box para acceder a las operaciones de base de datos relacionadas con la entidad Box.

    // •   Listar boxes 
    public List<Box> listar() {
        return boxRepository.findAll();
    }

    // •   Buscar box por ID
    public Box buscarPorId(Integer id) {
        return boxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Box no encontrado con id: " + id));
    }

    // •   Crear box
    public Box guardar(Box box) {
        validarBox(box);
        return boxRepository.save(box);
    }

    // •   Actualizar box 
    public Box actualizar(Integer id, Box box) {

        Box boxExistente = buscarPorId(id);

        validarBox(box);

        boxExistente.setNombreBox(box.getNombreBox());
        boxExistente.setDescripcion(box.getDescripcion());
        boxExistente.setEstado(box.getEstado());

        return boxRepository.save(boxExistente);
    }

    // •   Eliminar box 
    public void eliminar(Integer id) {

        if (!boxRepository.existsById(id)) {
            throw new RuntimeException("Box no encontrado con id: " + id);
        }

        boxRepository.deleteById(id);
    }

    // Método para validar la información de un box antes de guardarlo o actualizarlo, asegurándose de que los campos obligatorios (nombre, descripción, estado) estén presentes y no estén vacíos. Si alguna validación falla, se lanza una excepción con un mensaje de error adecuado para informar al usuario sobre el problema con los datos del box.
    private void validarBox(Box box) {

        if (box.getNombreBox() == null || box.getNombreBox().isBlank()) { // Validar que el nombre del box no sea nulo ni esté vacío, ya que es un campo obligatorio para identificar el box. Si la validación falla, se lanza una excepción con un mensaje de error adecuado.
            throw new RuntimeException("El nombre del box es obligatorio");
        }

        if (box.getDescripcion() == null || box.getDescripcion().isBlank()) { // Validar que la descripción del box no sea nula ni esté vacía, ya que es un campo obligatorio para proporcionar información adicional sobre el box. Si la validación falla, se lanza una excepción con un mensaje de error adecuado.
            throw new RuntimeException("La descripción del box es obligatoria");
        }

        if (box.getEstado() == null || box.getEstado().isBlank()) { // Validar que el estado del box no sea nulo ni esté vacío, ya que es un campo obligatorio para indicar la disponibilidad o el estado actual del box. Si la validación falla, se lanza una excepción con un mensaje de error adecuado.
            throw new RuntimeException("El estado del box es obligatorio");
        }
    }
}