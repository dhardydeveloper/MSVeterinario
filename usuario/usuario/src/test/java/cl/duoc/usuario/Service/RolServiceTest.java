package cl.duoc.usuario.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import cl.duoc.usuario.model.Rol;
import cl.duoc.usuario.repository.RolRepository;
import cl.duoc.usuario.service.RolService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para RolService.
 *
 * Se utiliza Mockito para simular el comportamiento del repositorio
 * y validar la lógica de negocio sin necesidad de conectarse a una
 * base de datos real.
 */
@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    /**
     * Mock del repositorio de roles.
     */
    @Mock
    private RolRepository rolRepository;

    /**
     * Inyecta los mocks dentro del servicio a probar.
     */
    @InjectMocks
    private RolService rolService;

    private Rol rol;

    /**
     * Configuración inicial ejecutada antes de cada prueba.
     */
    @BeforeEach
    void setUp() {

        rol = new Rol();
        rol.setIdRol(1);
        rol.setNombreRol("ADMIN");
        rol.setDescripcion("Administrador del sistema");
    }

    /**
     * Verifica que el servicio retorne correctamente
     * la lista de roles.
     */
    @Test
    void deberiaListarRoles() {

        // Simula una lista con un rol
        when(rolRepository.findAll())
                .thenReturn(Arrays.asList(rol));

        // Ejecutar método
        List<Rol> roles = rolService.listar();

        // Verificar resultado
        assertEquals(1, roles.size());

        // Verificar llamada al repositorio
        verify(rolRepository).findAll();
    }

    /**
     * Verifica la búsqueda exitosa de un rol por ID.
     */
    @Test
    void deberiaBuscarRolPorId() {

        // Simula que existe un rol con ID 1
        when(rolRepository.findById(1))
                .thenReturn(Optional.of(rol));

        // Ejecutar método
        Rol resultado = rolService.buscarPorId(1);

        // Verificar resultado
        assertNotNull(resultado);
        assertEquals("ADMIN", resultado.getNombreRol());
    }

    /**
     * Verifica que se lance una excepción
     * cuando el rol no existe.
     */
    @Test
    void deberiaLanzarExcepcionSiRolNoExiste() {

        // Simula que el rol no existe
        when(rolRepository.findById(1))
                .thenReturn(Optional.empty());

        // Verificar excepción
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> rolService.buscarPorId(1));

        assertEquals(
                "Rol no encontrado con id: 1",
                ex.getMessage());
    }

    /**
     * Verifica que un rol se guarde correctamente.
     */
    @Test
    void deberiaGuardarRol() {

        // Simula el guardado del rol
        when(rolRepository.save(any(Rol.class)))
                .thenReturn(rol);

        // Ejecutar método
        Rol resultado = rolService.guardar(rol);

        // Verificar resultado
        assertNotNull(resultado);
        assertEquals("ADMIN", resultado.getNombreRol());

        // Verificar guardado
        verify(rolRepository).save(rol);
    }

    /**
     * Verifica la actualización correcta de un rol.
     */
    @Test
    void deberiaActualizarRol() {

        // Crear datos actualizados
        Rol rolActualizado = new Rol();
        rolActualizado.setNombreRol("USUARIO");
        rolActualizado.setDescripcion("Usuario estándar");

        // Simular rol existente
        when(rolRepository.findById(1))
                .thenReturn(Optional.of(rol));

        // Retornar el objeto actualizado
        when(rolRepository.save(any(Rol.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar método
        Rol resultado = rolService.actualizar(1, rolActualizado);

        // Verificar actualización
        assertEquals("USUARIO", resultado.getNombreRol());
        assertEquals("Usuario estándar", resultado.getDescripcion());
    }

    /**
     * Verifica la eliminación exitosa de un rol.
     */
    @Test
    void deberiaEliminarRol() {

        // Simula que el rol existe
        when(rolRepository.existsById(1))
                .thenReturn(true);

        // Ejecutar eliminación
        rolService.eliminar(1);

        // Verificar eliminación
        verify(rolRepository).deleteById(1);
    }

    /**
     * Verifica que se lance una excepción
     * cuando se intenta eliminar un rol inexistente.
     */
    @Test
    void deberiaLanzarExcepcionAlEliminarRolInexistente() {

        // Simula que el rol no existe
        when(rolRepository.existsById(1))
                .thenReturn(false);

        // Verificar excepción
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> rolService.eliminar(1));

        assertEquals(
                "Rol no encontrado con id: 1",
                ex.getMessage());
    }

    /**
     * Verifica que se lance una excepción
     * cuando el nombre del rol está vacío.
     */
    @Test
    void deberiaLanzarExcepcionCuandoNombreEsVacio() {

        rol.setNombreRol("");

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> rolService.guardar(rol));

        assertEquals(
                "El nombre del rol es obligatorio",
                ex.getMessage());
    }

    /**
     * Verifica que se lance una excepción
     * cuando la descripción está vacía.
     */
    @Test
    void deberiaLanzarExcepcionCuandoDescripcionEsVacia() {

        rol.setDescripcion("");

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> rolService.guardar(rol));

        assertEquals(
                "La descripción del rol es obligatoria",
                ex.getMessage());
    }
}