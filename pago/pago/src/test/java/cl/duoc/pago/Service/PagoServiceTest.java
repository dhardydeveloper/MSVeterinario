package cl.duoc.pago.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import cl.duoc.pago.dto.AtencionDTO;
import cl.duoc.pago.model.Pago;
import cl.duoc.pago.model.TipoPago;
import cl.duoc.pago.repository.PagoRepository;
import cl.duoc.pago.repository.TipoPagoRepository;
import cl.duoc.pago.service.PagoService;

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

        pago = new Pago();
        pago.setIdPago(1);
        pago.setIdAtencion(1);
        pago.setTipoPago(tipoPago);
        pago.setMonto(15000.0);
        pago.setFechaPago(LocalDate.of(2026, 5, 9));
        pago.setEstadoPago("PAGADO");

        atencionDTO = new AtencionDTO();
        atencionDTO.setIdAtencion(1);
        atencionDTO.setPrecioBase(15000.0);
    }

    @Test
    void listar_deberiaRetornarListaDePagos() {
        when(pagoRepository.findAll()).thenReturn(Arrays.asList(pago));

        List<Pago> resultado = pagoService.listar();

        assertEquals(1, resultado.size());
        assertEquals("PAGADO", resultado.get(0).getEstadoPago());
        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarPago() {
        when(pagoRepository.findById(1)).thenReturn(Optional.of(pago));

        Pago resultado = pagoService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals("PAGADO", resultado.getEstadoPago());
        verify(pagoRepository, times(1)).findById(1);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(pagoRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.buscarPorId(99));

        assertEquals("Pago no encontrado con id: 99", ex.getMessage());
    }

    @Test
    void buscarPorAtencion_deberiaRetornarListaDePagos() {
        when(pagoRepository.findByIdAtencion(1)).thenReturn(Arrays.asList(pago));

        List<Pago> resultado = pagoService.buscarPorAtencion(1);

        assertEquals(1, resultado.size());
        verify(pagoRepository, times(1)).findByIdAtencion(1);
    }

    @Test
    void buscarPorEstado_deberiaRetornarListaDePagos() {
        when(pagoRepository.findByEstadoPago("PAGADO")).thenReturn(Arrays.asList(pago));

        List<Pago> resultado = pagoService.buscarPorEstado("PAGADO");

        assertEquals(1, resultado.size());
        verify(pagoRepository, times(1)).findByEstadoPago("PAGADO");
    }

    @Test
    void guardar_cuandoDatosValidos_deberiaGuardarYRetornarPago() {
        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);
        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        Pago resultado = pagoService.guardar(pago);

        assertNotNull(resultado);
        assertEquals("PAGADO", resultado.getEstadoPago());
        assertEquals(15000.0, resultado.getMonto());
        verify(pagoRepository, times(1)).save(pago);
    }

    @Test
    void guardar_cuandoIdAtencionEsNulo_deberiaLanzarExcepcion() {
        pago.setIdAtencion(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.guardar(pago));

        assertEquals("El id de atención es obligatorio", ex.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void guardar_cuandoTipoPagoNoExiste_deberiaLanzarExcepcion() {
        Pago nuevo = new Pago();
        nuevo.setIdAtencion(1);
        TipoPago tipoRef = new TipoPago();
        tipoRef.setIdTipoPago(99);
        nuevo.setTipoPago(tipoRef);
        nuevo.setFechaPago(LocalDate.now());
        nuevo.setEstadoPago("PENDIENTE");

        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);
        when(tipoPagoRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.guardar(nuevo));

        assertEquals("Tipo de pago no encontrado con id: 99", ex.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void actualizar_cuandoExiste_deberiaActualizarYRetornarPago() {
        Pago datosActualizados = new Pago();
        datosActualizados.setIdAtencion(1);
        datosActualizados.setTipoPago(tipoPago);
        datosActualizados.setMonto(20000.0);
        datosActualizados.setFechaPago(LocalDate.of(2026, 5, 10));
        datosActualizados.setEstadoPago("PENDIENTE");

        when(pagoRepository.findById(1)).thenReturn(Optional.of(pago));
        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);
        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pago resultado = pagoService.actualizar(1, datosActualizados);

        assertEquals("PENDIENTE", resultado.getEstadoPago());
        assertEquals(15000.0, resultado.getMonto());
        verify(pagoRepository, times(1)).findById(1);
        verify(pagoRepository, times(1)).save(pago);
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminar() {
        when(pagoRepository.existsById(1)).thenReturn(true);

        pagoService.eliminar(1);

        verify(pagoRepository, times(1)).existsById(1);
        verify(pagoRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(pagoRepository.existsById(99)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.eliminar(99));

        assertEquals("Pago no encontrado con id: 99", ex.getMessage());
        verify(pagoRepository, never()).deleteById(any());
    }
}
