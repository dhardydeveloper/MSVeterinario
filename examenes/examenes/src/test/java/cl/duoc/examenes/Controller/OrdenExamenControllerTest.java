package cl.duoc.examenes.Controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cl.duoc.examenes.controller.OrdenExamenController;
import cl.duoc.examenes.model.OrdenExamen;
import cl.duoc.examenes.model.TipoExamen;
import cl.duoc.examenes.service.OrdenExamenService;

@ExtendWith(MockitoExtension.class)
class OrdenExamenControllerTest {

    @Mock
    private OrdenExamenService ordenExamenService;

    @InjectMocks
    private OrdenExamenController ordenExamenController;

    // MockMvc simula peticiones HTTP sin levantar un servidor real
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private OrdenExamen ordenExamen;
    private TipoExamen tipoExamen;

    // @BeforeEach se ejecuta antes de cada @Test para preparar los datos de prueba desde cero
    @BeforeEach
    void setUp() {
        // Se inicializa el entorno de pruebas con el controlador
        mockMvc = MockMvcBuilders.standaloneSetup(ordenExamenController).build();

        tipoExamen = new TipoExamen();
        tipoExamen.setIdTipoExamen(1);
        tipoExamen.setNombreExamen("Examen de sangre");

        ordenExamen = new OrdenExamen();
        ordenExamen.setIdOrdenExamen(1);
        ordenExamen.setIdAtencion(1);
        ordenExamen.setIdMascota(1);
        ordenExamen.setIdVeterinario(1);
        ordenExamen.setTipoExamen(tipoExamen);
        ordenExamen.setFechaSolicitud(LocalDate.of(2026, 5, 9));
        ordenExamen.setEstado("Pendiente");
    }

