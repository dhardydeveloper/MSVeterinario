package cl.duoc.agenda.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.agenda.controller.AgendaController;
import cl.duoc.agenda.model.Agenda;
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


    // Listar agendas - 200 cuando hay datos
    @Test
    void listar_retorna200ConDatos() throws Exception {
        // ARRANGE: el service retorna una lista con una agenda
        when(service.listar()).thenReturn(List.of(agendaEjemplo));

        // ACT + ASSERT
        llamadaFalsa.perform(get("/api/v1/agendas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAgenda").value(1))
                .andExpect(jsonPath("$[0].idVeterinario").value(5));
    }


    // Listar agendas - 204 cuando no hay datos
    @Test
    void listar_retorna204CuandoVacia() throws Exception {
        // ARRANGE: el service retorna una lista vacía
        when(service.listar()).thenReturn(List.of());

        // ACT + ASSERT
        llamadaFalsa.perform(get("/api/v1/agendas"))
                .andExpect(status().isNoContent());
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
    @Test
    void guardar_retorna201() throws Exception {
        // ARRANGE: el service guarda y retorna la agenda creada
        when(service.guardar(any(Agenda.class))).thenReturn(agendaEjemplo);

        // ACT + ASSERT
        llamadaFalsa.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaEjemplo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAgenda").value(1));
    }


    // Guardar agenda - 400 cuando hay datos inválidos
    @Test
    void guardar_retorna400CuandoFalla() throws Exception {
        // ARRANGE: el service lanza una excepción si los datos son inválidos
        when(service.guardar(any(Agenda.class)))
                .thenThrow(new RuntimeException("El estado es obligatorio"));

        // ACT + ASSERT
        llamadaFalsa.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaEjemplo)))
                .andExpect(status().isBadRequest());
    }


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


    // Actualizar agenda - 400 cuando no existe o falla la validación
    @Test
    void actualizar_retorna400CuandoFalla() throws Exception {
        // ARRANGE: el service lanza una excepción si la agenda no existe o los datos son inválidos
        when(service.actualizar(eq(99), any(Agenda.class)))
                .thenThrow(new RuntimeException("Agenda no encontrada con id: 99"));

        // ACT + ASSERT
        llamadaFalsa.perform(put("/api/v1/agendas/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaEjemplo)))
                .andExpect(status().isBadRequest());
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