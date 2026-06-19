package cl.duoc.registro.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import cl.duoc.registro.controller.RegistroController;
import cl.duoc.registro.model.Cliente;
import cl.duoc.registro.model.Mascota;
import cl.duoc.registro.service.RegistroService;

/**
 * Clase de pruebas unitarias para RegistroController.
 *
 * Se utiliza MockMvc en modo "standalone" (sin levantar el contexto completo
 * de Spring) junto con Mockito para simular el comportamiento de RegistroService.
 * Esto permite probar la capa web (rutas, códigos HTTP, serialización JSON)
 * de forma aislada y rápida, sin depender de la base de datos ni de otros beans.
 *
 * No se incluyen pruebas para el endpoint del DTO (RegistroDTO), tal como fue solicitado.
 */
@ExtendWith(MockitoExtension.class) // Habilita la integración de Mockito 
class RegistroControllerTest {

    // Mock del servicio: simula las respuestas de RegistroService sin ejecutar lógica real
    @Mock
    private RegistroService registroService;

    // Inyecta el mock anterior dentro del controlador que vamos a probar
    @InjectMocks
    private RegistroController registroController;

    // Cliente HTTP simulado que nos permite "llamar" a los endpoints del controlador sin un servidor real
    private MockMvc mockMvc;

    // Utilidad de Jackson para convertir objetos Java a JSON (cuerpo de las peticiones POST/PUT)
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Objetos de prueba reutilizados en varios tests
    private Cliente cliente;
    private Mascota mascota;

    @BeforeEach
    void setUp() {
        // Construimos el MockMvc apuntando directamente al controlador con el servicio mockeado.
        // Standalone setup evita levantar todo el contexto de Spring Boot (más rápido y aislado).
        mockMvc = MockMvcBuilders.standaloneSetup(registroController).build();

        // Preparamos un cliente de ejemplo con datos válidos
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setRut("11111111-1");
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setTelefono("987654321");
        cliente.setCorreo("juan.perez@mail.com");
        cliente.setDireccion("Calle Falsa 123");
        cliente.setComuna("Santiago");
        cliente.setRegion("Metropolitana");

        // Preparamos una mascota de ejemplo asociada al cliente anterior
        mascota = new Mascota();
        mascota.setId(1);
        mascota.setNombre("Firulais");
        mascota.setEspecie("Perro");
        mascota.setRaza("Labrador");
        mascota.setEdad(3);
        mascota.setSexo("Macho");
        mascota.setColor("Café");
        mascota.setPeso(20.5);
        mascota.setNumeroChip("CHIP123");
        mascota.setCliente(cliente);
    }

    // ===================== CLIENTE =====================

