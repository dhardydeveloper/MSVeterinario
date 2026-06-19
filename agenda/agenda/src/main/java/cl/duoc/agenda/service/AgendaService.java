package cl.duoc.agenda.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.duoc.agenda.dto.AgendaDTO;
import cl.duoc.agenda.dto.VeterinarioDTO;
import cl.duoc.agenda.model.Agenda;
import cl.duoc.agenda.repository.AgendaRepository;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository; // Inyección del repositorio de Agenda para acceder a la base de datos

    @Autowired
    private RestTemplate restTemplate; // Inyección de RestTemplate para realizar llamadas HTTP a otros microservicios

    private final String URL_VETERINARIO = "http://localhost:8082/api/v1/veterinarios"; // URL base del microservicio de Veterinarios

  
    // •   Listar agendas
    public List<Agenda> listar() {
        return agendaRepository.findAll();
    }

    // •   Buscar agenda por ID
    public Agenda buscarPorId(Integer id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agenda no encontrada con id: " + id));
    }

    // •   Buscar agenda por ID veterinario
    public List<Agenda> buscarPorVeterinario(Integer idVeterinario) { // Busca todas las agendas asociadas a un veterinario específico utilizando su ID. Esto permite obtener todas las agendas programadas para ese veterinario en particular.
        return agendaRepository.findByIdVeterinario(idVeterinario); // Llama al método findByIdVeterinario del repositorio de Agenda para obtener la lista de agendas asociadas al ID del veterinario proporcionado.
    }

    // •   Crear agenda
    public Agenda guardar(Agenda agenda) {

        // Validamos que el veterinario exista en el microservicio de Veterinarios.
        validarVeterinario(agenda.getIdVeterinario());

        // Validamos que el horario sea correcto (hora inicio antes de hora fin, campos obligatorios, etc).
        validarHorario(agenda);

        // Validamos que no se cruce con otra agenda del mismo veterinario.
        validarAgendaCruzada(agenda, null);

        return agendaRepository.save(agenda);
    }

    // •   Actualizar agenda
    public Agenda actualizar(Integer id, Agenda agenda) {

        Agenda agendaExistente = buscarPorId(id);

        // Validamos que el veterinario exista en el microservicio de Veterinarios.
        validarVeterinario(agenda.getIdVeterinario());

        // Validamos que el horario sea correcto (hora inicio antes de hora fin, campos obligatorios, etc).
        validarHorario(agenda);

        // Validamos que no se cruce con otra agenda del mismo veterinario, excluyendo la agenda actual.
        validarAgendaCruzada(agenda, id);

        agendaExistente.setIdVeterinario(agenda.getIdVeterinario());
        agendaExistente.setFecha(agenda.getFecha());
        agendaExistente.setHoraInicio(agenda.getHoraInicio());
        agendaExistente.setHoraFin(agenda.getHoraFin());
        agendaExistente.setEstado(agenda.getEstado());

        return agendaRepository.save(agendaExistente); // Guarda la agenda actualizada en la base de datos utilizando el repositorio de Agenda y retorna la agenda actualizada.
    }

    // •   Eliminar agenda
    public void eliminar(Integer id) {

        if (!agendaRepository.existsById(id)) {
            throw new RuntimeException("Agenda no encontrada con id: " + id); // Verifica si la agenda con el ID proporcionado existe en la base de datos utilizando el método existsById del repositorio de Agenda. Si no existe, lanza una excepción indicando que la agenda no fue encontrada con ese ID.
        }

        agendaRepository.deleteById(id); // Elimina la agenda con el ID proporcionado de la base de datos utilizando el método deleteById del repositorio de Agenda.
    }

    // VALIDAR QUE EL VETERINARIO EXISTA EN EL MICRO SERVICIO DE VETERINARIOS
    public VeterinarioDTO validarVeterinario(Integer idVeterinario) {

        try {
            String url = URL_VETERINARIO + "/dto/" + idVeterinario; // Construye la URL completa para realizar la llamada al microservicio de Veterinarios utilizando el ID del veterinario proporcionado. La URL se forma concatenando la URL base del microservicio de Veterinarios con el endpoint específico para obtener el DTO del veterinario por su ID.
            return restTemplate.getForObject(url, VeterinarioDTO.class); // Realiza una llamada HTTP GET al microservicio de Veterinarios para validar que el veterinario exista y obtener su información utilizando el RestTemplate. Si el veterinario no existe o hay un error en la llamada, lanza una excepción indicando que no se pudo validar el veterinario con el ID proporcionado.

        } catch (Exception e) {
            throw new RuntimeException("No se pudo validar el veterinario con id: " + idVeterinario);
        }
    }

    // VALIDAR QUE LA CITA CALCE CON LA AGENDA
    private void validarHorario(Agenda agenda) {

        // Validamos que los campos obligatorios estén presentes
        if (agenda.getIdVeterinario() == null) {
            throw new RuntimeException("El id del veterinario es obligatorio");
        }

        // Validamos que la fecha y horas no sean nulas
        if (agenda.getFecha() == null) {
            throw new RuntimeException("La fecha es obligatoria");
        }

        // Validamos que la hora de inicio y fin no sean nulas
        if (agenda.getHoraInicio() == null) {
            throw new RuntimeException("La hora de inicio es obligatoria");
        }

        // Validamos que la hora de fin no sea nula
        if (agenda.getHoraFin() == null) {
            throw new RuntimeException("La hora de fin es obligatoria");
        }

        // Validamos que la hora de inicio sea antes que la hora de fin
        if (!agenda.getHoraInicio().isBefore(agenda.getHoraFin())) {
            throw new RuntimeException("La hora de inicio debe ser menor que la hora de fin");
        }

        // Validamos que el estado no sea nulo o vacío
        if (agenda.getEstado() == null || agenda.getEstado().isBlank()) {
            throw new RuntimeException("El estado es obligatorio");
        }
    }

    // VALIDAR HORARIO DE LA CITA CON LA AGENDA
    private void validarAgendaCruzada(Agenda agenda, Integer idExcluir) {

        // Busca en la base de datos si existe alguna agenda que se cruce con el horario de la agenda que se está intentando guardar o actualizar para el mismo veterinario. Esto se hace para evitar que haya dos agendas programadas para el mismo veterinario en horarios que se solapen, lo cual no sería posible de cumplir en la realidad. 
        List<Agenda> agendasCruzadas = agendaRepository.buscarAgendasCruzadas(
                agenda.getIdVeterinario(),
                agenda.getFecha(),
                agenda.getHoraInicio(),
                agenda.getHoraFin(),
                idExcluir
        );

        if (!agendasCruzadas.isEmpty()) {
            throw new RuntimeException("Ya existe una agenda para ese veterinario en ese horario"); // Si se encuentra alguna agenda que se cruce con el horario de la agenda que se está intentando guardar o actualizar, lanza una excepción indicando que ya existe una agenda para ese veterinario en ese horario. Esto garantiza que no se puedan programar dos agendas para el mismo veterinario en horarios, manteniendo la integridad de las agendas programadas.
        }
    }

    // •   DTO agenda 
    public AgendaDTO obtenerAgendaDTO(Integer idAgenda) {

        Agenda agenda = buscarPorId(idAgenda);
        VeterinarioDTO veterinario = validarVeterinario(agenda.getIdVeterinario());

        AgendaDTO dto = new AgendaDTO();

        dto.setIdAgenda(agenda.getIdAgenda()); 
        dto.setIdVeterinario(agenda.getIdVeterinario());
        dto.setFecha(agenda.getFecha());
        dto.setHoraInicio(agenda.getHoraInicio());
        dto.setHoraFin(agenda.getHoraFin());
        dto.setEstado(agenda.getEstado());

        dto.setNombreVeterinario(veterinario.getNombre());
        dto.setEspecialidadVeterinario(veterinario.getEspecialidad());

        return dto; // Retorna el DTO de Agenda con toda la información combinada de la agenda y el veterinario para ser utilizado en la presentación de la información completa de la agenda al cliente.
    }
}