package cl.duoc.examenes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.duoc.examenes.dto.AtencionDTO;
import cl.duoc.examenes.dto.OrdenExamenDTO;
import cl.duoc.examenes.model.OrdenExamen;
import cl.duoc.examenes.model.TipoExamen;
import cl.duoc.examenes.repository.OrdenExamenRepository;
import cl.duoc.examenes.repository.TipoExamenRepository;

@Service
public class OrdenExamenService {

    @Autowired
    private OrdenExamenRepository ordenExamenRepository; // Inyección del repositorio de ordenes de examen

    @Autowired
    private TipoExamenRepository tipoExamenRepository; // Inyección del repositorio de tipos de examen, para validar que el tipo de examen exista

    @Autowired
    private RestTemplate restTemplate; // Inyección de RestTemplate para realizar llamadas HTTP a otros microservicios (en este caso, al microservicio de atenciones)

    private final String URL_ATENCION = "http://localhost:8084/api/v1/atenciones"; // URL base del microservicio de atenciones 


    // •   Listar órdenes de examenes
    public List<OrdenExamen> listar() {
        return ordenExamenRepository.findAll(); // findAll() para obtener todas las ordenes de examen de la base de datos
    }

    // •   Buscar orden de examen por ID
    public OrdenExamen buscarPorId(Integer id) {
        return ordenExamenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Orden de examen no encontrada con id: " + id));
    }

    // •   Buscar ordenes de examen por ID atención 
    public List<OrdenExamen> buscarPorAtencion(Integer idAtencion) {
        return ordenExamenRepository.findByIdAtencion(idAtencion);
    }
    
    // •   Buscar ordenes de examen por ID mascota 
    public List<OrdenExamen> buscarPorMascota(Integer idMascota) {
        return ordenExamenRepository.findByIdMascota(idMascota);
    }

    // •   Buscar ordenes de examen por ID veterinario
    public List<OrdenExamen> buscarPorVeterinario(Integer idVeterinario) {
        return ordenExamenRepository.findByIdVeterinario(idVeterinario);
    }

    // •   Crear orden de examen
    public OrdenExamen guardar(OrdenExamen ordenExamen) {

        // Validaciones previas a guardar la nueva orden de examen
        validarDatosBasicos(ordenExamen); // Validación de datos básicos (idAtencion, idMascota, idVeterinario, tipoExamen, fechaSolicitud, estado)

        AtencionDTO atencion = validarAtencion(ordenExamen.getIdAtencion()); // Validación de que la atención exista en el microservicio de atenciones

        validarCoincidenciaConAtencion(ordenExamen, atencion); // Validación de que la mascota y el veterinario de la orden coincidan con los datos de la atención obtenida del microservicio de atenciones

        TipoExamen tipoExistente = obtenerTipoExamen(ordenExamen); // Validación de que el tipo de examen exista en la base de datos

        ordenExamen.setTipoExamen(tipoExistente); // Asignación del tipo de examen existente a la orden de examen antes de guardarla

        return ordenExamenRepository.save(ordenExamen); // Guardar la orden de examen en la base de datos y retornar la orden guardada
    }

    // •   Actualizar orden de examen 
    public OrdenExamen actualizar(Integer id, OrdenExamen ordenExamen) { // Recibe el ID de la orden de examen a actualizar y el objeto OrdenExamen con los nuevos datos
        OrdenExamen ordenExistente = buscarPorId(id); // Buscar la orden de examen existente por su ID, si no se encuentra se lanza una excepción

        // Validaciones previas a actualizar la orden de examen
        validarDatosBasicos(ordenExamen); // Validación de datos básicos (idAtencion, idMascota, idVeterinario, tipoExamen, fechaSolicitud, estado)

        AtencionDTO atencion = validarAtencion(ordenExamen.getIdAtencion()); // Validación de que la atención exista en el microservicio de atenciones

        validarCoincidenciaConAtencion(ordenExamen, atencion); // Validación de que la mascota y el veterinario de la orden coincidan con los datos de la atención obtenida del microservicio de atenciones

        TipoExamen tipoExistente = obtenerTipoExamen(ordenExamen); // Validación de que el tipo de examen exista en la base de datos

        // Actualización de los campos de la orden de examen existente con los nuevos datos
        ordenExistente.setIdAtencion(ordenExamen.getIdAtencion());
        ordenExistente.setIdMascota(ordenExamen.getIdMascota());
        ordenExistente.setIdVeterinario(ordenExamen.getIdVeterinario());
        ordenExistente.setTipoExamen(tipoExistente);
        ordenExistente.setFechaSolicitud(ordenExamen.getFechaSolicitud());
        ordenExistente.setEstado(ordenExamen.getEstado());

        return ordenExamenRepository.save(ordenExistente); // Guardar la orden de examen actualizada en la base de datos y retornar la orden actualizada
    }

    // •   Eliminar orden de examen 
    public void eliminar(Integer id) {

        if (!ordenExamenRepository.existsById(id)) {
            throw new RuntimeException("Orden de examen no encontrada con id: " + id); // Validación de que la orden de examen exista antes de intentar eliminarla, si no se encuentra se lanza una excepción
        }

        ordenExamenRepository.deleteById(id); // Eliminar la orden de examen de la base de datos por su ID
    }

