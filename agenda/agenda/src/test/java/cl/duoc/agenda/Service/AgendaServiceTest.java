package cl.duoc.agenda.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import cl.duoc.agenda.dto.VeterinarioDTO;
import cl.duoc.agenda.model.Agenda;
import cl.duoc.agenda.repository.AgendaRepository;
import cl.duoc.agenda.service.AgendaService;

// Activa la integración de Mockito con JUnit 5 para crear mocks automáticamente
@ExtendWith(MockitoExtension.class)
public class AgendaServiceTest {
    
    @Mock
    private AgendaRepository agendaRepository; // repositorio simulado

    @Mock
    private RestTemplate restTemplate; // cliente HTTP simulado

    @InjectMocks
    private AgendaService agendaService; 

    private Agenda agendaEjemplo;

    @BeforeEach
    void setUp() {
        agendaEjemplo = new Agenda();
        agendaEjemplo.setIdAgenda(1);
        agendaEjemplo.setIdVeterinario(5);
        agendaEjemplo.setFecha(LocalDate.of(2026, 6, 10));
        agendaEjemplo.setHoraInicio(LocalTime.of(9, 0));
        agendaEjemplo.setHoraFin(LocalTime.of(18, 0));
        agendaEjemplo.setEstado("Disponible");
    }


    // Listar agendas 
    // El repositorio retorna una lista con agendas, se verifica que el servicio la devuelva completa
    @Test
    void listar_retornaListaDeAgendas() {

        // ARRANGE: el repositorio simulado retorna una lista con la agenda de ejemplo
        when(agendaRepository.findAll()).thenReturn(List.of(agendaEjemplo));

        List<Agenda> resultado = agendaService.listar();

        // ASSERT: la lista tiene un elemento y el estado es el esperado
        assertEquals(1, resultado.size());
        assertEquals("Disponible", resultado.get(0).getEstado());

        // Se verifica que findAll() fue invocado exactamente una vez
        verify(agendaRepository, times(1)).findAll();
    }


    // Buscar por Id (Agenda encontrada)
    // El repositorio retorna la agenda con el id buscado, se verifica que los datos sean correctos
    @Test
    void buscarPorId_encontrado() {
        // ARRANGE: Optional.of() simula que el registro sí existe en la base de datos
        when(agendaRepository.findById(1)).thenReturn(Optional.of(agendaEjemplo));

        // ACT: Se busca la agenda por su id
        Agenda resultado = agendaService.buscarPorId(1);

        // ASSERT: Los datos retornados deben coincidir con los de la agenda de ejemplo
        assertEquals(1, resultado.getIdAgenda());
        assertEquals(5, resultado.getIdVeterinario());
        assertEquals("Disponible", resultado.getEstado());
    }
    

    // Buscar por Id no encontrado (Agenda no encontrada)
    // El repositorio no encuentra la agenda, se verifica que el servicio lance una excepción con el mensaje correcto
    @Test
    void buscarPorId_noEncontrado() {
        // ARRANGE: Optional.empty() simula que el registro NO existe en la base de datos
        when(agendaRepository.findById(99)).thenReturn(Optional.empty());

        // ACT: assertThrows captura la excepción lanzada por el servicio sin detener el test
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> agendaService.buscarPorId(99));

