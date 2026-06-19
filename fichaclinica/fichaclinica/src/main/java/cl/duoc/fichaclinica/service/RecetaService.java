package cl.duoc.fichaclinica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.duoc.fichaclinica.dto.AtencionDTO;
import cl.duoc.fichaclinica.dto.RecetaDTO;
import cl.duoc.fichaclinica.model.Medicamento;
import cl.duoc.fichaclinica.model.Receta;
import cl.duoc.fichaclinica.repository.MedicamentoRepository;
import cl.duoc.fichaclinica.repository.RecetaRepository;

@Service
public class RecetaService {

    @Autowired
    private RecetaRepository recetaRepository; // Inyección del repositorio de recetas

    @Autowired
    private MedicamentoRepository medicamentoRepository; // Inyección del repositorio de medicamentos para validar la existencia del medicamento asociado a la receta

    @Autowired
    private RestTemplate restTemplate; // Inyección de RestTemplate para realizar llamadas HTTP a otros microservicios (en este caso, al microservicio de atenciones)

    private final String URL_ATENCION = "http://localhost:8084/api/v1/atenciones"; // URL base del microservicio de atenciones 

    

    // •   Listar recetas
    public List<Receta> listar() {
        return recetaRepository.findAll();
    }

    // •   Buscar receta por ID
    public Receta buscarPorId(Integer id) {
        return recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con id: " + id));
    }

    // •   Buscar recetas por ID de atención
    public List<Receta> buscarPorAtencion(Integer idAtencion) {
        return recetaRepository.findByIdAtencion(idAtencion);
    }

    // •   Buscar recetas por ID de mascota
    public List<Receta> buscarPorMascota(Integer idMascota) {
        return recetaRepository.findByIdMascota(idMascota);
    }

    // •   Buscar recetas por ID de veterinario
    public List<Receta> buscarPorVeterinario(Integer idVeterinario) {
        return recetaRepository.findByIdVeterinario(idVeterinario);
    }

    // •   Crear receta
    public Receta guardar(Receta receta) {

        validarDatosBasicos(receta); // Validar que se hayan proporcionado los datos básicos necesarios para crear una receta

        AtencionDTO atencion = validarAtencion(receta.getIdAtencion()); // Validar que la atención asociada a la receta exista en el microservicio de atenciones

        validarCoincidenciaConAtencion(receta, atencion); // Validar que la mascota y el veterinario de la receta coincidan con los datos de la atención obtenida del microservicio de atenciones

        Medicamento medicamentoExistente = obtenerMedicamento(receta); // Validar que el medicamento asociado a la receta exista en la base de datos y obtener su información completa para asociarla a la receta

        receta.setMedicamento(medicamentoExistente); // Asociar el medicamento existente a la receta antes de guardarla

        return recetaRepository.save(receta); // Guardar la receta en la base de datos y devolver la receta guardada con su ID generado
    }

    // •   Actualizar receta
    public Receta actualizar(Integer id, Receta receta) {

        Receta recetaExistente = buscarPorId(id); // Buscar la receta existente por su ID para asegurarse de que existe antes de intentar actualizarla

        validarDatosBasicos(receta); // Validar que se hayan proporcionado los datos básicos necesarios para actualizar una receta

        AtencionDTO atencion = validarAtencion(receta.getIdAtencion()); // Validar que la atención asociada a la receta exista en el microservicio de atenciones para asegurarse de que se está asociando a una atención válida

        validarCoincidenciaConAtencion(receta, atencion); // Validar que la mascota y el veterinario de la receta coincidan con los datos de la atención obtenida del microservicio de atenciones para asegurarse de que se está asociando a la atención correcta

        Medicamento medicamentoExistente = obtenerMedicamento(receta); // Validar que el medicamento asociado a la receta exista en la base de datos y obtener su información completa para asociarla a la receta actualizada

        // Actualizar los campos de la receta existente con los nuevos valores proporcionados en el objeto receta recibido como parámetro, manteniendo el ID de la receta existente para asegurarse de que se está actualizando la receta correcta
        recetaExistente.setIdAtencion(receta.getIdAtencion());
        recetaExistente.setIdMascota(receta.getIdMascota());
        recetaExistente.setIdVeterinario(receta.getIdVeterinario());
        recetaExistente.setMedicamento(medicamentoExistente);
        recetaExistente.setFechaEmision(receta.getFechaEmision());
        recetaExistente.setIndicaciones(receta.getIndicaciones());

        return recetaRepository.save(recetaExistente); // Guardar la receta actualizada en la base de datos y devolver la receta actualizada con su ID
    }

    // •   Eliminar receta 
    public void eliminar(Integer id) {

        if (!recetaRepository.existsById(id)) {
            throw new RuntimeException("Receta no encontrada con id: " + id); // Verificar si la receta existe antes de intentar eliminarla para evitar errores al intentar eliminar una receta que no existe
        }

        recetaRepository.deleteById(id); // Eliminar la receta de la base de datos utilizando el método deleteById proporcionado por Spring Data JPA, que elimina la entidad con el ID especificado
    }

