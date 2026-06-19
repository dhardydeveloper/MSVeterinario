package cl.duoc.usuario.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.usuario.model.Rol;
import cl.duoc.usuario.repository.RolRepository;

@Service
public class RolService {

    @Autowired 
    private RolRepository rolRepository;

    // CRUD para Rol
    
    // •  Listar roles
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    // •  Buscar rol por ID
    public Rol buscarPorId(Integer id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));
    }

    // •  Crear rol 
    public Rol guardar(Rol rol) {

        validarRol(rol);

        return rolRepository.save(rol);
    }

    // •  Actualizar rol 
    public Rol actualizar(Integer id, Rol rol) {

        Rol rolExistente = buscarPorId(id);

        validarRol(rol);

        rolExistente.setNombreRol(rol.getNombreRol());
        rolExistente.setDescripcion(rol.getDescripcion());

        return rolRepository.save(rolExistente);
    }

    // •  Eliminar rol 
    public void eliminar(Integer id) {

        if (!rolRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado con id: " + id);
        }

        rolRepository.deleteById(id);
    }

    // Validación de datos del rol
    private void validarRol(Rol rol) { 

        if (rol.getNombreRol() == null || rol.getNombreRol().isBlank()) { // Si el nombre del rol es nulo o está vacío, lanzamos una excepción
            throw new RuntimeException("El nombre del rol es obligatorio"); // thow new RuntimeException: es una forma de lanzar una excepción en Java, indicando que el nombre del rol es obligatorio
        }

        if (rol.getDescripcion() == null || rol.getDescripcion().isBlank()) { // Si la descripción del rol es nula o está vacía, lanzamos una excepción
            throw new RuntimeException("La descripción del rol es obligatoria"); // La descripción del rol es obligatoria
        }
    }
}