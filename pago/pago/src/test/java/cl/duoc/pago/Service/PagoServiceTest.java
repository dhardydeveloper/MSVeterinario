package cl.duoc.pago.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import cl.duoc.pago.dto.AtencionDTO;
import cl.duoc.pago.model.Pago;
import cl.duoc.pago.model.TipoPago;
import cl.duoc.pago.repository.PagoRepository;
import cl.duoc.pago.repository.TipoPagoRepository;
import cl.duoc.pago.service.PagoService;

// Activa la integración de Mockito con JUnit 5 para crear mocks automáticamente
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private TipoPagoRepository tipoPagoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PagoService pagoService;

    private Pago pago;
    private TipoPago tipoPago;
    private AtencionDTO atencionDTO;

    @BeforeEach
    void setUp() {
        tipoPago = new TipoPago();
        tipoPago.setIdTipoPago(1);
        tipoPago.setNombreTipoPago("EFECTIVO");
        tipoPago.setDescripcion("Pago realizado en efectivo");

        // Pago de ejemplo con todos sus datos, reutilizable en los tests
        pago = new Pago();
        pago.setIdPago(1);
        pago.setIdAtencion(1);
        pago.setTipoPago(tipoPago);
        pago.setMonto(15000.0);
        pago.setFechaPago(LocalDate.of(2026, 5, 9));
        pago.setEstadoPago("PAGADO");

        // DTO de atención que simula la respuesta del microservicio externo
        atencionDTO = new AtencionDTO();
        atencionDTO.setIdAtencion(1);
        atencionDTO.setPrecioBase(15000.0);
    }


    // Listar pagos
    // El repositorio retorna una lista con pagos
    @Test
    void listar_deberiaRetornarListaDePagos() {
        when(pagoRepository.findAll()).thenReturn(Arrays.asList(pago));

        List<Pago> resultado = pagoService.listar();

        assertEquals(1, resultado.size());
        assertEquals("PAGADO", resultado.get(0).getEstadoPago());
        // Se verifica que findAll() fue invocado exactamente una vez
        verify(pagoRepository, times(1)).findAll();
    }

    // Buscar por Id (pago encontrado)
    // El repositorio retorna el pago con el id buscado, se verifica que los datos sean correctos
    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarPago() {
        when(pagoRepository.findById(1)).thenReturn(Optional.of(pago));

        Pago resultado = pagoService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals("PAGADO", resultado.getEstadoPago());
        verify(pagoRepository, times(1)).findById(1);
    }

    // Buscar por Id (pago no encontrado)
    // El repositorio no encuentra el pago, lanza una excepción con el mensaje correcto
    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(pagoRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.buscarPorId(99));

        assertEquals("Pago no encontrado con id: 99", ex.getMessage());
    }

    // Buscar por atención
    // El repositorio retorna los pagos asociados a una atención
    @Test
    void buscarPorAtencion_deberiaRetornarListaDePagos() {
        when(pagoRepository.findByIdAtencion(1)).thenReturn(Arrays.asList(pago));

        List<Pago> resultado = pagoService.buscarPorAtencion(1);

        assertEquals(1, resultado.size());
        verify(pagoRepository, times(1)).findByIdAtencion(1);
    }

    // Buscar por estado
    // El repositorio retorna los pagos con el estado indicado
    @Test
    void buscarPorEstado_deberiaRetornarListaDePagos() {
        when(pagoRepository.findByEstadoPago("PAGADO")).thenReturn(Arrays.asList(pago));

        List<Pago> resultado = pagoService.buscarPorEstado("PAGADO");

        assertEquals(1, resultado.size());
        verify(pagoRepository, times(1)).findByEstadoPago("PAGADO");
    }

    // Guardar pago con datos válidos
    // Se simula la consulta al microservicio de atenciones, la validación del tipo de pago y el guardado
    @Test
    void guardar_cuandoDatosValidos_deberiaGuardarYRetornarPago() {
        // ARRANGE: El microservicio externo retorna un AtencionDTO válido
        // anyString() acepta cualquier URL para no depender de la URL exacta configurada
        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);
        // ARRANGE: El tipo de pago con id=1 existe en el repositorio
        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        // ARRANGE: El repositorio confirma que el pago fue guardado correctamente
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        // ACT: Guardado del pago
        Pago resultado = pagoService.guardar(pago);

        // ASSERT: El resultado no es nulo, el estado y monto son correctos, y save() fue llamado una vez
        assertNotNull(resultado);
        assertEquals("PAGADO", resultado.getEstadoPago());
        assertEquals(15000.0, resultado.getMonto());
        verify(pagoRepository, times(1)).save(pago);
    }


    // Guardar pago sin id de atención
    // El servicio debe lanzar una excepción y nunca intentar guardar
    @Test
    void guardar_cuandoIdAtencionEsNulo_deberiaLanzarExcepcion() {
        pago.setIdAtencion(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.guardar(pago));

        assertEquals("El id de atención es obligatorio", ex.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    // Guardar pago con tipo de pago inexistente
    // El repositorio no encuentra el tipo de pago, lanza una excepción y nunca se guarda
    @Test
    void guardar_cuandoTipoPagoNoExiste_deberiaLanzarExcepcion() {
        // ARRANGE: Se crea un pago con un tipo de pago con id=99 que no existe
        Pago nuevo = new Pago();
        nuevo.setIdAtencion(1);
        TipoPago tipoRef = new TipoPago();
        tipoRef.setIdTipoPago(99);
        nuevo.setTipoPago(tipoRef);
        nuevo.setFechaPago(LocalDate.now());
        nuevo.setEstadoPago("PENDIENTE");

        // ARRANGE: Microservicio externo retorna un AtencionDTO válido
        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);
        // ARRANGE: Optional.empty() simula que el tipo de pago con id=99 NO existe
        when(tipoPagoRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.guardar(nuevo));

        assertEquals("Tipo de pago no encontrado con id: 99", ex.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }


    // Actualizar pago existente con datos válidos
    // Se simula la consulta al microservicio, la validación del tipo de pago y el guardado de los cambios
    @Test
    void actualizar_cuandoExiste_deberiaActualizarYRetornarPago() {
        Pago datosActualizados = new Pago();
        datosActualizados.setIdAtencion(1);
        datosActualizados.setTipoPago(tipoPago);
        datosActualizados.setMonto(20000.0);
        datosActualizados.setFechaPago(LocalDate.of(2026, 5, 10));
        datosActualizados.setEstadoPago("PENDIENTE");

        when(pagoRepository.findById(1)).thenReturn(Optional.of(pago));
        // ARRANGE: El microservicio externo retorna un AtencionDTO válido
        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);
        // ARRANGE: El tipo de pago con id=1 existe en el repositorio
        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        // ARRANGE: thenAnswer retorna el mismo objeto que recibe, simulando que se guardaron los cambios
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT: Actualización con los nuevos datos
        Pago resultado = pagoService.actualizar(1, datosActualizados);

        assertEquals("PENDIENTE", resultado.getEstadoPago());
        assertEquals(15000.0, resultado.getMonto());
        verify(pagoRepository, times(1)).findById(1);
        verify(pagoRepository, times(1)).save(pago);
    }

    // Eliminar pago existente
    // El pago existe, el repositorio ejecuta el borrado exactamente una vez
    @Test
    void eliminar_cuandoExiste_deberiaEliminar() {
        // ARRANGE: existsById retorna true indicando que el pago sí existe
        when(pagoRepository.existsById(1)).thenReturn(true);

        // ACT: Eliminación del pago
        pagoService.eliminar(1);

        // ASSERT: existsById y deleteById fueron llamados exactamente una vez con el id correcto
        verify(pagoRepository, times(1)).existsById(1);
        verify(pagoRepository, times(1)).deleteById(1);
    }

    // Eliminar pago no existente
    // El repositorio no encuentra el pago, lanza una excepción y nunca se intenta borrar
    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarExcepcion() {
        // ARRANGE: existsById retorna false indicando que el pago no existe
        when(pagoRepository.existsById(99)).thenReturn(false);

        // ACT: Lanza una excepción al no encontrar el pago 
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.eliminar(99));

        assertEquals("Pago no encontrado con id: 99", ex.getMessage());
        // El repositorio nunca debe intentar borrar si el pago no existe
        verify(pagoRepository, never()).deleteById(any());
    }
}
