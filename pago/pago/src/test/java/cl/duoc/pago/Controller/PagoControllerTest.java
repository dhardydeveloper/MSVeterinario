package cl.duoc.pago.Controller;

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

import cl.duoc.pago.controller.PagoController;
import cl.duoc.pago.model.Pago;
import cl.duoc.pago.model.TipoPago;
import cl.duoc.pago.service.PagoService;

@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PagoController pagoController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private Pago pago;
    private TipoPago tipoPago;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pagoController).build();

        tipoPago = new TipoPago();
        tipoPago.setIdTipoPago(1);
        tipoPago.setNombreTipoPago("EFECTIVO");

        pago = new Pago();
        pago.setIdPago(1);
        pago.setIdAtencion(1);
        pago.setTipoPago(tipoPago);
        pago.setMonto(15000.0);
        pago.setFechaPago(LocalDate.of(2026, 5, 9));
        pago.setEstadoPago("PAGADO");
    }

    @Test
    void listar_cuandoHayPagos_deberiaRetornar200() throws Exception {
        when(pagoService.listar()).thenReturn(Arrays.asList(pago));

        mockMvc.perform(get("/api/v1/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estadoPago").value("PAGADO"));

        verify(pagoService, times(1)).listar();
    }

    @Test
    void listar_cuandoNoHayPagos_deberiaRetornar204() throws Exception {
        when(pagoService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pagos"))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornar200() throws Exception {
        when(pagoService.buscarPorId(1)).thenReturn(pago);

        mockMvc.perform(get("/api/v1/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoPago").value("PAGADO"));
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaRetornar404() throws Exception {
        when(pagoService.buscarPorId(99))
                .thenThrow(new RuntimeException("Pago no encontrado con id: 99"));

        mockMvc.perform(get("/api/v1/pagos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorAtencion_cuandoHayResultados_deberiaRetornar200() throws Exception {
        when(pagoService.buscarPorAtencion(1)).thenReturn(Arrays.asList(pago));

        mockMvc.perform(get("/api/v1/pagos/atencion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAtencion").value(1));
    }

    @Test
    void buscarPorAtencion_cuandoNoHayResultados_deberiaRetornar204() throws Exception {
        when(pagoService.buscarPorAtencion(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pagos/atencion/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarPorEstado_cuandoHayResultados_deberiaRetornar200() throws Exception {
        when(pagoService.buscarPorEstado("PAGADO")).thenReturn(Arrays.asList(pago));

        mockMvc.perform(get("/api/v1/pagos/estado/PAGADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estadoPago").value("PAGADO"));
    }

    @Test
    void buscarPorEstado_cuandoNoHayResultados_deberiaRetornar204() throws Exception {
        when(pagoService.buscarPorEstado("PENDIENTE")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pagos/estado/PENDIENTE"))
                .andExpect(status().isNoContent());
    }

    @Test
    void guardar_cuandoEsExitoso_deberiaRetornar201() throws Exception {
        when(pagoService.guardar(any(Pago.class))).thenReturn(pago);

        mockMvc.perform(post("/api/v1/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pago)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estadoPago").value("PAGADO"));

        verify(pagoService, times(1)).guardar(any(Pago.class));
    }

    @Test
    void guardar_cuandoFalla_deberiaRetornar400() throws Exception {
        when(pagoService.guardar(any(Pago.class)))
                .thenThrow(new RuntimeException("Error al guardar"));

        mockMvc.perform(post("/api/v1/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pago)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_cuandoExiste_deberiaRetornar200() throws Exception {
        when(pagoService.actualizar(any(Integer.class), any(Pago.class))).thenReturn(pago);

        mockMvc.perform(put("/api/v1/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pago)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoPago").value("PAGADO"));

        verify(pagoService, times(1)).actualizar(any(Integer.class), any(Pago.class));
    }

    @Test
    void actualizar_cuandoNoExiste_deberiaRetornar400() throws Exception {
        when(pagoService.actualizar(any(Integer.class), any(Pago.class)))
                .thenThrow(new RuntimeException("Pago no encontrado con id: 99"));

        mockMvc.perform(put("/api/v1/pagos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pago)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminar_cuandoExiste_deberiaRetornar204() throws Exception {
        mockMvc.perform(delete("/api/v1/pagos/1"))
                .andExpect(status().isNoContent());

        verify(pagoService, times(1)).eliminar(1);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaRetornar404() throws Exception {
        Mockito.doThrow(new RuntimeException("Pago no encontrado con id: 99"))
                .when(pagoService).eliminar(99);

        mockMvc.perform(delete("/api/v1/pagos/99"))
                .andExpect(status().isNotFound());
    }
}
