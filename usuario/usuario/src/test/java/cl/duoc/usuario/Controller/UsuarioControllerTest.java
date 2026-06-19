package cl.duoc.usuario.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import cl.duoc.usuario.controller.UsuarioController;
import cl.duoc.usuario.model.Rol;
import cl.duoc.usuario.model.Usuario;
import cl.duoc.usuario.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pruebas unitarias para UsuarioController.
 * Se utiliza MockMvc para simular peticiones HTTP y Mockito
 * para simular el comportamiento del servicio.
 */
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    /**
     * Permite ejecutar solicitudes HTTP simuladas.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock del servicio utilizado por el controlador.
     */
    @MockitoBean
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Rol rol;

    /**
     * Datos de prueba utilizados en los distintos casos.
     */
    @BeforeEach
    void setUp() {

        rol = new Rol();
        rol.setIdRol(1);
        rol.setNombreRol("ADMIN");

        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("David");
        usuario.setApellido("Perez");
        usuario.setCorreo("david@gmail.com");
        usuario.setPassword("123456");
        usuario.setEstado(true);
        usuario.setRol(rol);


    }

    /**
     * Verifica que se retorne HTTP 200 cuando existen usuarios.
     */
    @Test
    void deberiaListarUsuarios() throws Exception {

        when(usuarioService.listar())
                .thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que se retorne HTTP 204 cuando no existen usuarios.
     */
    @Test
    void deberiaRetornarNoContentCuandoListaVacia() throws Exception {

        when(usuarioService.listar())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica búsqueda exitosa por ID.
     */
    @Test
    void deberiaBuscarUsuarioPorId() throws Exception {

        when(usuarioService.buscarPorId(1))
                .thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk());
    }

    /**
     * Verifica respuesta 404 cuando el usuario no existe.
     */
    @Test
    void deberiaRetornar404CuandoUsuarioNoExiste() throws Exception {

        when(usuarioService.buscarPorId(1))
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifica búsqueda por correo electrónico.
     */
    @Test
    void deberiaBuscarUsuarioPorCorreo() throws Exception {

        when(usuarioService.buscarPorCorreo("david@gmail.com"))
                .thenReturn(usuario);

        mockMvc.perform(
                get("/api/v1/usuarios/correo")
                        .param("correo", "david@gmail.com"))
                .andExpect(status().isOk());
    }

    /**
     * Verifica respuesta 404 cuando el correo no existe.
     */
    @Test
    void deberiaRetornar404AlBuscarPorCorreo() throws Exception {

        when(usuarioService.buscarPorCorreo("david@gmail.com"))
                .thenThrow(new RuntimeException());

        mockMvc.perform(
                get("/api/v1/usuarios/correo")
                        .param("correo", "david@gmail.com"))
                .andExpect(status().isNotFound());
    }

   
    @Test
    void deberiaGuardarUsuario() throws Exception {

        when(usuarioService.guardar(any(Usuario.class)))
                .thenReturn(usuario);

        String body = """
        {
            "nombre":"David",
            "apellido":"Perez",
            "correo":"david@gmail.com",
            "password":"123456"
        }
        """;

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    /**
     * Verifica respuesta 400 cuando ocurre un error al guardar.
     */
    @Test
    void deberiaRetornar400AlGuardar() throws Exception {

        when(usuarioService.guardar(any(Usuario.class)))
                .thenThrow(new RuntimeException());

        String body = """
        {
            "nombre":"David"
        }
        """;

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica actualización exitosa.
     */
    @Test
    void deberiaActualizarUsuario() throws Exception {

        when(usuarioService.actualizar(any(Integer.class), any(Usuario.class)))
                .thenReturn(usuario);

        String body = """
        {
            "nombre":"David Actualizado"
        }
        """;

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    /**
     * Verifica respuesta 400 cuando falla la actualización.
     */
    @Test
    void deberiaRetornar400AlActualizar() throws Exception {

        when(usuarioService.actualizar(any(Integer.class), any(Usuario.class)))
                .thenThrow(new RuntimeException());

        String body = """
        {
            "nombre":"David"
        }
        """;

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica eliminación exitosa.
     */
    @Test
    void deberiaEliminarUsuario() throws Exception {

        doNothing().when(usuarioService).eliminar(1);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica respuesta 404 cuando falla la eliminación.
     */
    @Test
    void deberiaRetornar404AlEliminar() throws Exception {

        doThrow(new RuntimeException())
                .when(usuarioService)
                .eliminar(1);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNotFound());
    }
}