        // ASSERT: El mensaje de error indica el id que no fue encontrado
        assertEquals("Agenda no encontrada con id: 99", error.getMessage());
    }


    // Buscar por veterinario
    // El repositorio retorna las agendas del veterinario indicado, se verifica que correspondan a ese veterinario
    @Test
    void buscarPorVeterinario_retornaAgendas() {
        // ARRANGE: El repositorio retorna la agenda asociada al veterinario con id=5
        when(agendaRepository.findByIdVeterinario(5)).thenReturn(List.of(agendaEjemplo));

        // ACT: Se buscan las agendas del veterinario
        List<Agenda> resultado = agendaService.buscarPorVeterinario(5);

        // ASSERT: La lista tiene un elemento y pertenece al veterinario correcto
        assertEquals(1, resultado.size());
        assertEquals(5, resultado.get(0).getIdVeterinario());
    }


    // Actualizar agenda (veterinarioDTO) Preguntar profesor
    // Se simula la consulta al microservicio de veterinarios, la validación de cruces y el guardado
    @Test
    void actualizar_agendaValida_actualizaCorrectamente() {
        
        // ARRANGE: La agenda con id=1 existe en el repositorio
        when(agendaRepository.findById(1)).thenReturn(Optional.of(agendaEjemplo));

        VeterinarioDTO vetDTO = new VeterinarioDTO();
        when(restTemplate.getForObject(anyString(), eq(VeterinarioDTO.class))).thenReturn(vetDTO);
        when(agendaRepository.buscarAgendasCruzadas(
                anyInt(), any(), any(), any(), eq(1)))
                .thenReturn(List.of());
        when(agendaRepository.save(any())).thenReturn(agendaEjemplo);

        Agenda datosNuevos = new Agenda();
        datosNuevos.setIdVeterinario(5);
        datosNuevos.setFecha(LocalDate.of(2026, 6, 20));
        datosNuevos.setHoraInicio(LocalTime.of(8, 0));
        datosNuevos.setHoraFin(LocalTime.of(14, 0));
        datosNuevos.setEstado("Ocupada");

        // ACT: Se ejecuta la actualización con los nuevos datos
        Agenda resultado = agendaService.actualizar(1, datosNuevos);

        // ASSERT: El resultado no es nulo y el repositorio guardó exactamente una vez
        assertNotNull(resultado);
        verify(agendaRepository, times(1)).save(any());
    }


    // Actualizar agenda no existente (Retorna un error 404)
    // El repositorio no encuentra la agenda, se verifica que se lance excepción y nunca se intente guardar
    @Test
    void actualizar_agendaNoExistente_lanzaExcepcion() {

        // ARRANGE: La agenda con id=99 no existe en el repositorio
        when(agendaRepository.findById(99)).thenReturn(Optional.empty());

        Agenda datosNuevos = new Agenda();
        datosNuevos.setIdVeterinario(5);
        datosNuevos.setFecha(LocalDate.of(2026, 6, 20));
        datosNuevos.setHoraInicio(LocalTime.of(8, 0));
        datosNuevos.setHoraFin(LocalTime.of(14, 0));
        datosNuevos.setEstado("Ocupada");

        // ACT: Debe lanzar una excepción al no encontrar la agenda
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> agendaService.actualizar(99, datosNuevos));

        assertEquals("Agenda no encontrada con id: 99", error.getMessage());

        // El repositorio nunca debe intentar guardar si la agenda no existe
        verify(agendaRepository, never()).save(any());
    }


    // Eliminar agenda existente
    // La agenda existe, se verifica que el repositorio ejecute el borrado exactamente una vez
    @Test
    void eliminar_agendaExistente() {
        // ARRANGE: existsById retorna true indicando que la agenda sí existe
        when(agendaRepository.existsById(1)).thenReturn(true);

        // ACT: se solicita la eliminación de la agenda
        agendaService.eliminar(1);

        // ASSERT: deleteById fue llamado exactamente una vez con el id correcto
        verify(agendaRepository, times(1)).deleteById(1);
    }

    //Eliminar agenda no existente (Retorna un error 404)
    // El repositorio no encuentra la agenda, se verifica que se lance excepción y nunca se intente borrar
    @Test
    void eliminar_agendaNoExistente_lanzaExcepcion() {
        // ARRANGE: existsById retorna false indicando que la agenda no existe
        when(agendaRepository.existsById(99)).thenReturn(false);

        // ACT & ASSERT: Lanza una excepción al no encontrar la agenda
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> agendaService.eliminar(99));

        assertEquals("Agenda no encontrada con id: 99", error.getMessage());
        verify(agendaRepository, never()).deleteById(any());
    }
}


