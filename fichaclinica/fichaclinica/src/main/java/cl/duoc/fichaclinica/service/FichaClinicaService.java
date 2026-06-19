package cl.duoc.fichaclinica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.fichaclinica.dto.FichaClinicaDTO;
import cl.duoc.fichaclinica.model.FichaClinica;
import cl.duoc.fichaclinica.repository.FichaClinicaRepository;

@Service
public class FichaClinicaService {

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository; // Inyectar el repositorio de fichas clínicas utilizando la anotación @Autowired para poder acceder a los métodos de acceso a datos proporcionados por Spring Data JPA


    // •   Listar fichas clínicas
    public List<FichaClinica> listar() {
        return fichaClinicaRepository.findAll();
    }

    // •   Buscar ficha clínica por ID
    public FichaClinica buscarPorId(Integer id) {
        return fichaClinicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ficha clínica no encontrada con id: " + id));
    }

    // •   Buscar fichas clínicas por ID mascota
    public List<FichaClinica> buscarPorMascota(Integer idMascota) {
        return fichaClinicaRepository.findByIdMascota(idMascota);
    }

    // •   Crear ficha
    public FichaClinica guardar(FichaClinica fichaClinica) {

        validarFicha(fichaClinica); 

        return fichaClinicaRepository.save(fichaClinica);
    }

    // •   Actualizar ficha clínica
    public FichaClinica actualizar(Integer id, FichaClinica fichaClinica) {

        FichaClinica fichaExistente = buscarPorId(id); // Buscar la ficha clínica existente por su ID utilizando el método buscarPorId, lanzando una excepción si no se encuentra una ficha clínica con el ID proporcionado

        validarFicha(fichaClinica); // Validar los datos de la ficha clínica utilizando el método validarFicha, lanzando una excepción si no se pueden validar los datos

        // Actualizar los campos de la ficha clínica existente con los datos de la ficha clínica proporcionada
        fichaExistente.setIdMascota(fichaClinica.getIdMascota());
        fichaExistente.setAntecedentes(fichaClinica.getAntecedentes());
        fichaExistente.setAlergias(fichaClinica.getAlergias());
        fichaExistente.setEnfermedadesPrevias(fichaClinica.getEnfermedadesPrevias());
        fichaExistente.setObservaciones(fichaClinica.getObservaciones());
        fichaExistente.setFechaCreacion(fichaClinica.getFechaCreacion());

        return fichaClinicaRepository.save(fichaExistente); // Guardar la ficha clínica actualizada en la base de datos utilizando el método save proporcionado por Spring Data JPA, que actualiza la entidad si ya existe o la crea si no existe
    }

    // •   Eliminar ficha clínica 
    public void eliminar(Integer id) {

        if (!fichaClinicaRepository.existsById(id)) {
            throw new RuntimeException("Ficha clínica no encontrada con id: " + id);
        }

        fichaClinicaRepository.deleteById(id); // Eliminar la ficha clínica de la base de datos utilizando el método deleteById proporcionado por Spring Data JPA, que elimina la entidad con el ID especificado
    }

    // •   DTO ficha clínica por ID
    public FichaClinicaDTO obtenerFichaDTO(Integer idFicha) {

        FichaClinica ficha = buscarPorId(idFicha); // Buscar la ficha clínica por su ID utilizando el método buscarPorId, lanzando una excepción si no se encuentra una ficha clínica con el ID proporcionado

        FichaClinicaDTO dto = new FichaClinicaDTO(); // Crear un nuevo objeto DTO para almacenar los datos de la ficha clínica en un formato adecuado para ser enviado a través de la capa de presentación o a otros servicios

        // Mapear los campos de la ficha clínica al DTO, asignando los valores correspondientes a cada campo del DTO utilizando los getters de la entidad FichaClinica para obtener los valores de los campos y los setters del DTO para asignar esos valores a los campos del DTO
        dto.setIdFicha(ficha.getIdFicha());
        dto.setIdMascota(ficha.getIdMascota());
        dto.setAntecedentes(ficha.getAntecedentes());
        dto.setAlergias(ficha.getAlergias());
        dto.setEnfermedadesPrevias(ficha.getEnfermedadesPrevias());
        dto.setObservaciones(ficha.getObservaciones());
        dto.setFechaCreacion(ficha.getFechaCreacion());

        return dto; // Devolver el DTO con los datos de la ficha clínica, que puede ser utilizado para ser enviado a través de la capa de presentación o a otros servicios, proporcionando una representación más adecuada y segura de los datos de la ficha clínica al ocultar detalles innecesarios o sensibles que podrían estar presentes en la entidad FichaClinica original
    }

    // Validar los datos de una ficha clínica para asegurarse de que se están proporcionando los datos básicos necesarios para crear o actualizar una ficha clínica, como el ID de la mascota a la que pertenece la ficha clínica y la fecha de creación de la ficha clínica, lanzando una excepción si alguno de estos datos no se ha proporcionado o es inválido
    private void validarFicha(FichaClinica fichaClinica) {

        if (fichaClinica.getIdMascota() == null) {
            throw new RuntimeException("El id de la mascota es obligatorio");
        }

        if (fichaClinica.getFechaCreacion() == null) {
            throw new RuntimeException("La fecha de creación es obligatoria");
        }
    }
}