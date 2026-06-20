package cl.duoc.agenda.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

import cl.duoc.agenda.model.Agenda;
import cl.duoc.agenda.repository.AgendaRepository;


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

    // Buscar por Id
    @Test
    void buscarPorId_encontrado() {
        // ARRANGE
        when(agendaRepository.findById(1)).thenReturn(Optional.of(agendaEjemplo));

        // ACT
        Agenda resultado = agendaService.buscarPorId(1);

        // ASSERT
        assertEquals(1, resultado.getIdAgenda());
        assertEquals(5, resultado.getIdVeterinario());
        assertEquals("Disponible", resultado.getEstado());
    }

    // Buscar por Id no encontrado
    @Test
    void buscarPorId_noEncontrado() {
        // ARRANGE
        when(agendaRepository.findById(99)).thenReturn(Optional.empty());

        // ACT: 
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> agendaService.buscarPorId(99));

        // ASSERT
        assertEquals("Agenda no encontrada con id: 99", error.getMessage());
    }


    //Guardar agenda


    // Actualizar agenda (veterinarioDTO) Preguntar profesor
    @Test
    void actualizar_agendaValida_actualizaCorrectamente() {
        // ARRANGE
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

        // ACT
        Agenda resultado = agendaService.actualizar(1, datosNuevos);

        // ASSERT
        assertNotNull(resultado);
        verify(agendaRepository, times(1)).save(any());
    }

    // eliminar
    @Test
    void eliminar_agendaExistente() {
        // ARRANGE
        when(agendaRepository.existsById(1)).thenReturn(true);

        // ACT
        agendaService.eliminar(1);

        // ASSERT
        verify(agendaRepository, times(1)).deleteById(1);
    }

    //Eliminar agenda no existente (Retorna un error 404)
    @Test
    void eliminar_agendaNoExistente_lanzaExcepcion() {
        // ARRANGE
        when(agendaRepository.existsById(99)).thenReturn(false);

        // ACT & ASSERT
        RuntimeException error = assertThrows(RuntimeException.class,
                () -> agendaService.eliminar(99));

        assertEquals("Agenda no encontrada con id: 99", error.getMessage());
        verify(agendaRepository, never()).deleteById(any());
    }
}