    /**
     * GET /clientes
     * Caso: el servicio retorna una lista con clientes.
     * Esperado: 200 OK y el cuerpo con la lista serializada en JSON.
     */
    @Test
    void listarClientes_cuandoHayClientes_deberiaRetornar200() throws Exception {
        when(registroService.listarClientes()).thenReturn(Arrays.asList(cliente));

        mockMvc.perform(get("/api/v1/registros/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));

        // Verificamos que el controlador efectivamente delega en el servicio
        verify(registroService, times(1)).listarClientes();
    }

    /**
     * GET /clientes
     * Caso: el servicio retorna una lista vacía.
     * Esperado: 204 No Content, según la lógica del controlador.
     */
    @Test
    void listarClientes_cuandoNoHayClientes_deberiaRetornar204() throws Exception {
        when(registroService.listarClientes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/registros/clientes"))
                .andExpect(status().isNoContent());
    }

    /**
     * GET /clientes/{id}
     * Caso: el cliente existe.
     * Esperado: 200 OK con los datos del cliente.
     */
    @Test
    void buscarClientePorId_cuandoExiste_deberiaRetornar200() throws Exception {
        when(registroService.buscarClientePorId(1)).thenReturn(cliente);

        mockMvc.perform(get("/api/v1/registros/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("11111111-1"));
    }

    /**
     * GET /clientes/{id}
     * Caso: el servicio lanza una excepción porque el cliente no existe.
     * Esperado: 404 Not Found, ya que el controlador captura la excepción y responde así.
     */
    @Test
    void buscarClientePorId_cuandoNoExiste_deberiaRetornar404() throws Exception {
        when(registroService.buscarClientePorId(99))
                .thenThrow(new RuntimeException("Cliente no encontrado con id: 99"));

        mockMvc.perform(get("/api/v1/registros/clientes/99"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /clientes/rut/{rut}
     * Caso: el cliente existe por RUT.
     * Esperado: 200 OK con los datos del cliente.
     */
    @Test
    void buscarClientePorRut_cuandoExiste_deberiaRetornar200() throws Exception {
        when(registroService.buscarClientePorRut("11111111-1")).thenReturn(cliente);

        mockMvc.perform(get("/api/v1/registros/clientes/rut/11111111-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    /**
     * GET /clientes/rut/{rut}
     * Caso: no existe un cliente con ese RUT.
     * Esperado: 404 Not Found.
     */
    @Test
    void buscarClientePorRut_cuandoNoExiste_deberiaRetornar404() throws Exception {
        when(registroService.buscarClientePorRut("99999999-9"))
                .thenThrow(new RuntimeException("Cliente no encontrado con rut: 99999999-9"));

        mockMvc.perform(get("/api/v1/registros/clientes/rut/99999999-9"))
                .andExpect(status().isNotFound());
    }

    /**
     * POST /clientes
     * Caso: creación exitosa de un cliente.
     * Esperado: 200 OK con el cliente creado en el cuerpo de la respuesta.
     * (El controlador no maneja excepciones aquí, siempre responde 200 si el servicio no falla).
     */
    @Test
    void guardarCliente_deberiaRetornar200ConClienteCreado() throws Exception {
        when(registroService.guardarCliente(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/api/v1/registros/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente))) // serializamos el cliente a JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"));

        verify(registroService, times(1)).guardarCliente(any(Cliente.class));
    }

    /**
     * PUT /clientes/{id}
     * Caso: actualización exitosa de un cliente existente.
     * Esperado: 200 OK con el cliente actualizado.
     */
    @Test
    void actualizarCliente_cuandoExiste_deberiaRetornar200() throws Exception {
        when(registroService.actualizarCliente(eq(1), any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(put("/api/v1/registros/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    /**
     * PUT /clientes/{id}
     * Caso: se intenta actualizar un cliente que no existe.
     * Esperado: 404 Not Found.
     */
    @Test
    void actualizarCliente_cuandoNoExiste_deberiaRetornar404() throws Exception {
        when(registroService.actualizarCliente(eq(99), any(Cliente.class)))
                .thenThrow(new RuntimeException("Cliente no encontrado con id: 99"));

        mockMvc.perform(put("/api/v1/registros/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isNotFound());
    }

    /**
     * DELETE /clientes/{id}
     * Caso: el cliente existe y se elimina correctamente.
     * Esperado: 204 No Content.
     */
    @Test
    void eliminarCliente_cuandoExiste_deberiaRetornar204() throws Exception {
        // No hace falta "when(...)" porque el método del servicio retorna void;
        // por defecto Mockito no hace nada (no lanza excepción), simulando éxito.
        mockMvc.perform(delete("/api/v1/registros/clientes/1"))
                .andExpect(status().isNoContent());

        verify(registroService, times(1)).eliminarCliente(1);
    }

    /**
     * DELETE /clientes/{id}
     * Caso: se intenta eliminar un cliente que no existe.
     * Esperado: 404 Not Found.
     */
    @Test
    void eliminarCliente_cuandoNoExiste_deberiaRetornar404() throws Exception {
        // Para métodos void que deben lanzar excepción, se usa doThrow().when(...)
        Mockito.doThrow(new RuntimeException("Cliente no encontrado con id: 99"))
                .when(registroService).eliminarCliente(99);

        mockMvc.perform(delete("/api/v1/registros/clientes/99"))
                .andExpect(status().isNotFound());
    }

    // ===================== MASCOTA =====================

    /**
     * GET /mascotas
     * Caso: existen mascotas registradas.
     * Esperado: 200 OK con la lista de mascotas.
     */
    @Test
    void listarMascotas_cuandoHayMascotas_deberiaRetornar200() throws Exception {
        when(registroService.listarMascotas()).thenReturn(Arrays.asList(mascota));

        mockMvc.perform(get("/api/v1/registros/mascotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Firulais"));
    }

    /**
     * GET /mascotas
     * Caso: no hay mascotas registradas.
     * Esperado: 204 No Content.
     */
    @Test
    void listarMascotas_cuandoNoHayMascotas_deberiaRetornar204() throws Exception {
        when(registroService.listarMascotas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/registros/mascotas"))
                .andExpect(status().isNoContent());
    }

    /**
     * GET /mascotas/{id}
     * Caso: la mascota existe.
     * Esperado: 200 OK con los datos de la mascota.
     */
    @Test
    void buscarMascotaPorId_cuandoExiste_deberiaRetornar200() throws Exception {
        when(registroService.buscarMascotaPorId(1)).thenReturn(mascota);

        mockMvc.perform(get("/api/v1/registros/mascotas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroChip").value("CHIP123"));
    }

    /**
     * GET /mascotas/{id}
     * Caso: la mascota no existe.
     * Esperado: 404 Not Found.
     */
    @Test
    void buscarMascotaPorId_cuandoNoExiste_deberiaRetornar404() throws Exception {
        when(registroService.buscarMascotaPorId(99))
                .thenThrow(new RuntimeException("Mascota no encontrada con id: 99"));

        mockMvc.perform(get("/api/v1/registros/mascotas/99"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /mascotas/cliente/{clienteId}
     * Caso: el cliente tiene mascotas asociadas.
     * Esperado: 200 OK con la lista de mascotas.
     */
    @Test
    void buscarMascotasPorCliente_cuandoHayResultados_deberiaRetornar200() throws Exception {
        when(registroService.buscarMascotasPorCliente(1)).thenReturn(Arrays.asList(mascota));

        mockMvc.perform(get("/api/v1/registros/mascotas/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Firulais"));
    }

    /**
     * GET /mascotas/cliente/{clienteId}
     * Caso: el cliente no tiene mascotas asociadas.
     * Esperado: 204 No Content.
     */
    @Test
    void buscarMascotasPorCliente_cuandoNoHayResultados_deberiaRetornar204() throws Exception {
        when(registroService.buscarMascotasPorCliente(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/registros/mascotas/cliente/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * GET /mascotas/buscar?nombre=...
     * Caso: hay mascotas que coinciden con el nombre buscado.
     * Esperado: 200 OK con la lista de coincidencias.
     */
    @Test
    void buscarMascotasPorNombre_cuandoHayResultados_deberiaRetornar200() throws Exception {
        when(registroService.buscarMascotasPorNombre("Firu")).thenReturn(Arrays.asList(mascota));

        mockMvc.perform(get("/api/v1/registros/mascotas/buscar").param("nombre", "Firu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Firulais"));
    }

    /**
     * GET /mascotas/buscar?nombre=...
     * Caso: no hay coincidencias para el nombre buscado.
     * Esperado: 204 No Content.
     */
    @Test
    void buscarMascotasPorNombre_cuandoNoHayResultados_deberiaRetornar204() throws Exception {
        when(registroService.buscarMascotasPorNombre("NoExiste")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/registros/mascotas/buscar").param("nombre", "NoExiste"))
                .andExpect(status().isNoContent());
    }

    /**
     * GET /mascotas/chip?numeroChip=...
     * Caso: existe una mascota con ese número de chip.
     * Esperado: 200 OK con los datos de la mascota.
     */
    @Test
    void buscarMascotaPorNumeroChip_cuandoExiste_deberiaRetornar200() throws Exception {
        when(registroService.buscarMascotaPorNumeroChip("CHIP123")).thenReturn(mascota);

        mockMvc.perform(get("/api/v1/registros/mascotas/chip").param("numeroChip", "CHIP123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroChip").value("CHIP123"));
    }

    /**
     * GET /mascotas/chip?numeroChip=...
     * Caso: no existe ninguna mascota con ese número de chip.
     * Esperado: 404 Not Found.
     */
    @Test
    void buscarMascotaPorNumeroChip_cuandoNoExiste_deberiaRetornar404() throws Exception {
        when(registroService.buscarMascotaPorNumeroChip("NOEXISTE"))
                .thenThrow(new RuntimeException("Mascota no encontrada con número de chip: NOEXISTE"));

        mockMvc.perform(get("/api/v1/registros/mascotas/chip").param("numeroChip", "NOEXISTE"))
                .andExpect(status().isNotFound());
    }

    /**
     * POST /mascotas
     * Caso: creación exitosa de una mascota.
     * Esperado: 200 OK con la mascota creada.
     * Nota: aunque la documentación Swagger indica 201, el código real del
     * controlador responde 200 OK cuando el guardado es exitoso.
     */
    @Test
    void guardarMascota_cuandoEsExitoso_deberiaRetornar200() throws Exception {
        when(registroService.guardarMascota(any(Mascota.class))).thenReturn(mascota);

        mockMvc.perform(post("/api/v1/registros/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mascota)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Firulais"));
    }

    /**
     * POST /mascotas
     * Caso: el servicio falla (por ejemplo, el cliente asociado no existe).
     * Esperado: 400 Bad Request, según la lógica del controlador.
     */
    @Test
    void guardarMascota_cuandoFalla_deberiaRetornar400() throws Exception {
        when(registroService.guardarMascota(any(Mascota.class)))
                .thenThrow(new RuntimeException("Cliente no encontrado con id: 99"));

        mockMvc.perform(post("/api/v1/registros/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mascota)))
                .andExpect(status().isBadRequest());
    }

    /**
     * PUT /mascotas/{id}
     * Caso: actualización exitosa de una mascota existente.
     * Esperado: 200 OK con la mascota actualizada.
     */
    @Test
    void actualizarMascota_cuandoExiste_deberiaRetornar200() throws Exception {
        when(registroService.actualizarMascota(eq(1), any(Mascota.class))).thenReturn(mascota);

        mockMvc.perform(put("/api/v1/registros/mascotas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mascota)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Firulais"));
    }

    /**
     * PUT /mascotas/{id}
     * Caso: se intenta actualizar una mascota que no existe.
     * Esperado: 404 Not Found.
     */
    @Test
    void actualizarMascota_cuandoNoExiste_deberiaRetornar404() throws Exception {
        when(registroService.actualizarMascota(eq(99), any(Mascota.class)))
                .thenThrow(new RuntimeException("Mascota no encontrada con id: 99"));

        mockMvc.perform(put("/api/v1/registros/mascotas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mascota)))
                .andExpect(status().isNotFound());
    }

    /**
     * DELETE /mascotas/{id}
     * Caso: la mascota existe y se elimina correctamente.
     * Esperado: 204 No Content.
     */
    @Test
    void eliminarMascota_cuandoExiste_deberiaRetornar204() throws Exception {
        mockMvc.perform(delete("/api/v1/registros/mascotas/1"))
                .andExpect(status().isNoContent());

        verify(registroService, times(1)).eliminarMascota(1);
    }

    /**
     * DELETE /mascotas/{id}
     * Caso: se intenta eliminar una mascota que no existe.
     * Esperado: 404 Not Found.
     */
    @Test
    void eliminarMascota_cuandoNoExiste_deberiaRetornar404() throws Exception {
        Mockito.doThrow(new RuntimeException("Mascota no encontrada con id: 99"))
                .when(registroService).eliminarMascota(99);

        mockMvc.perform(delete("/api/v1/registros/mascotas/99"))
                .andExpect(status().isNotFound());
    }

    // No se incluyen pruebas para el endpoint "/dto/mascota/{idMascota}" (RegistroDTO),
    // tal como fue solicitado.
}