package cl.duoc.atencionClinica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.duoc.atencionClinica.dto.AtencionDTO;
import cl.duoc.atencionClinica.dto.CitaDTO;
import cl.duoc.atencionClinica.model.Atencion;
import cl.duoc.atencionClinica.model.Box;
import cl.duoc.atencionClinica.model.TipoAtencion;
import cl.duoc.atencionClinica.repository.AtencionRepository;
import cl.duoc.atencionClinica.repository.BoxRepository;
import cl.duoc.atencionClinica.repository.TipoAtencionRepository;

@Service
public class AtencionService {

    @Autowired
    private AtencionRepository atencionRepository; // Inyección del repositorio de Atencion

    @Autowired
    private TipoAtencionRepository tipoAtencionRepository; // Inyección del repositorio de TipoAtencion

    @Autowired
    private BoxRepository boxRepository; // Inyección del repositorio de Box

    @Autowired
    private RestTemplate restTemplate; // Inyección del RestTemplate para consumir servicios REST externos, como el servicio de Agenda para validar citas. 

    private final String URL_AGENDA = "http://localhost:8083/api/v1/citas"; // URL base del servicio de Agenda para validar citas


  

    // •   Listar atenciones
    public List<Atencion> listar() {
        return atencionRepository.findAll(); 
    }

    // •   Buscar atención por ID
    public Atencion buscarPorId(Integer id) {
        return atencionRepository.findById(id) 
                .orElseThrow(() -> new RuntimeException("Atención no encontrada con id: " + id)); 
    }

    // •   Buscar atencion por ID de cita
    public List<Atencion> buscarPorCita(Integer idCita) {
        return atencionRepository.findByIdCita(idCita); 
    }

    // •   Buscar atencione por ID de mascota
    public List<Atencion> buscarPorMascota(Integer idMascota) {
        return atencionRepository.findByIdMascota(idMascota);
    }

    // •   Buscar atencione por ID de veterinario
    public List<Atencion> buscarPorVeterinario(Integer idVeterinario) {
        return atencionRepository.findByIdVeterinario(idVeterinario);
    }

    // •   Crear atención
    public Atencion guardar(Atencion atencion) {

        // Validar los datos básicos de la atención clínica
        validarDatosBasicos(atencion);

        // Validar que la cita asociada a la atención clínica exista y sea válida
        CitaDTO cita = validarCita(atencion.getIdCita());

        // Validar que la información de la atención clínica coincida con la información de la cita (mascota, veterinario, estado de la cita)
        validarCoincidenciaConCita(atencion, cita);

        TipoAtencion tipoAtencion = obtenerTipoAtencion(atencion); // Validar que el tipo de atención exista en la base de datos y obtener su información completa
        Box box = obtenerBox(atencion); // Validar que el box exista en la base de datos y obtener su información completa

        atencion.setTipoAtencion(tipoAtencion); // Asignar el tipo de atención completo a la atención clínica antes de guardarla
        atencion.setBox(box); // Asignar el box completo a la atención clínica antes de guardarla

        return atencionRepository.save(atencion); // Guardar la atención clínica en la base de datos y retornar la entidad guardada
    }

