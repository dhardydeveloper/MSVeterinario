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

import cl.duoc.veterinario.controller.VeterinarioController;
import cl.duoc.veterinario.model.Veterinario;
import cl.duoc.veterinario.service.VeterinarioService;

@WebMvcTest(VeterinarioController.class) // levanta la capa web, sin BD
public class VeterinarioControllerTest {

    @Autowired
    private MockMvc llamadaFalsa; // sirve para crear llamadas HTTP falsas

    @Autowired
    private ObjectMapper objectMapper; // sirve para convertir objetos Java <-> JSON en los body de POST/PUT

    @MockitoBean
    private VeterinarioService service;

    private Veterinario veterinarioEjemplo;

    @BeforeEach
    void setUp() {

        veterinarioEjemplo = new Veterinario();
        veterinarioEjemplo.setId(1);
        veterinarioEjemplo.setNombre("David");
        veterinarioEjemplo.setApellido("Hardy");
        veterinarioEjemplo.setRut("17621180-6");
    }

    // ===================== LISTAR =====================

    @Test
    void listar_retorna200ConLista() throws Exception {

        // ARRANGE: el service retorna una lista con un veterinario
        when(service.listar()).thenReturn(List.of(veterinarioEjemplo));

        // ACT + ASSERT
        llamadaFalsa.perform(get("/api/v1/veterinarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("David"))
                .andExpect(jsonPath("$[0].rut").value("17621180-6"));
    }

    @Test
    void listar_retorna204CuandoListaVacia() throws Exception {

        // ARRANGE: el service retorna una lista vacía
        when(service.listar()).thenReturn(Collections.emptyList());

        // ACT + ASSERT
        llamadaFalsa.perform(get("/api/v1/veterinarios"))
                .andExpect(status().isNoContent());
    }

    // ===================== BUSCAR POR ID =====================

    @Test
    void buscarPorId_retorna200() throws Exception {

        // ARRANGE: el service si recibe un 1, retorna el veterinario
        when(service.buscarPorId(1)).thenReturn(veterinarioEjemplo);

        // ACT + ASSERT
        llamadaFalsa.perform(get("/api/v1/veterinarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("David")); // es un objeto, no una lista -> $.nombre
    }

    @Test
    void buscarPorId_retorna404CuandoNoExiste() throws Exception {

        // ARRANGE: el service lanza una excepción si no encuentra el id
        when(service.buscarPorId(99)).thenThrow(new RuntimeException("Veterinario no encontrado"));

        // ACT + ASSERT
        llamadaFalsa.perform(get("/api/v1/veterinarios/99"))
                .andExpect(status().isNotFound());
    }

    // ===================== BUSCAR POR RUT =====================

    @Test
    void buscarPorRut_retorna200() throws Exception {

        when(service.buscarPorRut("17621180-6")).thenReturn(veterinarioEjemplo);

        llamadaFalsa.perform(get("/api/v1/veterinarios/rut/17621180-6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("David"));
    }

    @Test
    void buscarPorRut_retorna404CuandoNoExiste() throws Exception {

        when(service.buscarPorRut("0-0")).thenThrow(new RuntimeException("Veterinario no encontrado"));

        llamadaFalsa.perform(get("/api/v1/veterinarios/rut/0-0"))
                .andExpect(status().isNotFound());
    }

    
    // ===================== CREAR (POST) =====================

    @Test
    void guardar_retorna200() throws Exception {

        when(service.guardar(any(Veterinario.class))).thenReturn(veterinarioEjemplo);

        llamadaFalsa.perform(post("/api/v1/veterinarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veterinarioEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("David"));
    }

    @Test
    void guardar_retorna400CuandoFalla() throws Exception {

        when(service.guardar(any(Veterinario.class))).thenThrow(new RuntimeException("Datos inválidos"));

        llamadaFalsa.perform(post("/api/v1/veterinarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veterinarioEjemplo)))
                .andExpect(status().isBadRequest());
    }

    // ===================== ACTUALIZAR (PUT) =====================

    @Test
    void actualizar_retorna200() throws Exception {

        when(service.actualizar(eq(1), any(Veterinario.class))).thenReturn(veterinarioEjemplo);

        llamadaFalsa.perform(put("/api/v1/veterinarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veterinarioEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("David"));
    }

    @Test
    void actualizar_retorna404CuandoNoExiste() throws Exception {

        when(service.actualizar(eq(99), any(Veterinario.class)))
                .thenThrow(new RuntimeException("Veterinario no encontrado"));

        llamadaFalsa.perform(put("/api/v1/veterinarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veterinarioEjemplo)))
                .andExpect(status().isNotFound());
    }

    // ===================== ELIMINAR (DELETE) =====================

    @Test
    void eliminar_retorna204() throws Exception {

        // No es necesario stubear nada: por defecto un mock no lanza excepción al llamar a un método void

        llamadaFalsa.perform(delete("/api/v1/veterinarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_retorna404CuandoNoExiste() throws Exception {

        doThrow(new RuntimeException("Veterinario no encontrado")).when(service).eliminar(99);

        llamadaFalsa.perform(delete("/api/v1/veterinarios/99"))
                .andExpect(status().isNotFound());
    }
}