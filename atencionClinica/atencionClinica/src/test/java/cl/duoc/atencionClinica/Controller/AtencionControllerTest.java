package cl.duoc.atencionClinica.Controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cl.duoc.atencionClinica.controller.AtencionController;
import cl.duoc.atencionClinica.model.Atencion;
import cl.duoc.atencionClinica.model.Box;
import cl.duoc.atencionClinica.model.TipoAtencion;
import cl.duoc.atencionClinica.service.AtencionService;

// Carga solo el contexto web del controlador indicado, sin levantar toda la aplicación
@WebMvcTest(AtencionController.class)
public class AtencionControllerTest {

    @Autowired
    private MockMvc mockMvc; // cliente HTTP simulado para llamar al controller

    // @MockitoBean reemplaza el service real por uno simulado dentro del contexto de Spring
    @MockitoBean
    private AtencionService atencionService; // service simulado (no se conecta a BD ni a otros microservicios)

    // ObjectMapper convierte objetos Java a JSON para enviarlos en las peticiones
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

        // Box de ejemplo
        Box boxEjemplo = new Box();
        boxEjemplo.setIdBox(1);
        boxEjemplo.setNombreBox("Box 1");
        boxEjemplo.setDescripcion("Box de consultas generales");
        boxEjemplo.setEstado("Disponible");

        // Atención de ejemplo con todos sus datos, reutilizable en los tests
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

    // GET /api/v1/atenciones (listar)
    // El service retorna una lista con atenciones, controlador responde un 200 con los datos
    @Test
    void listar_retornaListaDeAtenciones() throws Exception {

        // ARRANGE: el service retorna una lista con la atención de ejemplo
        when(atencionService.listar()).thenReturn(List.of(atencionEjemplo));

        // ACT & ASSERT: Se hace GET y se verifica el código 200 y los datos del JSON
        mockMvc.perform(get("/api/v1/atenciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAtencion").value(1))
                .andExpect(jsonPath("$[0].diagnostico").value("Otitis leve"));

        // El service fue invocado exactamente una vez
        verify(atencionService, times(1)).listar();
    }

    // GET /api/v1/atenciones (listar sin datos)
    // El service retorna una lista vacía, se verifica que el controlador responda 204 sin cuerpo
    @Test
    void listar_sinAtenciones_retorna204() throws Exception {
        // ARRANGE: El service retorna una lista vacía, sin atenciones registradas
        when(atencionService.listar()).thenReturn(Collections.emptyList());

        // ACT & ASSERT: 204 No Content cuando no hay datos que retornar
        mockMvc.perform(get("/api/v1/atenciones"))
                .andExpect(status().isNoContent());

        verify(atencionService, times(1)).listar();
    }


    // GET /api/v1/atenciones/{id} (buscarPorId encontrado)
    // El service encuentra la atención, controlador responde un 200 con los datos correctos
    @Test
    void buscarPorId_encontrado_retorna200() throws Exception {
        // ARRANGE: El service retorna la atención cuando recibe el id=1
        when(atencionService.buscarPorId(1)).thenReturn(atencionEjemplo);

        // ACT & ASSERT: Se hace GET a /api/v1/atenciones/1 y se verifican los datos del JSON
        mockMvc.perform(get("/api/v1/atenciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAtencion").value(1))
                .andExpect(jsonPath("$.idCita").value(10))
                .andExpect(jsonPath("$.diagnostico").value("Otitis leve"));

        verify(atencionService, times(1)).buscarPorId(1);
    }