    // Validaciones y obtención de datos relacionados
    private void validarDatosBasicos(OrdenExamen ordenExamen) { // Validación de datos básicos de la orden de examen

        // Validación de que los campos básicos no sean nulos o vacíos, si alguna validación falla se lanza una excepción con un mensaje descriptivo del error
        if (ordenExamen.getIdAtencion() == null) {
            throw new RuntimeException("El id de atención es obligatorio"); 
        }

        if (ordenExamen.getIdMascota() == null) {
            throw new RuntimeException("El id de mascota es obligatorio");
        }

        if (ordenExamen.getIdVeterinario() == null) {
            throw new RuntimeException("El id de veterinario es obligatorio");
        }

        if (ordenExamen.getTipoExamen() == null || ordenExamen.getTipoExamen().getIdTipoExamen() == null) {
            throw new RuntimeException("Debe indicar un tipo de examen válido");
        }

        if (ordenExamen.getFechaSolicitud() == null) {
            throw new RuntimeException("La fecha de solicitud es obligatoria");
        }

        if (ordenExamen.getEstado() == null || ordenExamen.getEstado().isBlank()) {
            throw new RuntimeException("El estado de la orden es obligatorio");
        }
    }

    // Validar que la atención exista en el microservicio de atenciones y obtener los datos de la atención
    private AtencionDTO validarAtencion(Integer idAtencion) { 

        try {
            String url = URL_ATENCION + "/dto/" + idAtencion;
            return restTemplate.getForObject(url, AtencionDTO.class);  // Realizar una llamada HTTP GET al microservicio de atenciones para obtener los datos de la atención por su ID

        } catch (Exception e) {
            throw new RuntimeException("No se pudo validar la atención con id: " + idAtencion); // Si ocurre cualquier error al intentar validar la atención (por ejemplo, si el microservicio de atenciones no está disponible o si la atención no se encuentra), se lanza una excepción con un mensaje descriptivo del error
        }
    }

    // Validar que la mascota y el veterinario de la orden coincidan con los datos de la atención obtenida del microservicio de atenciones
    private void validarCoincidenciaConAtencion(OrdenExamen ordenExamen, AtencionDTO atencion) { 

        if (!ordenExamen.getIdMascota().equals(atencion.getIdMascota())) {
            throw new RuntimeException("La mascota de la orden no coincide con la mascota de la atención");
        }

        if (!ordenExamen.getIdVeterinario().equals(atencion.getIdVeterinario())) {
            throw new RuntimeException("El veterinario de la orden no coincide con el veterinario de la atención");
        }
    }

    // Validar que el tipo de examen exista en la base de datos y obtener el tipo de examen existente
    private TipoExamen obtenerTipoExamen(OrdenExamen ordenExamen) {

        Integer idTipoExamen = ordenExamen.getTipoExamen().getIdTipoExamen(); 

        return tipoExamenRepository.findById(idTipoExamen)
                .orElseThrow(() -> new RuntimeException("Tipo de examen no encontrado con id: " + idTipoExamen)); 
    }

    // •   Ver DTO orden
    public OrdenExamenDTO obtenerOrdenExamenDTO(Integer idOrdenExamen) {

        OrdenExamen orden = buscarPorId(idOrdenExamen); // Buscar la orden de examen por su ID, si no se encuentra se lanza una excepción

        AtencionDTO atencion = validarAtencion(orden.getIdAtencion()); // Validar que la atención exista en el microservicio de atenciones y obtener los datos de la atención, si no se encuentra se lanza una excepción

        OrdenExamenDTO dto = new OrdenExamenDTO(); // Crear un nuevo DTO para almacenar los datos completos de la orden de examen

        dto.setIdOrdenExamen(orden.getIdOrdenExamen()); // Asignar el ID de la orden de examen al DTO

        // Asignar los datos relacionados de la atención, mascota, veterinario y tipo de examen al DTO
        dto.setIdAtencion(orden.getIdAtencion());
        dto.setIdMascota(orden.getIdMascota());
        dto.setNombreMascota(atencion.getNombreMascota());
        dto.setEspecieMascota(atencion.getEspecieMascota());
        dto.setRazaMascota(atencion.getRazaMascota());

        // Asignar los datos del cliente relacionados con la atención al DTO
        dto.setNombreCliente(atencion.getNombreCliente());
        dto.setCorreoCliente(atencion.getCorreoCliente());

        // Asignar los datos del veterinario relacionados con la atención al DTO
        dto.setIdVeterinario(orden.getIdVeterinario());
        dto.setNombreVeterinario(atencion.getNombreVeterinario());
        dto.setEspecialidadVeterinario(atencion.getEspecialidadVeterinario());

        // Asignar los datos del tipo de examen relacionados con la orden de examen al DTO
        dto.setIdTipoExamen(orden.getTipoExamen().getIdTipoExamen());
        dto.setNombreExamen(orden.getTipoExamen().getNombreExamen());
        dto.setDescripcionExamen(orden.getTipoExamen().getDescripcion());
        dto.setPrecioExamen(orden.getTipoExamen().getPrecio());

        // Asignar los datos básicos de la orden de examen al DTO
        dto.setFechaSolicitud(orden.getFechaSolicitud());
        dto.setEstado(orden.getEstado());

        return dto; // Retornar el DTO con los datos completos de la orden de examen
    }
}