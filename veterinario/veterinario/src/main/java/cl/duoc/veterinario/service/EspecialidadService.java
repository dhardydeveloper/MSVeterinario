package cl.duoc.veterinario.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.veterinario.model.Especialidad;
import cl.duoc.veterinario.repository.EspecialidadRepository;

@Service
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository; // Inyectamos el repositorio de especialidades para realizar las operaciones CRUD sobre las especialidades

    // •   Listar especialidades
    public List<Especialidad> listar() {
        return especialidadRepository.findAll();
    }

    // •   Buscar especialidad por ID
    public Especialidad buscarPorId(Integer id) {
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con id: " + id));
    }

    // •  Crear especialidad
    public Especialidad guardar(Especialidad especialidad) {
        return especialidadRepository.save(especialidad);
    }

    // •   Actualizar especialidad
    public Especialidad actualizar(Integer id, Especialidad especialidad) {

        // Buscamos la especialidad existente por ID, si no existe lanzamos una excepción
        Especialidad especialidadExistente = buscarPorId(id);

        // Actualizamos los campos de la especialidad existente con los datos de la especialidad recibida en el JSON
        especialidadExistente.setNombre(especialidad.getNombre());

        // Guardamos la especialidad actualizada en la base de datos y la retornamos
        return especialidadRepository.save(especialidadExistente);
    }

    // •   Eliminar especialidad
    public void eliminar(Integer id) {

        if (!especialidadRepository.existsById(id)) {
            throw new RuntimeException("Especialidad no encontrada con id: " + id);
        }

        especialidadRepository.deleteById(id);
    }
}