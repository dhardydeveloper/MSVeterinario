package cl.duoc.fichaclinica.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cl.duoc.fichaclinica.controller.FichaClinicaController;
import cl.duoc.fichaclinica.model.FichaClinica;
import cl.duoc.fichaclinica.service.FichaClinicaService;

@ExtendWith(MockitoExtension.class)
class FichaClinicaControllerTest {

    @Mock
    private FichaClinicaService fichaClinicaService;

    @InjectMocks
    private FichaClinicaController fichaClinicaController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private FichaClinica fichaClinica;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fichaClinicaController).build();

        fichaClinica = new FichaClinica();
        fichaClinica.setIdFicha(1);
        fichaClinica.setIdMascota(1);
        fichaClinica.setIdVeterinario("VET001");
        fichaClinica.setAntecedentes("Sin antecedentes");
        fichaClinica.setAlergias("Sin alergias");
        fichaClinica.setEnfermedadesPrevias("Ninguna");
        fichaClinica.setObservaciones("Todo normal");
        fichaClinica.setFechaCreacion(LocalDate.of(2026, 5, 9));
    }

    @Test
    void listar_cuandoHayFichas_deberiaRetornar200() throws Exception {
        when(fichaClinicaService.listar()).thenReturn(Arrays.asList(fichaClinica));

        mockMvc.perform(get("/api/v1/fichas-clinicas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].antecedentes").value("Sin antecedentes"));

        verify(fichaClinicaService, times(1)).listar();
    }

    @Test
    void listar_cuandoNoHayFichas_deberiaRetornar204() throws Exception {
        when(fichaClinicaService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/fichas-clinicas"))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornar200() throws Exception {
        when(fichaClinicaService.buscarPorId(1)).thenReturn(fichaClinica);

        mockMvc.perform(get("/api/v1/fichas-clinicas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMascota").value(1));
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaRetornar404() throws Exception {
        when(fichaClinicaService.buscarPorId(99))
                .thenThrow(new RuntimeException("Ficha clínica no encontrada con id: 99"));

        mockMvc.perform(get("/api/v1/fichas-clinicas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorMascota_cuandoHayResultados_deberiaRetornar200() throws Exception {
        when(fichaClinicaService.buscarPorMascota(1)).thenReturn(Arrays.asList(fichaClinica));

        mockMvc.perform(get("/api/v1/fichas-clinicas/mascota/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMascota").value(1));
    }

    @Test
    void buscarPorMascota_cuandoNoHayResultados_deberiaRetornar204() throws Exception {
        when(fichaClinicaService.buscarPorMascota(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/fichas-clinicas/mascota/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void guardar_cuandoEsExitoso_deberiaRetornar201() throws Exception {
        when(fichaClinicaService.guardar(any(FichaClinica.class))).thenReturn(fichaClinica);

        mockMvc.perform(post("/api/v1/fichas-clinicas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fichaClinica)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.antecedentes").value("Sin antecedentes"));

        verify(fichaClinicaService, times(1)).guardar(any(FichaClinica.class));
    }

    @Test
    void guardar_cuandoFalla_deberiaRetornar400() throws Exception {
        when(fichaClinicaService.guardar(any(FichaClinica.class)))
                .thenThrow(new RuntimeException("Error al guardar"));

        mockMvc.perform(post("/api/v1/fichas-clinicas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fichaClinica)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_cuandoExiste_deberiaRetornar200() throws Exception {
        when(fichaClinicaService.actualizar(any(Integer.class), any(FichaClinica.class))).thenReturn(fichaClinica);

        mockMvc.perform(put("/api/v1/fichas-clinicas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fichaClinica)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.antecedentes").value("Sin antecedentes"));

        verify(fichaClinicaService, times(1)).actualizar(any(Integer.class), any(FichaClinica.class));
    }

    @Test
    void actualizar_cuandoNoExiste_deberiaRetornar400() throws Exception {
        when(fichaClinicaService.actualizar(any(Integer.class), any(FichaClinica.class)))
                .thenThrow(new RuntimeException("Ficha clínica no encontrada con id: 99"));

        mockMvc.perform(put("/api/v1/fichas-clinicas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fichaClinica)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminar_cuandoExiste_deberiaRetornar204() throws Exception {
        mockMvc.perform(delete("/api/v1/fichas-clinicas/1"))
                .andExpect(status().isNoContent());

        verify(fichaClinicaService, times(1)).eliminar(1);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaRetornar404() throws Exception {
        Mockito.doThrow(new RuntimeException("Ficha clínica no encontrada con id: 99"))
                .when(fichaClinicaService).eliminar(99);

        mockMvc.perform(delete("/api/v1/fichas-clinicas/99"))
                .andExpect(status().isNotFound());
    }
}
