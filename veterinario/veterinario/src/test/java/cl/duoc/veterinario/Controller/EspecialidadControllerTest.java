package cl.duoc.veterinario.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.veterinario.controller.EspecialidadController;
import cl.duoc.veterinario.model.Especialidad;
import cl.duoc.veterinario.service.EspecialidadService;

@WebMvcTest(EspecialidadController.class) // levanta la capa web, sin BD
public class EspecialidadControllerTest {

    @Autowired
    private MockMvc llamadaFalsa; // sirve para crear llamadas HTTP falsas

    @Autowired
    private ObjectMapper objectMapper; // sirve para convertir objetos Java <-> JSON en los body de POST/PUT

    @MockitoBean
    private EspecialidadService service;

    private Especialidad especialidadEjemplo;

    @BeforeEach
    void setUp() {

        especialidadEjemplo = new Especialidad();
        especialidadEjemplo.setId(1);
        especialidadEjemplo.setNombre("Cirugía");
    }

    // ===================== LISTAR =====================

    @Test
    void listar_retorna200ConLista() throws Exception {

        when(service.listar()).thenReturn(List.of(especialidadEjemplo));

        llamadaFalsa.perform(get("/api/v1/especialidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Cirugía"));
    }

    @Test
    void listar_retorna204CuandoListaVacia() throws Exception {

        when(service.listar()).thenReturn(Collections.emptyList());

        llamadaFalsa.perform(get("/api/v1/especialidades"))
                .andExpect(status().isNoContent());
    }

    // ===================== BUSCAR POR ID =====================

    @Test
    void buscarPorId_retorna200() throws Exception {

        when(service.buscarPorId(1)).thenReturn(especialidadEjemplo);

        llamadaFalsa.perform(get("/api/v1/especialidades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cirugía"));
    }

    @Test
    void buscarPorId_retorna404CuandoNoExiste() throws Exception {

        when(service.buscarPorId(99)).thenThrow(new RuntimeException("Especialidad no encontrada"));

        llamadaFalsa.perform(get("/api/v1/especialidades/99"))
                .andExpect(status().isNotFound());
    }

    // ===================== CREAR (POST) =====================

    @Test
    void guardar_retorna200() throws Exception {

        when(service.guardar(any(Especialidad.class))).thenReturn(especialidadEjemplo);

        llamadaFalsa.perform(post("/api/v1/especialidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(especialidadEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cirugía"));
    }

    // Nota: a diferencia de VeterinarioController, el "guardar" de este controller
    // no tiene try/catch, por lo tanto no existe un caso 400 definido para testear aquí.

    // ===================== ACTUALIZAR (PUT) =====================

    @Test
    void actualizar_retorna200() throws Exception {

        when(service.actualizar(eq(1), any(Especialidad.class))).thenReturn(especialidadEjemplo);

        llamadaFalsa.perform(put("/api/v1/especialidades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(especialidadEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cirugía"));
    }

    @Test
    void actualizar_retorna404CuandoNoExiste() throws Exception {

        when(service.actualizar(eq(99), any(Especialidad.class)))
                .thenThrow(new RuntimeException("Especialidad no encontrada"));

        llamadaFalsa.perform(put("/api/v1/especialidades/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(especialidadEjemplo)))
                .andExpect(status().isNotFound());
    }

    // ===================== ELIMINAR (DELETE) =====================

    @Test
    void eliminar_retorna204() throws Exception {

        llamadaFalsa.perform(delete("/api/v1/especialidades/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_retorna404CuandoNoExiste() throws Exception {

        doThrow(new RuntimeException("Especialidad no encontrada")).when(service).eliminar(99);

        llamadaFalsa.perform(delete("/api/v1/especialidades/99"))
                .andExpect(status().isNotFound());
    }
}