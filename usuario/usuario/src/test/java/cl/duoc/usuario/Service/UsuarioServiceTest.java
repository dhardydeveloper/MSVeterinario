package cl.duoc.usuario.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import cl.duoc.usuario.dto.UsuarioDTO;
import cl.duoc.usuario.model.Rol;
import cl.duoc.usuario.model.Usuario;
import cl.duoc.usuario.repository.RolRepository;
import cl.duoc.usuario.repository.UsuarioRepository;
import cl.duoc.usuario.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Se utiliza Mockito para simular el comportamiento de los repositorios
 * y validar la lógica de negocio sin necesidad de conectarse a una base
 * de datos real.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    /** 
     * Mock del repositorio de usuarios.
     * Simula las operaciones de acceso a datos.
     */
    @Mock
    private UsuarioRepository usuarioRepository;

    /**
     * Mock del repositorio de roles.
     * Permite simular la búsqueda y validación de roles.
     */
    @Mock
    private RolRepository rolRepository;

    /**
     * Inyecta automáticamente los mocks dentro del servicio a probar.
     */
    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Rol rol;

    /**
     * Configuración inicial ejecutada antes de cada prueba.
     * Crea un rol y un usuario de ejemplo que serán reutilizados
     * en los distintos escenarios de prueba.
     */
    @BeforeEach
    void setUp() {

        // Crear rol de prueba
        rol = new Rol();
        rol.setIdRol(1);
        rol.setNombreRol("ADMIN");

        // Crear usuario de prueba
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
     * Verifica que el servicio retorne correctamente
     * la lista de usuarios.
     */
    @Test
    void deberiaListarUsuarios() {

        // Simula una lista con un usuario
        when(usuarioRepository.findAll())
                .thenReturn(Arrays.asList(usuario));

        // Ejecutar método a probar
        List<Usuario> usuarios = usuarioService.listar();

        // Verificar resultados
        assertEquals(1, usuarios.size());

        // Verificar que se llamó al repositorio
        verify(usuarioRepository).findAll();
    }

    /**
     * Verifica la búsqueda exitosa de un usuario por ID.
     */
    @Test
    void deberiaBuscarUsuarioPorId() {

        // Simula que existe un usuario con ID 1
        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        // Ejecutar método a probar
        Usuario resultado = usuarioService.buscarPorId(1);

        // Verificar resultado
        assertNotNull(resultado);
        assertEquals("David", resultado.getNombre());
    }

    /**
     * Verifica que se lance una excepción
     * cuando el usuario no existe.
     */
    @Test
    void deberiaLanzarExcepcionSiUsuarioNoExiste() {

        // Simula que no existe un usuario con ID 1
        when(usuarioRepository.findById(1))
                .thenReturn(Optional.empty());

        // Verificar excepción
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> usuarioService.buscarPorId(1));

        assertEquals(
                "Usuario no encontrado con id: 1",
                ex.getMessage());
    }

    /**
     * Verifica la búsqueda exitosa de un usuario por correo.
     */
    @Test
    void deberiaBuscarUsuarioPorCorreo() {

        // Simula búsqueda por correo
        when(usuarioRepository.findByCorreo("david@gmail.com"))
                .thenReturn(Optional.of(usuario));

        // Ejecutar método
        Usuario resultado =
                usuarioService.buscarPorCorreo("david@gmail.com");

        // Verificar resultado
        assertNotNull(resultado);
        assertEquals("David", resultado.getNombre());
    }

    /**
     * Verifica que un usuario se guarde correctamente
     * cuando el rol existe.
     */
    @Test
    void deberiaGuardarUsuario() {

        // Simula que el rol existe
        when(rolRepository.findById(1))
                .thenReturn(Optional.of(rol));

        // Simula el guardado del usuario
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        // Ejecutar método
        Usuario resultado = usuarioService.guardar(usuario);

        // Verificar resultado
        assertNotNull(resultado);
        assertEquals("David", resultado.getNombre());

        // Verificar interacciones con repositorios
        verify(rolRepository).findById(1);
        verify(usuarioRepository).save(usuario);
    }

    /**
     * Verifica la actualización correcta de un usuario.
     */
    @Test
    void deberiaActualizarUsuario() {

        // Crear datos actualizados
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setNombre("Juan");
        usuarioActualizado.setApellido("Gonzalez");
        usuarioActualizado.setCorreo("juan@gmail.com");
        usuarioActualizado.setPassword("456789");
        usuarioActualizado.setEstado(true);
        usuarioActualizado.setRol(rol);

        // Simula usuario existente
        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        // Simula rol válido
        when(rolRepository.findById(1))
                .thenReturn(Optional.of(rol));

        // Retorna el objeto guardado
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar método
        Usuario resultado =
                usuarioService.actualizar(1, usuarioActualizado);

        // Verificar actualización
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Gonzalez", resultado.getApellido());
        assertEquals("juan@gmail.com", resultado.getCorreo());
    }

    /**
     * Verifica la eliminación exitosa de un usuario.
     */
    @Test
    void deberiaEliminarUsuario() {

        // Simula que el usuario existe
        when(usuarioRepository.existsById(1))
                .thenReturn(true);

        // Ejecutar eliminación
        usuarioService.eliminar(1);

        // Verificar eliminación
        verify(usuarioRepository).deleteById(1);
    }

    /**
     * Verifica que se lance una excepción
     * cuando se intenta eliminar un usuario inexistente.
     */
    @Test
    void deberiaLanzarExcepcionAlEliminarUsuarioInexistente() {

        // Simula que el usuario no existe
        when(usuarioRepository.existsById(1))
                .thenReturn(false);

        // Verificar excepción
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> usuarioService.eliminar(1));

        assertEquals(
                "Usuario no encontrado con id: 1",
                ex.getMessage());
    }

    /**
     * Verifica la obtención de un UsuarioDTO
     * a partir del ID de un usuario.
     */
    @Test
    void deberiaObtenerUsuarioDTO() {

        // Simula usuario existente
        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        // Ejecutar método
        UsuarioDTO dto =
                usuarioService.obtenerUsuarioDTO(1);

        // Verificar datos mapeados
        assertNotNull(dto);
        assertEquals(1, dto.getIdUsuario());
        assertEquals("David", dto.getNombre());
        assertEquals("Perez", dto.getApellido());
        assertEquals("ADMIN", dto.getNombreRol());
    }

    /**
     * Verifica la conversión manual
     * de una entidad Usuario a UsuarioDTO.
     */
    @Test
    void deberiaConvertirUsuarioADTO() {

        // Ejecutar conversión
        UsuarioDTO dto =
                usuarioService.convertirADTO(usuario);

        // Verificar datos convertidos
        assertEquals(usuario.getIdUsuario(), dto.getIdUsuario());
        assertEquals(usuario.getNombre(), dto.getNombre());
        assertEquals(usuario.getApellido(), dto.getApellido());
        assertEquals(usuario.getCorreo(), dto.getCorreo());
        assertEquals("ADMIN", dto.getNombreRol());
    }
}