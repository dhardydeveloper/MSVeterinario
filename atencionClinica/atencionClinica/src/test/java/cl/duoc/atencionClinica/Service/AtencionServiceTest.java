package cl.duoc.atencionClinica.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import cl.duoc.atencionClinica.model.Atencion;
import cl.duoc.atencionClinica.model.Box;
import cl.duoc.atencionClinica.model.TipoAtencion;
import cl.duoc.atencionClinica.repository.AtencionRepository;
import cl.duoc.atencionClinica.repository.BoxRepository;
import cl.duoc.atencionClinica.repository.TipoAtencionRepository;
import cl.duoc.atencionClinica.service.AtencionService;


@ExtendWith(MockitoExtension.class)
public class AtencionServiceTest {

    @Mock
    private AtencionRepository atencionRepository; // repositorio simulado

    @Mock
    private TipoAtencionRepository tipoAtencionRepository; // repositorio simulado

    @Mock
    private BoxRepository boxRepository; // repositorio simulado

    @Mock
    private RestTemplate restTemplate; // se mantiene como mock por @InjectMocks, pero no se usa en estas pruebas

    @InjectMocks
    private AtencionService atencionService;

    private Atencion atencionEjemplo;
    private TipoAtencion tipoAtencionEjemplo;
    private Box boxEjemplo;

    @BeforeEach
    void setUp() {
        tipoAtencionEjemplo = new TipoAtencion();
        tipoAtencionEjemplo.setIdTipoAtencion(1);
        tipoAtencionEjemplo.setNombreTipo("Consulta General");
        tipoAtencionEjemplo.setDescripcion("Evaluación médica general");
        tipoAtencionEjemplo.setPrecioBase(25000.0);

        boxEjemplo = new Box();
        boxEjemplo.setIdBox(1);
        boxEjemplo.setNombreBox("Box 1");
        boxEjemplo.setDescripcion("Box de consultas generales");
        boxEjemplo.setEstado("Disponible");

        atencionEjemplo = new Atencion();
        atencionEjemplo.setIdAtencion(1);
        atencionEjemplo.setIdCita(10);
        atencionEjemplo.setIdMascota(15);
        atencionEjemplo.setIdVeterinario(20);
        atencionEjemplo.setTipoAtencion(tipoAtencionEjemplo);
        atencionEjemplo.setBox(boxEjemplo);
        atencionEjemplo.setFechaAtencion(LocalDate.of(2026, 6, 10));
        atencionEjemplo.setDiagnostico("Otitis leve");
        atencionEjemplo.setTratamiento("Gotas óticas durante 7 días");
        atencionEjemplo.setObservaciones("Control en 15 días");
        atencionEjemplo.setPesoActual(12.5);
    }

    // ---------- Listar ----------
    @Test
    void listar_retornaListaDeAtenciones() {
        // ARRANGE
        when(atencionRepository.findAll()).thenReturn(List.of(atencionEjemplo));

        // ACT
        List<Atencion> resultado = atencionService.listar();

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals("Otitis leve", resultado.get(0).getDiagnostico());
        verify(atencionRepository, times(1)).findAll();
    }

    // Buscar por Id 
    @Test
    void buscarPorId_encontrado() {
        // ARRANGE
        when(atencionRepository.findById(1)).thenReturn(Optional.of(atencionEjemplo));

        // ACT
        Atencion resultado = atencionService.buscarPorId(1);

        // ASSERT
        assertEquals(1, resultado.getIdAtencion());
        assertEquals(10, resultado.getIdCita());
        assertEquals("Otitis leve", resultado.getDiagnostico());
    }

    // Buscar por Id (caso no encontrado)
    @Test
    void buscarPorId_noEncontrado() {
        // ARRANGE
        when(atencionRepository.findById(99)).thenReturn(Optional.empty());

        // ACT
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> atencionService.buscarPorId(99));

        // ASSERT
        assertEquals("Atención no encontrada con id: 99", error.getMessage());
    }

    // Buscar por cita 
    @Test
    void buscarPorCita_retornaAtenciones() {
        // ARRANGE
        when(atencionRepository.findByIdCita(10)).thenReturn(List.of(atencionEjemplo));

        // ACT
        List<Atencion> resultado = atencionService.buscarPorCita(10);

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals(10, resultado.get(0).getIdCita());
    }


    // Buscar por mascota
    @Test
    void buscarPorMascota_retornaAtenciones() {
        // ARRANGE
        when(atencionRepository.findByIdMascota(15)).thenReturn(List.of(atencionEjemplo));

        // ACT
        List<Atencion> resultado = atencionService.buscarPorMascota(15);

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals(15, resultado.get(0).getIdMascota());
    }


    // Buscar por veterinario 
    @Test
    void buscarPorVeterinario_retornaAtenciones() {
        // ARRANGE
        when(atencionRepository.findByIdVeterinario(20)).thenReturn(List.of(atencionEjemplo));

        // ACT
        List<Atencion> resultado = atencionService.buscarPorVeterinario(20);

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals(20, resultado.get(0).getIdVeterinario());
    }
    

    //  Actualizar (no existente)
    @Test
    void actualizar_atencionNoExistente_lanzaExcepcion() {
        // ARRANGE
        when(atencionRepository.findById(99)).thenReturn(Optional.empty());

        Atencion datosNuevos = new Atencion();
        datosNuevos.setIdCita(10);
        datosNuevos.setIdMascota(15);
        datosNuevos.setIdVeterinario(20);
        datosNuevos.setTipoAtencion(tipoAtencionEjemplo);
        datosNuevos.setBox(boxEjemplo);
        datosNuevos.setFechaAtencion(LocalDate.of(2026, 6, 10));
        datosNuevos.setDiagnostico("Otitis leve");
        datosNuevos.setTratamiento("Gotas óticas");

        // ACT & ASSERT
        // buscarPorId() lanza la excepción ANTES de llegar a validarCita(),
        // por lo tanto no depende de datos de otro microservicio.
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> atencionService.actualizar(99, datosNuevos));

        assertEquals("Atención no encontrada con id: 99", error.getMessage());
        verify(atencionRepository, never()).save(any());
    }


    // Eliminar 
    @Test
    void eliminar_atencionExistente() {
        // ARRANGE
        when(atencionRepository.existsById(1)).thenReturn(true);

        // ACT
        atencionService.eliminar(1);

        // ASSERT
        verify(atencionRepository, times(1)).deleteById(1);
    }


    // Eliminar (no existente)
    @Test
    void eliminar_atencionNoExistente_lanzaExcepcion() {
        // ARRANGE
        when(atencionRepository.existsById(99)).thenReturn(false);

        // ACT & ASSERT
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> atencionService.eliminar(99));

        assertEquals("Atención no encontrada con id: 99", error.getMessage());
        verify(atencionRepository, never()).deleteById(any());
    }
}