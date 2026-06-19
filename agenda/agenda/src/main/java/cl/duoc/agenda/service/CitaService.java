package cl.duoc.agenda.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.duoc.agenda.dto.CitaDTO;
import cl.duoc.agenda.dto.RegistroDTO;
import cl.duoc.agenda.dto.VeterinarioDTO;
import cl.duoc.agenda.model.Agenda;
import cl.duoc.agenda.model.Cita;
import cl.duoc.agenda.repository.AgendaRepository;
import cl.duoc.agenda.repository.CitaRepository;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository; // Inyección del repositorio de Cita para acceder a la base de datos

    @Autowired
    private AgendaRepository agendaRepository; // Inyección del repositorio de Agenda para validar que la cita calce con una agenda existente

    @Autowired
    private RestTemplate restTemplate;// RestTemplate para realizar llamadas HTTP a otros microservicios (Registro y Veterinarios)

    private final String URL_VETERINARIO = "http://localhost:8082/api/v1/veterinarios"; // URL base del microservicio de Veterinarios
    private final String URL_REGISTRO = "http://localhost:8081/api/v1/registros"; // URL base del microservicio de Registro


    // •   DTO agenda por ID
    public List<Cita> listar() {
        return citaRepository.findAll(); // Retorna todas las citas almacenadas en la base de datos utilizando el repositorio de Cita
    }

    // •   Buscar cita por ID
    public Cita buscarPorId(Integer id) {
        return citaRepository.findById(id) // Busca una cita por su ID utilizando el repositorio de Cita
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con id: " + id)); // Si no se encuentra la cita, lanza una excepción indicando que no se encontró la cita con el ID proporcionado
    }

    // •   Bucar cita por mascota
    public List<Cita> buscarPorMascota(Integer idMascota) {
        return citaRepository.findByIdMascota(idMascota); // Busca todas las citas asociadas a una mascota específica utilizando el repositorio de Cita
    }

    // •   Buscar cita por veterinario
    public List<Cita> buscarPorVeterinario(Integer idVeterinario) {
        return citaRepository.findByIdVeterinario(idVeterinario); // Busca todas las citas asociadas a un veterinario específico utilizando el repositorio de Cita
    }

    // •   Crear cita
    public Cita guardar(Cita cita) {

        // Validamos que la mascota exista en el microservicio Registro.
        validarMascota(cita.getIdMascota());

        // Validamos que el veterinario exista en el microservicio de Veterinarios.
        validarVeterinario(cita.getIdVeterinario());

        // Validamos que la cita calce con la agenda (mismo veterinario, misma fecha, hora dentro del rango de la agenda).
        Agenda agendaExistente = obtenerAgendaExistente(cita);

        // Validamos que la cita esté dentro del horario de la agenda y que tenga todos los campos obligatorios.
        validarCitaDentroDeAgenda(cita, agendaExistente);

        // Si todas las validaciones pasan, asignamos la agenda existente a la cita y guardamos la cita en la base de datos utilizando el repositorio de Cita.
        cita.setAgenda(agendaExistente);

        return citaRepository.save(cita); // Retorna la cita guardada con su ID generado por la base de datos y cualquier otra información adicional que se haya asignado durante el proceso de guardado
    }

    // •   Actualizar cita
    public Cita actualizar(Integer id, Cita cita) {

        Cita citaExistente = buscarPorId(id);

        validarMascota(cita.getIdMascota()); // Validamos que la mascota exista en el microservicio Registro.
        validarVeterinario(cita.getIdVeterinario()); // Validamos que el veterinario exista en el microservicio de Veterinarios.

        Agenda agendaExistente = obtenerAgendaExistente(cita); // Validamos que la cita calce con la agenda (mismo veterinario, misma fecha, hora dentro del rango de la agenda).

        validarCitaDentroDeAgenda(cita, agendaExistente); // Validamos que la cita esté dentro del horario de la agenda y que tenga todos los campos obligatorios.

        // Si todas las validaciones pasan, asignamos la agenda existente a la cita y actualizamos los campos de la cita existente con los valores de la cita proporcionada, luego guardamos la cita actualizada en la base de datos utilizando el repositorio de Cita.
        citaExistente.setIdMascota(cita.getIdMascota());
        citaExistente.setIdVeterinario(cita.getIdVeterinario());
        citaExistente.setAgenda(agendaExistente);
        citaExistente.setMotivo(cita.getMotivo());
        citaExistente.setFecha(cita.getFecha());
        citaExistente.setHora(cita.getHora());
        citaExistente.setEstado(cita.getEstado());

        return citaRepository.save(citaExistente); // Retorna la cita actualizada con su ID y cualquier otra información adicional que se haya modificado durante el proceso de actualización
    }

    // •   Eliminar cita
    public void eliminar(Integer id) {

        if (!citaRepository.existsById(id)) {
            throw new RuntimeException("Cita no encontrada con id: " + id); // Si no se encuentra la cita con el ID proporcionado, lanza una excepción indicando que no se encontró la cita con ese ID
        }

        citaRepository.deleteById(id); // Elimina la cita de la base de datos utilizando el repositorio de Cita
    }

    // OBTENER AGENDA EXISTENTE PARA LA CITA
    private Agenda obtenerAgendaExistente(Cita cita) {

        // Validamos que la cita tenga una agenda válida (con ID) para poder buscarla en la base de datos y obtener sus detalles, como el veterinario, fecha, hora de inicio y fin, etc. Si la cita no tiene una agenda válida, lanzamos una excepción indicando que se debe indicar una agenda válida.
        if (cita.getAgenda() == null || cita.getAgenda().getIdAgenda() == null) { // Validamos que la cita tenga una agenda válida (con ID)
            throw new RuntimeException("Debe indicar una agenda válida"); // Si la cita no tiene una agenda válida (null o sin ID), lanza una excepción indicando que se debe indicar una agenda válida
        }

        Integer idAgenda = cita.getAgenda().getIdAgenda(); // Obtenemos el ID de la agenda de la cita para buscarla en la base de datos utilizando el repositorio de Agenda. 

        return agendaRepository.findById(idAgenda)
                .orElseThrow(() -> new RuntimeException("Agenda no encontrada con id: " + idAgenda)); // Si no se encuentra la agenda con el ID proporcionado, lanza una excepción indicando que no se encontró la agenda con ese ID. Si se encuentra, retorna la agenda existente que calza con la cita para ser utilizada en las validaciones posteriores y asignada a la cita antes de guardarla o actualizarla en la base de datos.
    }

    // VALIDAR QUE LA CITA ESTÉ DENTRO DEL HORARIO DE LA AGENDA Y QUE TENGA TODOS LOS CAMPOS OBLIGATORIOS
    private void validarCitaDentroDeAgenda(Cita cita, Agenda agenda) {

        if (cita.getIdVeterinario() == null) {
            throw new RuntimeException("El id del veterinario es obligatorio");
        }

        if (cita.getIdMascota() == null) {
            throw new RuntimeException("El id de la mascota es obligatorio");
        }

        if (cita.getFecha() == null) {
            throw new RuntimeException("La fecha de la cita es obligatoria");
        }

        if (cita.getHora() == null) {
            throw new RuntimeException("La hora de la cita es obligatoria");
        }

        if (cita.getMotivo() == null || cita.getMotivo().isBlank()) {
            throw new RuntimeException("El motivo de la cita es obligatorio");
        }

        if (cita.getEstado() == null || cita.getEstado().isBlank()) {
            throw new RuntimeException("El estado de la cita es obligatorio");
        }

        // La cita debe tener el mismo veterinario que la agenda.
        if (!cita.getIdVeterinario().equals(agenda.getIdVeterinario())) {
            throw new RuntimeException("El veterinario de la cita no coincide con el veterinario de la agenda");
        }

        // La cita debe tener la misma fecha que la agenda.
        if (!cita.getFecha().equals(agenda.getFecha())) {
            throw new RuntimeException("La fecha de la cita no coincide con la fecha de la agenda");
        }

        // La hora de la cita debe estar dentro del horario de la agenda (hora inicio <= hora cita < hora fin).
        boolean horaAntesDelInicio = cita.getHora().isBefore(agenda.getHoraInicio()); // La hora de la cita es antes de la hora de inicio de la agenda
        boolean horaDespuesOIgualAlFin = !cita.getHora().isBefore(agenda.getHoraFin()); // La hora de la cita es después o igual a la hora de fin de la agenda

        if (horaAntesDelInicio || horaDespuesOIgualAlFin) {
            throw new RuntimeException("La hora de la cita debe estar dentro del horario de la agenda");
        }
    }

    // VALIDAR MASCOTA EXTERNA
    public RegistroDTO validarMascota(Integer idMascota) {

        try {
            String url = URL_REGISTRO + "/dto/mascota/" + idMascota;
            return restTemplate.getForObject(url, RegistroDTO.class); // restTemplate.getForObject realiza una llamada HTTP GET al microservicio de Registro para validar que la mascota exista y obtener su información utilizando el ID de la mascota.
        } catch (Exception e) {
            throw new RuntimeException("No se pudo validar la mascota con id: " + idMascota); // Si hay un error en la llamada al microservicio de Registro o la mascota no existe, lanza una excepción indicando que no se pudo validar la mascota con el ID proporcionado
        }
    }

    // VALIDAR VETERINARIO EXTERNO
    public VeterinarioDTO validarVeterinario(Integer idVeterinario) {

        try {
            String url = URL_VETERINARIO + "/dto/" + idVeterinario;
            return restTemplate.getForObject(url, VeterinarioDTO.class); // Realiza una llamada HTTP GET al microservicio de Veterinario para validar que el veterinario exista y obtener su información utilizando el RestTemplate. Si el veterinario no existe o hay un error en la llamada, lanza una excepción indicando que no se pudo validar el veterinario con el ID proporcionado.

        } catch (Exception e) {
            throw new RuntimeException("No se pudo validar el veterinario con id: " + idVeterinario); // Si hay un error en la llamada al microservicio de Veterinario o el veterinario no existe, lanza una excepción indicando que no se pudo validar el veterinario con el ID proporcionado
        }
    }

    // // •   Ver DTO 
    public CitaDTO obtenerCitaDTO(Integer idCita) {

        Cita cita = buscarPorId(idCita);

        RegistroDTO registro = validarMascota(cita.getIdMascota()); // Validamos que la mascota exista en el microservicio Registro y obtenemos su información para incluirla en el DTO de la cita.
        VeterinarioDTO veterinario = validarVeterinario(cita.getIdVeterinario()); // Validamos que el veterinario exista en el microservicio de Veterinarios y obtenemos su información para incluirla en el DTO de la cita.

        CitaDTO dto = new CitaDTO(); // Creamos un nuevo DTO de Cita para combinar la información de la cita, la mascota y el veterinario en un solo objeto que pueda ser retornado al cliente con toda la información relevante de la cita, incluyendo detalles de la mascota y el veterinario asociados a esa cita. Esto facilita la presentación de la información completa de la cita en una sola respuesta al cliente.

        dto.setIdCita(cita.getIdCita());
        dto.setIdMascota(cita.getIdMascota());
        dto.setIdVeterinario(cita.getIdVeterinario());
        dto.setIdAgenda(cita.getAgenda().getIdAgenda());

        dto.setMotivo(cita.getMotivo());
        dto.setFecha(cita.getFecha());
        dto.setHora(cita.getHora());
        dto.setEstado(cita.getEstado());

        dto.setNombreMascota(registro.getNombreMascota());
        dto.setEspecieMascota(registro.getEspecieMascota());
        dto.setRazaMascota(registro.getRazaMascota());

        dto.setNombreCliente(registro.getNombreCliente());
        dto.setCorreoCliente(registro.getCorreoCliente());

        dto.setNombreVeterinario(veterinario.getNombre());
        dto.setEspecialidadVeterinario(veterinario.getEspecialidad());

        return dto; // Retorna el DTO de Cita con toda la información combinada de la cita, la mascota y el veterinario para ser utilizado en la presentación de la información completa de la cita al cliente.
    }
}