    //GET /api/v1/ordenes-examen (listar con resultados)
    // El service retorna una lista con órdenes y el controlador responde un 200 con los datos
    @Test
    void listar_cuandoHayOrdenes_deberiaRetornar200() throws Exception {
        when(ordenExamenService.listar()).thenReturn(Arrays.asList(ordenExamen));

        // El endpoint devuelve 200 OK y el estado "Pendiente"
        mockMvc.perform(get("/api/v1/ordenes-examen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("Pendiente"));

        verify(ordenExamenService, times(1)).listar();
    }

    // GET /api/v1/ordenes-examen (listar sin resultados)
    @Test
    void listar_cuandoNoHayOrdenes_deberiaRetornar204() throws Exception {
        when(ordenExamenService.listar()).thenReturn(Collections.emptyList());

        // El endpoint devuelve un 204 No Content
        mockMvc.perform(get("/api/v1/ordenes-examen"))
                .andExpect(status().isNoContent());
    }


    // GET /api/v1/ordenes-examen/{id} (buscarPorId encontrado)
    @Test
    void buscarPorId_cuandoExiste_deberiaRetornar200() throws Exception {
        when(ordenExamenService.buscarPorId(1)).thenReturn(ordenExamen);

        // ACT + ASSERT: Se hace GET a /api/v1/ordenes-examen/1 y se verifica el estado del JSON
        mockMvc.perform(get("/api/v1/ordenes-examen/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("Pendiente"));
    }

    // GET /api/v1/ordenes-examen/{id} (buscarPorId no encontrado)
    @Test
    void buscarPorId_cuandoNoExiste_deberiaRetornar404() throws Exception {
        when(ordenExamenService.buscarPorId(99))
                .thenThrow(new RuntimeException("Orden de examen no encontrada con id: 99"));

        // ACT + ASSERT: 404 Not Found cuando la orden no existe
        mockMvc.perform(get("/api/v1/ordenes-examen/99"))
                .andExpect(status().isNotFound());
    }

    // GET /api/v1/ordenes-examen/atencion/{idAtencion} (buscarPorAtencion con resultados)
    @Test
    void buscarPorAtencion_cuandoHayResultados_deberiaRetornar200() throws Exception {
        when(ordenExamenService.buscarPorAtencion(1)).thenReturn(Arrays.asList(ordenExamen));

        mockMvc.perform(get("/api/v1/ordenes-examen/atencion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAtencion").value(1));
    }

    // GET /api/v1/ordenes-examen/atencion/{idAtencion} (buscarPorAtencion sin resultados)
    @Test
    void buscarPorAtencion_cuandoNoHayResultados_deberiaRetornar204() throws Exception {
        when(ordenExamenService.buscarPorAtencion(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/ordenes-examen/atencion/1"))
                .andExpect(status().isNoContent());
    }

    // GET /api/v1/ordenes-examen/mascota/{idMascota} (buscarPorMascota con resultados)

    @Test
    void buscarPorMascota_cuandoHayResultados_deberiaRetornar200() throws Exception {
        when(ordenExamenService.buscarPorMascota(1)).thenReturn(Arrays.asList(ordenExamen));

        mockMvc.perform(get("/api/v1/ordenes-examen/mascota/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMascota").value(1));
    }

    // GET /api/v1/ordenes-examen/mascota/{idMascota} (buscarPorMascota sin resultados)
    @Test
    void buscarPorMascota_cuandoNoHayResultados_deberiaRetornar204() throws Exception {
        when(ordenExamenService.buscarPorMascota(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/ordenes-examen/mascota/1"))
                .andExpect(status().isNoContent());
    }

    // GET /api/v1/ordenes-examen/veterinario/{idVeterinario} (buscarPorVeterinario con resultados)
    @Test
    void buscarPorVeterinario_cuandoHayResultados_deberiaRetornar200() throws Exception {
        when(ordenExamenService.buscarPorVeterinario(1)).thenReturn(Arrays.asList(ordenExamen));

        mockMvc.perform(get("/api/v1/ordenes-examen/veterinario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idVeterinario").value(1));
    }

    // GET /api/v1/ordenes-examen/veterinario/{idVeterinario} (buscarPorVeterinario sin resultados)
    @Test
    void buscarPorVeterinario_cuandoNoHayResultados_deberiaRetornar204() throws Exception {
        when(ordenExamenService.buscarPorVeterinario(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/ordenes-examen/veterinario/1"))
                .andExpect(status().isNoContent());
    }


    // POST /api/v1/ordenes-examen (guardar con datos válidos)
    @Test
    void guardar_cuandoEsExitoso_deberiaRetornar201() throws Exception {
        // ARRANGE: El service guarda y retorna la orden recién creada
        // any(OrdenExamen.class) acepta cualquier objeto OrdenExamen como argumento
        when(ordenExamenService.guardar(any(OrdenExamen.class))).thenReturn(ordenExamen);

        mockMvc.perform(post("/api/v1/ordenes-examen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordenExamen))) // convierte la orden a JSON 
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("Pendiente"));

        verify(ordenExamenService, times(1)).guardar(any(OrdenExamen.class));
    }

    // POST /api/v1/ordenes-examen (guardar con datos inválidos)
    @Test
    void guardar_cuandoFalla_deberiaRetornar400() throws Exception {
        when(ordenExamenService.guardar(any(OrdenExamen.class)))
                .thenThrow(new RuntimeException("Error al guardar"));

        mockMvc.perform(post("/api/v1/ordenes-examen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordenExamen)))
                .andExpect(status().isBadRequest());
    }


    // PUT /api/v1/ordenes-examen/{id} (actualizar orden existente)
    @Test
    void actualizar_cuandoExiste_deberiaRetornar200() throws Exception {
        // ARRANGE: El service retorna la orden actualizada
        // any(Integer.class) acepta cualquier id; any(OrdenExamen.class) acepta cualquier objeto OrdenExamen
        when(ordenExamenService.actualizar(any(Integer.class), any(OrdenExamen.class))).thenReturn(ordenExamen);

        mockMvc.perform(put("/api/v1/ordenes-examen/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordenExamen)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("Pendiente"));

        verify(ordenExamenService, times(1)).actualizar(any(Integer.class), any(OrdenExamen.class));
    }

    // PUT /api/v1/ordenes-examen/{id} (actualizar orden no existente)
    @Test
    void actualizar_cuandoNoExiste_deberiaRetornar400() throws Exception {
        when(ordenExamenService.actualizar(any(Integer.class), any(OrdenExamen.class)))
                .thenThrow(new RuntimeException("Orden de examen no encontrada con id: 99"));

        mockMvc.perform(put("/api/v1/ordenes-examen/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordenExamen)))
                .andExpect(status().isBadRequest());
    }


    // DELETE /api/v1/ordenes-examen/{id} (eliminar orden existente)
    @Test
    void eliminar_cuandoExiste_deberiaRetornar204() throws Exception {
        mockMvc.perform(delete("/api/v1/ordenes-examen/1"))
                .andExpect(status().isNoContent());

        verify(ordenExamenService, times(1)).eliminar(1);
    }

    // DELETE /api/v1/ordenes-examen/{id} (eliminar orden no existente)
    @Test
    void eliminar_cuandoNoExiste_deberiaRetornar404() throws Exception {
        // ARRANGE: doThrow() lanza una excepción cuando se intenta eliminar un id inexistente
        // se usa doThrow en lugar de when() porque eliminar() es un método void
        Mockito.doThrow(new RuntimeException("Orden de examen no encontrada con id: 99"))
                .when(ordenExamenService).eliminar(99);

        mockMvc.perform(delete("/api/v1/ordenes-examen/99"))
                .andExpect(status().isNotFound());
    }
}
