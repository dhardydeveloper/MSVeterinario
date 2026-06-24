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

// Activa la integración de Mockito con JUnit 5 para crear mocks automáticamente
@ExtendWith(MockitoExtension.class)
public class AtencionServiceTest {

    // @Mock crea una versión simulada del repositorio, sin conectarse a la base de datos real
    @Mock
    private AtencionRepository atencionRepository; // repositorio simulado

    @Mock
    private TipoAtencionRepository tipoAtencionRepository; // repositorio simulado

    @Mock
    private BoxRepository boxRepository; // repositorio simulado

    @Mock
    private RestTemplate restTemplate; // se mantiene como mock por @InjectMocks, pero no se usa en estas pruebas

     // @InjectMocks crea una instancia real de AtencionService e inyecta todos los mocks anteriores en ella
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

        // Box de ejemplo
        boxEjemplo = new Box();
        boxEjemplo.setIdBox(1);
        boxEjemplo.setNombreBox("Box 1");
        boxEjemplo.setDescripcion("Box de consultas generales");
        boxEjemplo.setEstado("Disponible");

        // Atención de ejemplo con todos sus datos, reutilizable en los tests
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


    // Listar atenciones
    // El repositorio retorna una lista con atenciones, se verifica que el servicio la devuelva completa
    @Test
    void listar_retornaListaDeAtenciones() {
        // ARRANGE
        when(atencionRepository.findAll()).thenReturn(List.of(atencionEjemplo));

        // ACT: Llama al método del servicio que se quiere probar
        List<Atencion> resultado = atencionService.listar();

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals("Otitis leve", resultado.get(0).getDiagnostico());
        verify(atencionRepository, times(1)).findAll();
    }


    // Buscar por Id (atención encontrada)
    // El repositorio retorna la atención con el id buscado
    @Test
    void buscarPorId_encontrado() {
        // ARRANGE: Optional.of() simula que el registro sí existe en la base de datos
        when(atencionRepository.findById(1)).thenReturn(Optional.of(atencionEjemplo));

        // ACT: Busca la atención por su id
        Atencion resultado = atencionService.buscarPorId(1);

        // ASSERT: Los datos retornados deben coincidir con los de la atención de ejemplo
        assertEquals(1, resultado.getIdAtencion());
        assertEquals(10, resultado.getIdCita());
        assertEquals("Otitis leve", resultado.getDiagnostico());
    }

    // Buscar por Id (atención no encontrada)
    // El repositorio no encuentra la atención, lanza una excepción con un mensaje correcto
    @Test
    void buscarPorId_noEncontrado() {
        // ARRANGE: Optional.empty() simula que el registro NO existe en la base de datos
        when(atencionRepository.findById(99)).thenReturn(Optional.empty());

        // ACT: assertThrows captura la excepción lanzada por el servicio sin detener el test
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> atencionService.buscarPorId(99));

        // ASSERT:  El mensaje de error indica el id que no fue encontrado
        assertEquals("Atención no encontrada con id: 99", error.getMessage());
    }


    // Buscar por cita 
    // El repositorio retorna las atenciones asociadas a una cita, se verifica que correspondan a esa cita
    @Test
    void buscarPorCita_retornaAtenciones() {
        // ARRANGE: El repositorio retorna la atención asociada a la cita con id=10
        when(atencionRepository.findByIdCita(10)).thenReturn(List.of(atencionEjemplo));

        // ACT: Busca las atenciones por id de cita
        List<Atencion> resultado = atencionService.buscarPorCita(10);

        // ASSERT: La lista tiene un elemento y pertenece a la cita correcta
        assertEquals(1, resultado.size());
        assertEquals(10, resultado.get(0).getIdCita());
    }


    // Buscar por mascota
    // El repositorio retorna las atenciones asociadas a una mascota, se verifica que correspondan a esa mascota
    @Test
    void buscarPorMascota_retornaAtenciones() {
        // ARRANGE: El repositorio retorna la atención asociada a la mascota con id=15
        when(atencionRepository.findByIdMascota(15)).thenReturn(List.of(atencionEjemplo));

        // ACT: buscan las atenciones por id de mascota
        List<Atencion> resultado = atencionService.buscarPorMascota(15);

        // ASSERT: La lista tiene un elemento y pertenece a la mascota correcta
        assertEquals(1, resultado.size());
        assertEquals(15, resultado.get(0).getIdMascota());
    }


    // Buscar por veterinario
    // El repositorio retorna las atenciones del veterinario indicado 
    @Test
    void buscarPorVeterinario_retornaAtenciones() {
        // ARRANGE: El repositorio retorna la atención asociada al veterinario con id=20
        when(atencionRepository.findByIdVeterinario(20)).thenReturn(List.of(atencionEjemplo));

        // ACT: Busca las atenciones por id de veterinario
        List<Atencion> resultado = atencionService.buscarPorVeterinario(20);

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals(20, resultado.get(0).getIdVeterinario());
    }
    

    // Actualizar atención no existente
    // El repositorio no encuentra la atención, Lanza excepción y nunca se debe guardar
    @Test
    void actualizar_atencionNoExistente_lanzaExcepcion() {
        // ARRANGE: La atención con id=99 no existe en el repositorio
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

        // ACT & ASSERT:     BuscarPorId() lanza la excepción ANTES de llegar a validarCita(),
        // por lo tanto no depende de datos de otro microservicio.
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> atencionService.actualizar(99, datosNuevos));

        assertEquals("Atención no encontrada con id: 99", error.getMessage());

        // El repositorio nunca debe intentar guardar si la atención no existe
        verify(atencionRepository, never()).save(any());
    }


    // Eliminar atención existente
    // La atención existe, se verifica que el repositorio ejecute el borrado exactamente una vez
    @Test
    void eliminar_atencionExistente() {
        // ARRANGE: existsById retorna true indicando que la atención sí existe
        when(atencionRepository.existsById(1)).thenReturn(true);

        // ACT: Se solicita la eliminación de la atención
        atencionService.eliminar(1);

        // ASSERT: deleteById llamado exactamente una vez con el id correcto
        verify(atencionRepository, times(1)).deleteById(1);
    }


    // Eliminar atención no existente
    // El repositorio no encuentra la atención, lanza excepción y nunca se intenta borrar
    @Test
    void eliminar_atencionNoExistente_lanzaExcepcion() {
        // ARRANGE: existsById retorna false indicando que la atención no existe
        when(atencionRepository.existsById(99)).thenReturn(false);

        // ACT & ASSERT: Lanza una excepción al no encontrar la atención 
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> atencionService.eliminar(99));

        assertEquals("Atención no encontrada con id: 99", error.getMessage());

        // El repositorio nunca debe intentar borrar si la atención no existe
        verify(atencionRepository, never()).deleteById(any());
    }
}