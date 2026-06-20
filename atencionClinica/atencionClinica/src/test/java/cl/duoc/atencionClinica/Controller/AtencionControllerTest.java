package cl.duoc.atencionClinica.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import cl.duoc.atencionClinica.controller.AtencionController;
import cl.duoc.atencionClinica.model.Atencion;
import cl.duoc.atencionClinica.model.TipoAtencion;
import cl.duoc.atencionClinica.service.AtencionService;

import com.fasterxml.jackson.databind.ObjectMapper;
import cl.duoc.atencionClinica.model.Box;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AtencionController.class)
public class AtencionControllerTest {

    @Autowired
    private MockMvc mockMvc; // cliente HTTP simulado para llamar al controller

    @MockBean
    private AtencionService atencionService; // service simulado (no se conecta a BD ni a otros microservicios)

    private ObjectMapper objectMapper;
    private Atencion atencionEjemplo;

    @BeforeEach
    void setUp() {
        // ObjectMapper con soporte para LocalDate (java.time)
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        TipoAtencion tipoAtencionEjemplo = new TipoAtencion();
        tipoAtencionEjemplo.setIdTipoAtencion(1);
        tipoAtencionEjemplo.setNombreTipo("Consulta General");
        tipoAtencionEjemplo.setDescripcion("Evaluación médica general");
        tipoAtencionEjemplo.setPrecioBase(25000.0);

        Box boxEjemplo = new Box();
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

     // GET /api/v1/atenciones — listar 

    @Test
    void listar_retornaListaDeAtenciones() throws Exception {
        // ARRANGE
        when(atencionService.listar()).thenReturn(List.of(atencionEjemplo));

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAtencion").value(1))
                .andExpect(jsonPath("$[0].diagnostico").value("Otitis leve"));

        verify(atencionService, times(1)).listar();
    }

    @Test
    void listar_sinAtenciones_retorna204() throws Exception {
        // ARRANGE
        when(atencionService.listar()).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones"))
                .andExpect(status().isNoContent());

        verify(atencionService, times(1)).listar();
    }


    // GET /api/v1/atenciones/{id} — buscarPorId

    @Test
    void buscarPorId_encontrado_retorna200() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorId(1)).thenReturn(atencionEjemplo);

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAtencion").value(1))
                .andExpect(jsonPath("$.idCita").value(10))
                .andExpect(jsonPath("$.diagnostico").value("Otitis leve"));

        verify(atencionService, times(1)).buscarPorId(1);
    }


    @Test
    void buscarPorId_noEncontrado_retorna404() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorId(99))
                .thenThrow(new RuntimeException("Atención no encontrada con id: 99"));

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/99"))
                .andExpect(status().isNotFound());

        verify(atencionService, times(1)).buscarPorId(99);
    }

    
    // GET /api/v1/atenciones/cita/{idCita} — buscarPorCita 

    @Test
    void buscarPorCita_retornaAtenciones() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorCita(10)).thenReturn(List.of(atencionEjemplo));

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/cita/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCita").value(10));

        verify(atencionService, times(1)).buscarPorCita(10);
    }

    @Test
    void buscarPorCita_sinAtenciones_retorna204() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorCita(10)).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/cita/10"))
                .andExpect(status().isNoContent());

        verify(atencionService, times(1)).buscarPorCita(10);
    }

    // GET /api/v1/atenciones/mascota/{idMascota} — buscarPorMascota 

    @Test
    void buscarPorMascota_retornaAtenciones() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorMascota(15)).thenReturn(List.of(atencionEjemplo));

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/mascota/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMascota").value(15));

        verify(atencionService, times(1)).buscarPorMascota(15);
    }

    @Test
    void buscarPorMascota_sinAtenciones_retorna204() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorMascota(15)).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/mascota/15"))
                .andExpect(status().isNoContent());

        verify(atencionService, times(1)).buscarPorMascota(15);
    }


    // GET /api/v1/atenciones/veterinario/{idVeterinario} — buscarPorVeterinario 

    @Test
    void buscarPorVeterinario_retornaAtenciones() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorVeterinario(20)).thenReturn(List.of(atencionEjemplo));

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/veterinario/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idVeterinario").value(20));

        verify(atencionService, times(1)).buscarPorVeterinario(20);
    }

    @Test
    void buscarPorVeterinario_sinAtenciones_retorna204() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorVeterinario(20)).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/veterinario/20"))
                .andExpect(status().isNoContent());

        verify(atencionService, times(1)).buscarPorVeterinario(20);
    }

    // POST /api/v1/atenciones — guardar 

    @Test
    void guardar_datosValidos_retorna201() throws Exception {
        // ARRANGE
        when(atencionService.guardar(any(Atencion.class))).thenReturn(atencionEjemplo);

        // ACT & ASSERT
        mockMvc.perform(post("/api/v1/atenciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atencionEjemplo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAtencion").value(1))
                .andExpect(jsonPath("$.diagnostico").value("Otitis leve"))
                .andExpect(jsonPath("$.tratamiento").value("Gotas óticas durante 7 días"));

        verify(atencionService, times(1)).guardar(any(Atencion.class));
    }

    @Test
    void guardar_datosInvalidos_retorna400() throws Exception {
        // ARRANGE
        // Simula el error que lanzaría el service al validar datos básicos
        when(atencionService.guardar(any(Atencion.class)))
                .thenThrow(new RuntimeException("El diagnóstico es obligatorio"));

        // ACT & ASSERT
        mockMvc.perform(post("/api/v1/atenciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atencionEjemplo)))
                .andExpect(status().isBadRequest());

        verify(atencionService, times(1)).guardar(any(Atencion.class));
    }


    // PUT /api/v1/atenciones/{id} — actualizar 

    @Test
    void actualizar_atencionExistente_retorna200() throws Exception {
        // ARRANGE
        atencionEjemplo.setDiagnostico("Otitis moderada");
        when(atencionService.actualizar(eq(1), any(Atencion.class))).thenReturn(atencionEjemplo);

        // ACT & ASSERT
        mockMvc.perform(put("/api/v1/atenciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atencionEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAtencion").value(1))
                .andExpect(jsonPath("$.diagnostico").value("Otitis moderada"));

        verify(atencionService, times(1)).actualizar(eq(1), any(Atencion.class));
    }

    @Test
    void actualizar_atencionNoExistente_retorna400() throws Exception {
        // ARRANGE
        // Simula el mismo error que lanza buscarPorId() dentro del service cuando no existe
        when(atencionService.actualizar(eq(99), any(Atencion.class)))
                .thenThrow(new RuntimeException("Atención no encontrada con id: 99"));

        // ACT & ASSERT
        mockMvc.perform(put("/api/v1/atenciones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atencionEjemplo)))
                .andExpect(status().isBadRequest());

        verify(atencionService, times(1)).actualizar(eq(99), any(Atencion.class));
    }


    // DELETE /api/v1/atenciones/{id} — eliminar 

    @Test
    void eliminar_atencionExistente_retorna204() throws Exception {
        // ARRANGE
        doNothing().when(atencionService).eliminar(1);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/v1/atenciones/1"))
                .andExpect(status().isNoContent());

        verify(atencionService, times(1)).eliminar(1);
    }

    @Test
    void eliminar_atencionNoExistente_retorna404() throws Exception {
        // ARRANGE
        doThrow(new RuntimeException("Atención no encontrada con id: 99"))
                .when(atencionService).eliminar(99);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/v1/atenciones/99"))
                .andExpect(status().isNotFound());

        verify(atencionService, times(1)).eliminar(99);
    }
}