    // GET /api/v1/atenciones/{id} (buscarPorId no encontrado)
    // El service lanza una excepción y el controlador responde un 404
    @Test
    void buscarPorId_noEncontrado_retorna404() throws Exception {
        // ARRANGE: El service lanza RuntimeException cuando el id no existe
        when(atencionService.buscarPorId(99))
                .thenThrow(new RuntimeException("Atención no encontrada con id: 99"));

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/99"))
                .andExpect(status().isNotFound());

        verify(atencionService, times(1)).buscarPorId(99);
    }

    
    // GET /api/v1/atenciones/cita/{idCita} (BuscarPorCita con resultados)
    // El service retorna atenciones de la cita indicada y el controlador responde un 200
    @Test
    void buscarPorCita_retornaAtenciones() throws Exception {
        // ARRANGE: El service retorna la atención asociada a la cita con id=10
        when(atencionService.buscarPorCita(10)).thenReturn(List.of(atencionEjemplo));

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/cita/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCita").value(10));

        verify(atencionService, times(1)).buscarPorCita(10);
    }

    // GET /api/v1/atenciones/cita/{idCita} (BuscarPorCita sin resultados)
    // El service retorna lista vacía y el controlador responde un 204
    @Test
    void buscarPorCita_sinAtenciones_retorna204() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorCita(10)).thenReturn(Collections.emptyList());

        // ACT & ASSERT: 204 No Content cuando no hay atenciones para esa cita
        mockMvc.perform(get("/api/v1/atenciones/cita/10"))
                .andExpect(status().isNoContent());

        verify(atencionService, times(1)).buscarPorCita(10);
    }

    // GET /api/v1/atenciones/mascota/{idMascota} (buscarPorMascota con resultados)
    // El service retorna atenciones de la mascota indicada y el controlador responde un 200
    @Test
    void buscarPorMascota_retornaAtenciones() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorMascota(15)).thenReturn(List.of(atencionEjemplo));

        // ACT & ASSERT: Se hace GET y se verifica que el idMascota del JSON es el correcto
        mockMvc.perform(get("/api/v1/atenciones/mascota/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMascota").value(15));

        verify(atencionService, times(1)).buscarPorMascota(15);
    }

    // GET /api/v1/atenciones/mascota/{idMascota} (buscarPorMascota sin resultados)
    // El service retorna lista vacía y el controlador responde un 204
    @Test
    void buscarPorMascota_sinAtenciones_retorna204() throws Exception {
        // ARRANGE: El service retorna lista vacía
        when(atencionService.buscarPorMascota(15)).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/mascota/15"))
                .andExpect(status().isNoContent());

        verify(atencionService, times(1)).buscarPorMascota(15);
    }


    // GET /api/v1/atenciones/veterinario/{idVeterinario} (buscarPorVeterinario con resultados)
    // El service retorna atenciones del veterinario indicado y el controlador responde un 200
    @Test
    void buscarPorVeterinario_retornaAtenciones() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorVeterinario(20)).thenReturn(List.of(atencionEjemplo));

        // ACT & ASSERT: Se hace GET y se verifica que el idVeterinario del JSON es el correcto
        mockMvc.perform(get("/api/v1/atenciones/veterinario/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idVeterinario").value(20));

        verify(atencionService, times(1)).buscarPorVeterinario(20);
    }

    // GET /api/v1/atenciones/veterinario/{idVeterinario} (buscar por veterinario sin resultados)
    // El service retorna lista vacía y el controlador responde un 204
    @Test
    void buscarPorVeterinario_sinAtenciones_retorna204() throws Exception {
        // ARRANGE
        when(atencionService.buscarPorVeterinario(20)).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/atenciones/veterinario/20"))
                .andExpect(status().isNoContent());

        verify(atencionService, times(1)).buscarPorVeterinario(20);
    }


    // POST /api/v1/atenciones (guarda con datos válidos)
    // El service guarda la atención correctamente y el controlador responde 201 con los datos creados
    @Test
    void guardar_datosValidos_retorna201() throws Exception {
        // ARRANGE: 
        when(atencionService.guardar(any(Atencion.class))).thenReturn(atencionEjemplo);

        // ACT & ASSERT: Se hace POST enviando el JSON (201 Created con los datos guardados) 
        mockMvc.perform(post("/api/v1/atenciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atencionEjemplo))) 
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAtencion").value(1))
                .andExpect(jsonPath("$.diagnostico").value("Otitis leve"))
                .andExpect(jsonPath("$.tratamiento").value("Gotas óticas durante 7 días"));

        verify(atencionService, times(1)).guardar(any(Atencion.class));
    }

    // POST /api/v1/atenciones (guardar con datos inválidos)
    // El service lanza una excepción por datos inválidos y el controlador responde un 400
    @Test
    void guardar_datosInvalidos_retorna400() throws Exception {
        // ARRANGE
        // Simula el error que lanzaría el service al validar datos básicos
        when(atencionService.guardar(any(Atencion.class)))
                .thenThrow(new RuntimeException("El diagnóstico es obligatorio"));

        // ACT & ASSERT: 400 Bad Request cuando los datos enviados son inválidos
        mockMvc.perform(post("/api/v1/atenciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atencionEjemplo)))
                .andExpect(status().isBadRequest());

        verify(atencionService, times(1)).guardar(any(Atencion.class));
    }


    // PUT /api/v1/atenciones/{id} (actualizar atención existente)
    // El service actualiza la atención correctamente y el controlador responde 200 con los datos actualizados
    @Test
    void actualizar_atencionExistente_retorna200() throws Exception {
        // ARRANGE: Modifica el diagnóstico y el service retorna la atención actualizada
        // eq(1) exige que el id sea exactamente 1; any(Atencion.class) acepta cualquier objeto Atencion
        atencionEjemplo.setDiagnostico("Otitis moderada");
        when(atencionService.actualizar(eq(1), any(Atencion.class))).thenReturn(atencionEjemplo);

        // ACT & ASSERT: Se hace PUT enviando el JSON y se verifica 200 Ok con el diagnóstico actualizado
        mockMvc.perform(put("/api/v1/atenciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atencionEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAtencion").value(1))
                .andExpect(jsonPath("$.diagnostico").value("Otitis moderada"));

        verify(atencionService, times(1)).actualizar(eq(1), any(Atencion.class));
    }

    // PUT /api/v1/atenciones/{id} (actualizar atención no existente)
    // El service lanza una excepción y el controlador responde un 400
    @Test
    void actualizar_atencionNoExistente_retorna400() throws Exception {
        // ARRANGE: Simula el mismo error que lanza buscar Por Id dentro del service cuando no existe
        when(atencionService.actualizar(eq(99), any(Atencion.class)))
                .thenThrow(new RuntimeException("Atención no encontrada con id: 99"));

        // ACT & ASSERT: 400 Bad Request cuando la actualización falla
        mockMvc.perform(put("/api/v1/atenciones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atencionEjemplo)))
                .andExpect(status().isBadRequest());

        verify(atencionService, times(1)).actualizar(eq(99), any(Atencion.class));
    }


    // DELETE /api/v1/atenciones/{id} (eliminar atención existente)
    // El service elimina la atención sin errores y el controlador responde un 204 sin cuerpo
    @Test
    void eliminar_atencionExistente_retorna204() throws Exception {
        // ARRANGE: doNothing() se usa para métodos void, simula que eliminar(1) se ejecuta sin errores
        doNothing().when(atencionService).eliminar(1);

        // ACT & ASSERT: Se hace DELETE (204 No Content)
        mockMvc.perform(delete("/api/v1/atenciones/1"))
                .andExpect(status().isNoContent());

        verify(atencionService, times(1)).eliminar(1);
    }

    // DELETE /api/v1/atenciones/{id} (eliminar atención no existente)
    // El service lanza una excepción y el controlador responde un 404
    @Test
    void eliminar_atencionNoExistente_retorna404() throws Exception {
        // ARRANGE: doThrow() lanza una excepción cuando se intenta eliminar un id inexistente
        // Se usa doThrow en lugar de when() porque eliminar() es un método void
        doThrow(new RuntimeException("Atención no encontrada con id: 99"))
                .when(atencionService).eliminar(99);

        // ACT & ASSERT: 404 Not Found cuando la atención a eliminar no existe
        mockMvc.perform(delete("/api/v1/atenciones/99"))
                .andExpect(status().isNotFound());

        verify(atencionService, times(1)).eliminar(99);
    }
}


