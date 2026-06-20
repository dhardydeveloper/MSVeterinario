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


    //Listar agendas 
    @Test
    void listar_retornaListaDeAgendas() {
        when(agendaRepository.findAll()).thenReturn(List.of(agendaEjemplo));

        List<Agenda> resultado = agendaService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Disponible", resultado.get(0).getEstado());
        verify(agendaRepository, times(1)).findAll();
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


    // Buscar por veterinario
    @Test
    void buscarPorVeterinario_retornaAgendas() {
        when(agendaRepository.findByIdVeterinario(5)).thenReturn(List.of(agendaEjemplo));

        List<Agenda> resultado = agendaService.buscarPorVeterinario(5);

        assertEquals(1, resultado.size());
        assertEquals(5, resultado.get(0).getIdVeterinario());
    }


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


    //Actualizar agenda no existente (Retorna un error 404)
    @Test
    void actualizar_agendaNoExistente_lanzaExcepcion() {
        when(agendaRepository.findById(99)).thenReturn(Optional.empty());

        Agenda datosNuevos = new Agenda();
        datosNuevos.setIdVeterinario(5);
        datosNuevos.setFecha(LocalDate.of(2026, 6, 20));
        datosNuevos.setHoraInicio(LocalTime.of(8, 0));
        datosNuevos.setHoraFin(LocalTime.of(14, 0));
        datosNuevos.setEstado("Ocupada");

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> agendaService.actualizar(99, datosNuevos));

        assertEquals("Agenda no encontrada con id: 99", error.getMessage());
        verify(agendaRepository, never()).save(any());
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


