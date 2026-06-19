package cl.duoc.pago.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.duoc.pago.dto.AtencionDTO;
import cl.duoc.pago.dto.PagoDTO;
import cl.duoc.pago.model.Pago;
import cl.duoc.pago.model.TipoPago;
import cl.duoc.pago.repository.PagoRepository;
import cl.duoc.pago.repository.TipoPagoRepository;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository; // Inyectamos el repositorio de Pago para acceder a la base de datos.

    @Autowired
    private TipoPagoRepository tipoPagoRepository; // Inyectamos el repositorio de TipoPago para validar que el tipo de pago exista en la base de datos.

    @Autowired
    private RestTemplate restTemplate; // Inyectamos RestTemplate para hacer llamadas HTTP a otros microservicios.

    
    // URL base del microservicio Atención Clínica.
    // Pago usa esta URL para validar la atención y obtener el precioBase.
    private final String URL_ATENCION = "http://localhost:8084/api/v1/atenciones";


    // •   Listar pagos
    public List<Pago> listar() {
        return pagoRepository.findAll();
    }

    
    // •   Buscar pago por ID
    public Pago buscarPorId(Integer id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con id: " + id));
    }

    
    // •   Busca pagos por ID atención clínica.
    public List<Pago> buscarPorAtencion(Integer idAtencion) {
        return pagoRepository.findByIdAtencion(idAtencion);
    }

    // •   Busca pagos por estado de pago.
    public List<Pago> buscarPorEstado(String estadoPago) {
        return pagoRepository.findByEstadoPago(estadoPago);
    }

    // •   Crear pago
    public Pago guardar(Pago pago) {

        
        // Validamos los datos básicos del pago.
        // Ojo: ya no validamos monto, porque ahora se calcula automáticamente.
        validarDatosBasicos(pago);

        // Validamos que la atención exista en Atención Clínica.
        // Además, desde ese DTO obtenemos el precioBase.
        AtencionDTO atencion = validarAtencion(pago.getIdAtencion());

        // Validamos que el tipo de pago exista dentro del microservicio Pago.
        TipoPago tipoPagoExistente = obtenerTipoPago(pago);


        //El monto ya no se digita manualmente desde Postman -> se toma desde el precioBase de la atención clínica.
        pago.setMonto(atencion.getPrecioBase());

        //Asignamos el tipo de pago real obtenido desde la base de datos.
        pago.setTipoPago(tipoPagoExistente);
        return pagoRepository.save(pago); 
    }

    // •   Actualizar pago
    public Pago actualizar(Integer id, Pago pago) {

        Pago pagoExistente = buscarPorId(id);

        //Validamos los datos básicos. 
        validarDatosBasicos(pago);

        //Validamos atención y obtenemos el precioBase actualizado.
        AtencionDTO atencion = validarAtencion(pago.getIdAtencion());

        // Validamos que el tipo de pago exista dentro del microservicio Pago.
        TipoPago tipoPagoExistente = obtenerTipoPago(pago);

        //Aunque alguien mande monto desde Postman, lo ignoramos -> El monto correcto siempre viene desde Atención Clínica.
        pago.setMonto(atencion.getPrecioBase());


        // Actualizamos los datos del pago existente.
        pagoExistente.setIdAtencion(pago.getIdAtencion());
        pagoExistente.setTipoPago(tipoPagoExistente);
        pagoExistente.setMonto(pago.getMonto());
        pagoExistente.setFechaPago(pago.getFechaPago());
        pagoExistente.setEstadoPago(pago.getEstadoPago());

        return pagoRepository.save(pagoExistente);
    }

    // •   Eliminar pago
    public void eliminar(Integer id) {

        if (!pagoRepository.existsById(id)) {
            throw new RuntimeException("Pago no encontrado con id: " + id);
        }

        pagoRepository.deleteById(id);
    }


    // Validar datos basicos del pago. Se usa tanto para crear como para actualizar.
    private void validarDatosBasicos(Pago pago) {

        if (pago.getIdAtencion() == null) {
            throw new RuntimeException("El id de atención es obligatorio");
        }

        if (pago.getTipoPago() == null || pago.getTipoPago().getIdTipoPago() == null) {
            throw new RuntimeException("Debe indicar un tipo de pago válido");
        }

        if (pago.getFechaPago() == null) {
            throw new RuntimeException("La fecha de pago es obligatoria");
        }

        if (pago.getEstadoPago() == null || pago.getEstadoPago().isBlank()) {
            throw new RuntimeException("El estado del pago es obligatorio");
        }

    }


    // Validamos que la atencion exista.
    private AtencionDTO validarAtencion(Integer idAtencion) {

        try {
            String url = URL_ATENCION + "/dto/" + idAtencion;
            return restTemplate.getForObject(url, AtencionDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo validar la atención con id: " + idAtencion);
        }
    }


    // Obstener tipo de pago.
    private TipoPago obtenerTipoPago(Pago pago) {

        Integer idTipoPago = pago.getTipoPago().getIdTipoPago();

        return tipoPagoRepository.findById(idTipoPago)
                .orElseThrow(() -> new RuntimeException("Tipo de pago no encontrado con id: " + idTipoPago));
    }


    // •   Ver DTO pago
    public PagoDTO obtenerPagoDTO(Integer idPago) {

        Pago pago = buscarPorId(idPago);
        

        // Consultamos Atención Clínica para traer datos enriquecidos: mascota, cliente, veterinario, tipo de atención y precioBase.

        AtencionDTO atencion = validarAtencion(pago.getIdAtencion());

        PagoDTO dto = new PagoDTO();

        dto.setIdPago(pago.getIdPago());

        dto.setIdAtencion(pago.getIdAtencion());
        dto.setIdMascota(atencion.getIdMascota());
        dto.setNombreMascota(atencion.getNombreMascota());
        dto.setNombreCliente(atencion.getNombreCliente());
        dto.setCorreoCliente(atencion.getCorreoCliente());

        dto.setIdVeterinario(atencion.getIdVeterinario());
        dto.setNombreVeterinario(atencion.getNombreVeterinario());

        dto.setNombreTipoAtencion(atencion.getNombreTipoAtencion());
        dto.setPrecioBase(atencion.getPrecioBase());

        dto.setIdTipoPago(pago.getTipoPago().getIdTipoPago());
        dto.setNombreTipoPago(pago.getTipoPago().getNombreTipoPago());

        dto.setMonto(pago.getMonto());
        dto.setFechaPago(pago.getFechaPago());
        dto.setEstadoPago(pago.getEstadoPago());

        return dto;
    }
}