    // •   Actualizar atención
    public Atencion actualizar(Integer id, Atencion atencion) {

        // Buscar la atención clínica existente por su ID para asegurarse de que exista antes de intentar actualizarla
        Atencion atencionExistente = buscarPorId(id);

        // Validar los datos básicos de la atención clínica que se desea actualizar
        validarDatosBasicos(atencion);

        // Validar que la cita asociada a la atención clínica exista y sea válida
        CitaDTO cita = validarCita(atencion.getIdCita());

        // Validar que la información de la atención clínica coincida con la información de la cita (mascota, veterinario, estado de la cita)
        validarCoincidenciaConCita(atencion, cita);

        TipoAtencion tipoAtencion = obtenerTipoAtencion(atencion); // Validar que el tipo de atención exista en la base de datos y obtener su información completa
        Box box = obtenerBox(atencion); // Validar que el box exista en la base de datos y obtener su información completa

        // Actualizar los campos de la atención clínica existente con los nuevos valores proporcionados en el objeto "atencion" que se desea actualizar
        atencionExistente.setIdCita(atencion.getIdCita());
        atencionExistente.setIdMascota(atencion.getIdMascota());
        atencionExistente.setIdVeterinario(atencion.getIdVeterinario());
        atencionExistente.setTipoAtencion(tipoAtencion);
        atencionExistente.setBox(box);
        atencionExistente.setFechaAtencion(atencion.getFechaAtencion());
        atencionExistente.setDiagnostico(atencion.getDiagnostico());
        atencionExistente.setTratamiento(atencion.getTratamiento());
        atencionExistente.setObservaciones(atencion.getObservaciones());
        atencionExistente.setPesoActual(atencion.getPesoActual());

        return atencionRepository.save(atencionExistente); // Guardar la atención clínica actualizada en la base de datos y retornar la entidad actualizada
    }

    // •   Eliminar atención 
    public void eliminar(Integer id) {

        if (!atencionRepository.existsById(id)) {
            throw new RuntimeException("Atención no encontrada con id: " + id);
        }

        atencionRepository.deleteById(id); 
    }

    // Métodos de validaciones y obtención de datos relacionados
    private void validarDatosBasicos(Atencion atencion) {

        // Validar que los campos básicos de la atención clínica no sean nulos o vacíos, y que tengan valores válidos según las reglas de negocio definidas para cada campo
        if (atencion.getIdCita() == null) {
            throw new RuntimeException("El id de la cita es obligatorio");
        }

        if (atencion.getIdMascota() == null) { 
            throw new RuntimeException("El id de la mascota es obligatorio");
        }

        if (atencion.getIdVeterinario() == null) {
            throw new RuntimeException("El id del veterinario es obligatorio");
        }

        if (atencion.getTipoAtencion() == null || atencion.getTipoAtencion().getIdTipoAtencion() == null) {
            throw new RuntimeException("Debe indicar un tipo de atención válido");
        }

        if (atencion.getBox() == null || atencion.getBox().getIdBox() == null) {
            throw new RuntimeException("Debe indicar un box válido");
        }

        if (atencion.getFechaAtencion() == null) {
            throw new RuntimeException("La fecha de atención es obligatoria");
        }

        if (atencion.getDiagnostico() == null || atencion.getDiagnostico().isBlank()) {
            throw new RuntimeException("El diagnóstico es obligatorio");
        }

        if (atencion.getTratamiento() == null || atencion.getTratamiento().isBlank()) {
            throw new RuntimeException("El tratamiento es obligatorio");
        }

        if (atencion.getPesoActual() != null && atencion.getPesoActual() < 0) {
            throw new RuntimeException("El peso actual no puede ser negativo");
        }
    }

