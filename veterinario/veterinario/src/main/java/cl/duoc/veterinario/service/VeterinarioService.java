package cl.duoc.veterinario.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.veterinario.model.Especialidad;
import cl.duoc.veterinario.model.Veterinario;
import cl.duoc.veterinario.repository.EspecialidadRepository;
import cl.duoc.veterinario.repository.VeterinarioRepository;

@Service
public class VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository; // Inyectamos el repositorio de veterinarios

    @Autowired
    private EspecialidadRepository especialidadRepository; // Inyectamos el repositorio de especialidades para validar la existencia de la especialidad al guardar o actualizar un veterinario

    // •   Listar veterinarios
    public List<Veterinario> listar() {
        return veterinarioRepository.findAll();
    }

    // •   Buscar veterinario por ID
    public Veterinario buscarPorId(Integer id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con id: " + id));
    }

    // •   Buscar veterinario por RUT
    public Veterinario buscarPorRut(String rut) {
        return veterinarioRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con rut: " + rut));
    }

    // •   Crear veterinario
    public Veterinario guardar(Veterinario veterinario) {

        // Validamos que la especialidad exista antes de guardar el veterinario
        Integer idEspecialidad = veterinario.getEspecialidad().getId();

        // Buscamos la especialidad por ID, si no existe lanzamos una excepción
        Especialidad especialidadExistente = especialidadRepository.findById(idEspecialidad)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con id: " + idEspecialidad));

        veterinario.setEspecialidad(especialidadExistente); // Asignamos la especialidad existente al veterinario

        return veterinarioRepository.save(veterinario); // Guardamos el veterinario en la base de datos con la especialidad validada
    }

    // •   Actualizar veterinario
    public Veterinario actualizar(Integer id, Veterinario veterinario) {

        Veterinario veterinarioExistente = buscarPorId(id); // Buscamos el veterinario existente por ID, si no existe lanzamos una excepción

        // Actualizamos los campos del veterinario existente con los datos del veterinario recibido en el JSON
        veterinarioExistente.setRut(veterinario.getRut());
        veterinarioExistente.setNombre(veterinario.getNombre());
        veterinarioExistente.setApellido(veterinario.getApellido());

       
        // Validamos que la especialidad exista antes de actualizar el veterinario, solo si se ha proporcionado una especialidad en el JSON
        if (veterinario.getEspecialidad() != null && veterinario.getEspecialidad().getId() != null) { // Verificamos que se ha proporcionado una especialidad con un ID válido

            Integer idEspecialidad = veterinario.getEspecialidad().getId(); // Obtenemos el ID de la especialidad proporcionada en el JSON

            Especialidad especialidadExistente = especialidadRepository.findById(idEspecialidad)
                    .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con id: " + idEspecialidad)); // Buscamos la especialidad por ID, si no existe lanzamos una excepción

            veterinarioExistente.setEspecialidad(especialidadExistente); // Asignamos la especialidad existente al veterinario existente para actualizar la relación entre el veterinario y su especialidad
        }

        return veterinarioRepository.save(veterinarioExistente); // Guardamos el veterinario actualizado en la base de datos con la especialidad validada (si se proporcionó una especialidad en el JSON)
    }

    // •   Eliminar veterinario
    public void eliminar(Integer id) {

        if (!veterinarioRepository.existsById(id)) {
            throw new RuntimeException("Veterinario no encontrado con id: " + id);
        }

        veterinarioRepository.deleteById(id);
    }
}