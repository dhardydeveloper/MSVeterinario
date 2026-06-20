package cl.duoc.agenda.controller;

import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.ContentResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import cl.duoc.agenda.service.AgendaService;


@WebMvcTest(AgendaController.class)
public class AgendaControllerTest {

    @Autowired
    private MockMvc llamadaFalsa; // sirve para crear llamadas HTTP falsas

    @MockBean
    private AgendaService service;

    @Autowired
    private ObjectMapper objectMapper; // sirve para convertir objetos a JSON

    private Agenda agendaEjemplo;

    @BeforeEach
    void setUp() {
        agendaEjemplo = new Agenda();
        agendaEjemplo.setIdAgenda(1);
        agendaEjemplo.setIdVeterinario(5);
        agendaEjemplo.setFecha(LocalDate.of(2026, 6, 8));
        agendaEjemplo.setHoraInicio(LocalTime.of(9, 0));
        agendaEjemplo.setHoraFin(LocalTime.of(18, 0));
        agendaEjemplo.setEstado("Disponible");
    }

    // Buscar agenda por ID - 200
    @Test
    void buscarPorId_retorna200() throws Exception {
        // ARRANGE: el service si recibe un 1, retorna la agenda
        when(service.buscarPorId(1)).thenReturn(agendaEjemplo);

        // ACT + ASSERT
        llamadaFalsa.perform(get("/api/v1/agendas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAgenda").value(1))
                .andExpect(jsonPath("$.idVeterinario").value(5));
    }

    // Buscar agenda por ID - 404 cuando no existe
    @Test
    void buscarPorId_retorna404CuandoNoExiste() throws Exception {
        // ARRANGE: el service lanza una excepción si no encuentra la agenda
        when(service.buscarPorId(99)).thenThrow(new RuntimeException("Agenda no encontrada con id: 99"));

        // ACT + ASSERT
        llamadaFalsa.perform(get("/api/v1/agendas/99"))
                .andExpect(status().isNotFound());
    }


    // Guardar agenda - 201


    // Actualizar agenda - 200
    @Test
    void actualizar_retorna200() throws Exception {
        // ARRANGE: el service retorna la agenda actualizada
        when(service.actualizar(eq(1), any(Agenda.class))).thenReturn(agendaEjemplo);

        // ACT + ASSERT
        llamadaFalsa.perform(put("/api/v1/agendas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAgenda").value(1));
    }

    // Eliminar agenda - 204
    @Test
    void eliminar_retorna204() throws Exception {
        // ARRANGE: el service elimina la agenda sin retornar nada 
        doNothing().when(service).eliminar(1);

        // ACT + ASSERT
        llamadaFalsa.perform(delete("/api/v1/agendas/1"))
                .andExpect(status().isNoContent());
    }

    // Eliminar agenda - 404 cuando no existe
    @Test
    void eliminar_retorna404CuandoNoExiste() throws Exception {
        // ARRANGE: el service lanza una excepción si no encuentra la agenda
        doThrow(new RuntimeException("Agenda no encontrada con id: 99")).when(service).eliminar(99);

        // ACT + ASSERT
        llamadaFalsa.perform(delete("/api/v1/agendas/99"))
                .andExpect(status().isNotFound());
    }





}

   