    // Método para validar que la cita asociada a la atención clínica exista y sea válida, utilizando el RestTemplate para consumir el servicio de Agenda y obtener la información de la cita por su ID. Si la cita no existe o no se puede validar, se lanza una excepción con un mensaje de error adecuado.
    private CitaDTO validarCita(Integer idCita) {

        try {
            String url = URL_AGENDA + "/dto/" + idCita;
            return restTemplate.getForObject(url, CitaDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo validar la cita con id: " + idCita);
        }
    }

    // Método para validar que la información de la atención clínica coincida con la información de la cita (mascota, veterinario, estado de la cita) para asegurar la coherencia de los datos entre ambos sistemas. Si hay alguna discrepancia, se lanza una excepción con un mensaje de error adecuado.
    private void validarCoincidenciaConCita(Atencion atencion, CitaDTO cita) {

        if (!atencion.getIdMascota().equals(cita.getIdMascota())) {
            throw new RuntimeException("La mascota de la atención no coincide con la mascota de la cita");
        }

        if (!atencion.getIdVeterinario().equals(cita.getIdVeterinario())) {
            throw new RuntimeException("El veterinario de la atención no coincide con el veterinario de la cita");
        }

        if (!"AGENDADA".equalsIgnoreCase(cita.getEstado())) {
            throw new RuntimeException("La cita debe estar en estado AGENDADA para crear una atención clínica");
        }
    }

    // Método para validar que el tipo de atención exista en la base de datos y obtener su información completa. Si el tipo de atención no existe, se lanza una excepción con un mensaje de error adecuado.
    private TipoAtencion obtenerTipoAtencion(Atencion atencion) {

        Integer idTipoAtencion = atencion.getTipoAtencion().getIdTipoAtencion();

        return tipoAtencionRepository.findById(idTipoAtencion)
                .orElseThrow(() -> new RuntimeException("Tipo de atención no encontrado con id: " + idTipoAtencion));
    }

    // Método para validar que el box exista en la base de datos y obtener su información completa. Si el box no existe, se lanza una excepción con un mensaje de error adecuado.
    private Box obtenerBox(Atencion atencion) {

        Integer idBox = atencion.getBox().getIdBox();

        return boxRepository.findById(idBox)
                .orElseThrow(() -> new RuntimeException("Box no encontrado con id: " + idBox));
    }

    // •   Ver DTO atención
    public AtencionDTO obtenerAtencionDTO(Integer idAtencion) {

        Atencion atencion = buscarPorId(idAtencion); // Buscar la atención clínica por su ID para obtener la entidad completa de la atención, incluyendo sus relaciones con el tipo de atención y el box.

        CitaDTO cita = validarCita(atencion.getIdCita()); // Validar la cita asociada a la atención clínica utilizando el método privado validarCita, que consume el servicio de Agenda para obtener la información de la cita por su ID. Si la cita no es válida, se lanzará una excepción y no se podrá construir el DTO.

        AtencionDTO dto = new AtencionDTO(); // Crear una nueva instancia de AtencionDTO para construir el objeto DTO que se retornará con toda la información relevante de la atención clínica y su cita asociada.

        dto.setIdAtencion(atencion.getIdAtencion());

        dto.setIdCita(atencion.getIdCita());
        dto.setIdMascota(atencion.getIdMascota());
        dto.setIdVeterinario(atencion.getIdVeterinario());

        dto.setNombreMascota(cita.getNombreMascota());
        dto.setEspecieMascota(cita.getEspecieMascota());
        dto.setRazaMascota(cita.getRazaMascota());

        dto.setNombreCliente(cita.getNombreCliente());
        dto.setCorreoCliente(cita.getCorreoCliente());

        dto.setNombreVeterinario(cita.getNombreVeterinario());
        dto.setEspecialidadVeterinario(cita.getEspecialidadVeterinario());

        dto.setIdTipoAtencion(atencion.getTipoAtencion().getIdTipoAtencion());
        dto.setNombreTipoAtencion(atencion.getTipoAtencion().getNombreTipo());
        dto.setPrecioBase(atencion.getTipoAtencion().getPrecioBase());

        dto.setIdBox(atencion.getBox().getIdBox());
        dto.setNombreBox(atencion.getBox().getNombreBox());

        dto.setFechaCita(cita.getFecha());
        dto.setHoraCita(cita.getHora());
        dto.setEstadoCita(cita.getEstado());

        dto.setFechaAtencion(atencion.getFechaAtencion());
        dto.setDiagnostico(atencion.getDiagnostico());
        dto.setTratamiento(atencion.getTratamiento());
        dto.setObservaciones(atencion.getObservaciones());
        dto.setPesoActual(atencion.getPesoActual());

        return dto; // Retornar el objeto AtencionDTO construido con toda la información relevante de la atención clínica y su cita asociada para ser utilizado en las capas superiores de la aplicación.
    }
}