    // Validar que se hayan proporcionado los datos básicos necesarios para crear o actualizar una receta, como el ID de atención, el ID de mascota, el ID de veterinario, el medicamento asociado, la fecha de emisión y las indicaciones, lanzando una excepción si alguno de estos datos no se ha proporcionado o es inválido
    private void validarDatosBasicos(Receta receta) { 

        if (receta.getIdAtencion() == null) {
            throw new RuntimeException("El id de atención es obligatorio");
        }

        if (receta.getIdMascota() == null) {
            throw new RuntimeException("El id de mascota es obligatorio");
        }

        if (receta.getIdVeterinario() == null) {
            throw new RuntimeException("El id de veterinario es obligatorio");
        }

        if (receta.getMedicamento() == null || receta.getMedicamento().getIdMedicamento() == null) {
            throw new RuntimeException("Debe indicar un medicamento válido");
        }

        if (receta.getFechaEmision() == null) {
            throw new RuntimeException("La fecha de emisión es obligatoria");
        }

        if (receta.getIndicaciones() == null || receta.getIndicaciones().isBlank()) {
            throw new RuntimeException("Las indicaciones son obligatorias");
        }
    }

    // Validar que la atención asociada a la receta exista en el microservicio de atenciones realizando una llamada HTTP utilizando RestTemplate para obtener los datos de la atención y asegurarse de que se está asociando a una atención válida, lanzando una excepción si no se puede validar la atención o si la atención no existe
    private AtencionDTO validarAtencion(Integer idAtencion) {

        try {
            String url = URL_ATENCION + "/dto/" + idAtencion; // Construir la URL completa para realizar la llamada HTTP al microservicio de atenciones, agregando el ID de la atención al final de la URL para obtener los datos específicos de esa atención en formato DTO (Data Transfer Object)
            return restTemplate.getForObject(url, AtencionDTO.class); // Realizar una llamada HTTP GET al microservicio de atenciones utilizando RestTemplate para obtener los datos de la atención en formato DTO (Data Transfer Object) y devolver el objeto AtencionDTO obtenido de la respuesta del microservicio

        } catch (Exception e) {
            throw new RuntimeException("No se pudo validar la atención con id: " + idAtencion); // Si ocurre cualquier excepción durante la llamada HTTP o si la atención no existe, lanzar una excepción indicando que no se pudo validar la atención con el ID proporcionado
        }
    }

    // Validar que la mascota y el veterinario de la receta coincidan con los datos de la atención obtenida del microservicio de atenciones para asegurarse de que se está asociando a la atención correcta, lanzando una excepción si no coinciden
    private void validarCoincidenciaConAtencion(Receta receta, AtencionDTO atencion) {

        if (!receta.getIdMascota().equals(atencion.getIdMascota())) {
            throw new RuntimeException("La mascota de la receta no coincide con la mascota de la atención");
        }

        if (!receta.getIdVeterinario().equals(atencion.getIdVeterinario())) {
            throw new RuntimeException("El veterinario de la receta no coincide con el veterinario de la atención");
        }
    }

    // Validar que el medicamento asociado a la receta exista en la base de datos y obtener su información completa para asociarla a la receta, lanzando una excepción si el medicamento no existe
    private Medicamento obtenerMedicamento(Receta receta) {

        Integer idMedicamento = receta.getMedicamento().getIdMedicamento();

        return medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado con id: " + idMedicamento));
    }

    // •   Ver DTO receta | información completa de una receta, incluyendo los datos de la atención asociada, la mascota, el veterinario y el medicamento, realizando las validaciones necesarias para asegurarse de que se está obteniendo la información correcta y lanzando excepciones si no se pueden validar los datos o si la receta no existe
    public RecetaDTO obtenerRecetaDTO(Integer idReceta) {

        Receta receta = buscarPorId(idReceta);

        AtencionDTO atencion = validarAtencion(receta.getIdAtencion());

        RecetaDTO dto = new RecetaDTO();

        dto.setIdReceta(receta.getIdReceta());

        dto.setIdAtencion(receta.getIdAtencion());
        dto.setIdMascota(receta.getIdMascota());
        dto.setNombreMascota(atencion.getNombreMascota());
        dto.setEspecieMascota(atencion.getEspecieMascota());
        dto.setRazaMascota(atencion.getRazaMascota());

        dto.setNombreCliente(atencion.getNombreCliente());
        dto.setCorreoCliente(atencion.getCorreoCliente());

        dto.setIdVeterinario(receta.getIdVeterinario());
        dto.setNombreVeterinario(atencion.getNombreVeterinario());
        dto.setEspecialidadVeterinario(atencion.getEspecialidadVeterinario());

        dto.setIdMedicamento(receta.getMedicamento().getIdMedicamento());
        dto.setNombreMedicamento(receta.getMedicamento().getNombreMedicamento());
        dto.setDescripcionMedicamento(receta.getMedicamento().getDescripcion());
        dto.setDosisRecomendada(receta.getMedicamento().getDosisRecomendada());

        dto.setFechaEmision(receta.getFechaEmision());
        dto.setIndicaciones(receta.getIndicaciones());

        return dto;
    }
}