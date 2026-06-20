package cl.duoc.examenes.Service;

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

import cl.duoc.examenes.dto.AtencionDTO;
import cl.duoc.examenes.model.OrdenExamen;
import cl.duoc.examenes.model.TipoExamen;
import cl.duoc.examenes.repository.OrdenExamenRepository;
import cl.duoc.examenes.repository.TipoExamenRepository;
import cl.duoc.examenes.service.OrdenExamenService;

@ExtendWith(MockitoExtension.class)
class OrdenExamenServiceTest {

    @Mock
    private OrdenExamenRepository ordenExamenRepository;

    @Mock
    private TipoExamenRepository tipoExamenRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrdenExamenService ordenExamenService;

    private OrdenExamen ordenExamen;
    private TipoExamen tipoExamen;
    private AtencionDTO atencionDTO;

    @BeforeEach
    void setUp() {
        tipoExamen = new TipoExamen();
        tipoExamen.setIdTipoExamen(1);
        tipoExamen.setNombreExamen("Examen de sangre");
        tipoExamen.setDescripcion("Examen para detectar anomalías en la sangre");
        tipoExamen.setPrecio(10000.0);

        ordenExamen = new OrdenExamen();
        ordenExamen.setIdOrdenExamen(1);
        ordenExamen.setIdAtencion(1);
        ordenExamen.setIdMascota(1);
        ordenExamen.setIdVeterinario(1);
        ordenExamen.setTipoExamen(tipoExamen);
        ordenExamen.setFechaSolicitud(LocalDate.of(2026, 5, 9));
        ordenExamen.setEstado("Pendiente");

        atencionDTO = new AtencionDTO();
        atencionDTO.setIdAtencion(1);
        atencionDTO.setIdMascota(1);
        atencionDTO.setIdVeterinario(1);
    }

    @Test
    void listar_deberiaRetornarListaDeOrdenes() {
        when(ordenExamenRepository.findAll()).thenReturn(Arrays.asList(ordenExamen));

        List<OrdenExamen> resultado = ordenExamenService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Pendiente", resultado.get(0).getEstado());
        verify(ordenExamenRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarOrden() {
        when(ordenExamenRepository.findById(1)).thenReturn(Optional.of(ordenExamen));

        OrdenExamen resultado = ordenExamenService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals("Pendiente", resultado.getEstado());
        verify(ordenExamenRepository, times(1)).findById(1);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(ordenExamenRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ordenExamenService.buscarPorId(99));

        assertEquals("Orden de examen no encontrada con id: 99", ex.getMessage());
    }

    @Test
    void buscarPorAtencion_deberiaRetornarListaDeOrdenes() {
        when(ordenExamenRepository.findByIdAtencion(1)).thenReturn(Arrays.asList(ordenExamen));

        List<OrdenExamen> resultado = ordenExamenService.buscarPorAtencion(1);

        assertEquals(1, resultado.size());
        verify(ordenExamenRepository, times(1)).findByIdAtencion(1);
    }

    @Test
    void buscarPorMascota_deberiaRetornarListaDeOrdenes() {
        when(ordenExamenRepository.findByIdMascota(1)).thenReturn(Arrays.asList(ordenExamen));

        List<OrdenExamen> resultado = ordenExamenService.buscarPorMascota(1);

        assertEquals(1, resultado.size());
        verify(ordenExamenRepository, times(1)).findByIdMascota(1);
    }

    @Test
    void buscarPorVeterinario_deberiaRetornarListaDeOrdenes() {
        when(ordenExamenRepository.findByIdVeterinario(1)).thenReturn(Arrays.asList(ordenExamen));

        List<OrdenExamen> resultado = ordenExamenService.buscarPorVeterinario(1);

        assertEquals(1, resultado.size());
        verify(ordenExamenRepository, times(1)).findByIdVeterinario(1);
    }

    @Test
    void guardar_cuandoDatosValidos_deberiaGuardarYRetornarOrden() {
        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);
        when(tipoExamenRepository.findById(1)).thenReturn(Optional.of(tipoExamen));
        when(ordenExamenRepository.save(any(OrdenExamen.class))).thenReturn(ordenExamen);

        OrdenExamen resultado = ordenExamenService.guardar(ordenExamen);

        assertNotNull(resultado);
        assertEquals("Pendiente", resultado.getEstado());
        verify(ordenExamenRepository, times(1)).save(ordenExamen);
    }

    @Test
    void guardar_cuandoIdAtencionEsNulo_deberiaLanzarExcepcion() {
        ordenExamen.setIdAtencion(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ordenExamenService.guardar(ordenExamen));

        assertEquals("El id de atención es obligatorio", ex.getMessage());
        verify(ordenExamenRepository, never()).save(any(OrdenExamen.class));
    }

    @Test
    void guardar_cuandoTipoExamenNoExiste_deberiaLanzarExcepcion() {
        OrdenExamen nueva = new OrdenExamen();
        nueva.setIdAtencion(1);
        nueva.setIdMascota(1);
        nueva.setIdVeterinario(1);
        TipoExamen tipoRef = new TipoExamen();
        tipoRef.setIdTipoExamen(99);
        nueva.setTipoExamen(tipoRef);
        nueva.setFechaSolicitud(LocalDate.now());
        nueva.setEstado("Pendiente");

        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);
        when(tipoExamenRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ordenExamenService.guardar(nueva));

        assertEquals("Tipo de examen no encontrado con id: 99", ex.getMessage());
        verify(ordenExamenRepository, never()).save(any(OrdenExamen.class));
    }

    @Test
    void actualizar_cuandoExiste_deberiaActualizarYRetornarOrden() {
        OrdenExamen datosActualizados = new OrdenExamen();
        datosActualizados.setIdAtencion(1);
        datosActualizados.setIdMascota(1);
        datosActualizados.setIdVeterinario(1);
        datosActualizados.setTipoExamen(tipoExamen);
        datosActualizados.setFechaSolicitud(LocalDate.of(2026, 5, 10));
        datosActualizados.setEstado("Completada");

        when(ordenExamenRepository.findById(1)).thenReturn(Optional.of(ordenExamen));
        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);
        when(tipoExamenRepository.findById(1)).thenReturn(Optional.of(tipoExamen));
        when(ordenExamenRepository.save(any(OrdenExamen.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdenExamen resultado = ordenExamenService.actualizar(1, datosActualizados);

        assertEquals("Completada", resultado.getEstado());
        verify(ordenExamenRepository, times(1)).findById(1);
        verify(ordenExamenRepository, times(1)).save(ordenExamen);
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminar() {
        when(ordenExamenRepository.existsById(1)).thenReturn(true);

        ordenExamenService.eliminar(1);

        verify(ordenExamenRepository, times(1)).existsById(1);
        verify(ordenExamenRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(ordenExamenRepository.existsById(99)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ordenExamenService.eliminar(99));

        assertEquals("Orden de examen no encontrada con id: 99", ex.getMessage());
        verify(ordenExamenRepository, never()).deleteById(any());
    }
}
