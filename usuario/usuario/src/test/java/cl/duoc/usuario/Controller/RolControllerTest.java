package cl.duoc.usuario.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import cl.duoc.usuario.controller.RolController;
import cl.duoc.usuario.model.Rol;
 import cl.duoc.usuario.service.RolService;

@WebMvcTest(RolController.class)
public class RolControllerTest {

    /**
     * Permite ejecutar solicitudes HTTP simuladas.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock del servicio utilizado por el controlador.
     */
    @MockitoBean
    private RolService rolService;

    private Rol rol;

    /**
     * Datos de prueba utilizados en los distintos casos.
     */
    @BeforeEach
    void setUp() {

        rol = new Rol();
        rol.setIdRol(1);
        rol.setNombreRol("ADMIN");

    }

    /**
     * Verifica que se retorne HTTP 200 cuando existen roles.
     */
    @Test
    void deberiaListarRol() throws Exception {

        when(rolService.listar())
                .thenReturn(List.of(rol));

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que se retorne HTTP 204 cuando no existen roles.
     */
    @Test
    void deberiaRetornarNoContentCuandoListaVacia() throws Exception {

        when(rolService.listar())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isNoContent());
    }


    /**
     * Verifica búsqueda exitosa por ID.
     */
    @Test
    void deberiaBuscarRolPorId() throws Exception {

        when(rolService.buscarPorId(1))
                .thenReturn(rol);

        mockMvc.perform(get("/api/v1/roles/1"))
                .andExpect(status().isOk());
    }

    /**
     * Verifica respuesta 404 cuando el rol no existe.
     */
    @Test
    void deberiaRetornar404CuandoRolNoExiste() throws Exception {

        when(rolService.buscarPorId(1))
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v1/roles/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaGuardarRol() throws Exception {

        when(rolService.guardar(any(Rol.class)))
                .thenReturn(rol);

        String body = """
        {
            "nombreRol": "ADMIN"
        }
        """;
        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    /**
     * Verifica respuesta 400 cuando ocurre un error al guardar.
     */
    @Test
    void deberiaRetornar400AlGuardar() throws Exception {

        when(rolService.guardar(any(Rol.class)))
                .thenThrow(new RuntimeException());

        String body = """
        {
            "nombre":"ADMIN"
        }
        """;

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica actualización exitosa.
     */
    @Test
    void deberiaActualizarRol() throws Exception {

        when(rolService.actualizar(any(Integer.class), any(Rol.class)))
                .thenReturn(rol);

        String body = """
        {
            "nombre":"ADMIN Actualizado"
        }
        """;

        mockMvc.perform(put("/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    /**
     * Verifica respuesta 400 cuando falla la actualización.
     */
    @Test
    void deberiaRetornar400AlActualizar() throws Exception {

        when(rolService.actualizar(any(Integer.class), any(Rol.class)))
                .thenThrow(new RuntimeException());

        String body = """
        {
            "nombre":"ADMIN"
        }
        """;

        mockMvc.perform(put("/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica eliminación exitosa.
     */
    @Test
    void deberiaEliminarRol() throws Exception {

        doNothing().when(rolService).eliminar(1);

        mockMvc.perform(delete("/api/v1/roles/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica respuesta 404 cuando falla la eliminación.
     */
    @Test
    void deberiaRetornar404AlEliminar() throws Exception {

        doThrow(new RuntimeException())
                .when(rolService)
                .eliminar(1);

        mockMvc.perform(delete("/api/v1/roles/1"))
                .andExpect(status().isNotFound());
    }


    

}
