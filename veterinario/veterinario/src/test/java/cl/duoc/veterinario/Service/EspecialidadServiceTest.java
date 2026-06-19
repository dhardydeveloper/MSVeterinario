package cl.duoc.veterinario.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.veterinario.model.Especialidad;
import cl.duoc.veterinario.repository.EspecialidadRepository;
import cl.duoc.veterinario.service.EspecialidadService;

/**
 * Pruebas unitarias para {@link EspecialidadService}.
 *
 * Se utiliza Mockito para simular el comportamiento de EspecialidadRepository,
 * de manera que las pruebas no dependan de una base de datos real: solo se
 * testea la lógica de negocio que vive dentro del service.
 */
@ExtendWith(MockitoExtension.class) // habilita la integración de Mockito con JUnit 5
public class EspecialidadServiceTest {

    @Mock
    private EspecialidadRepository especialidadRepository; // repositorio simulado (sin BD real)

    @InjectMocks
    private EspecialidadService especialidadService; // instancia real del service, con el mock inyectado automáticamente

    // Objeto de ejemplo reutilizado en varios tests
    private Especialidad especialidadEjemplo;

    /**
     * Se ejecuta antes de CADA test (@BeforeEach).
     * Aquí preparamos datos "limpios" de ejemplo para que cada prueba
     * empiece desde el mismo estado, sin que un test afecte a otro.
     */
    @BeforeEach
    void setUp() {

        especialidadEjemplo = new Especialidad();
        especialidadEjemplo.setId(1);
        especialidadEjemplo.setNombre("Cirugia");
    }

    // ===================== LISTAR =====================

    /**
     * Verifica que listar() devuelva todas las especialidades
     * que entrega el repositorio (findAll), sin alterarlas.
     */
    @Test
    void testListar() {

        // ARRANGE: creamos una segunda especialidad para simular una lista con 2 elementos
        Especialidad especialidad2 = new Especialidad();
        especialidad2.setId(2);
        especialidad2.setNombre("Dermatologia");

        // Simulamos que el repositorio devuelve 2 especialidades
        when(especialidadRepository.findAll()).thenReturn(Arrays.asList(especialidadEjemplo, especialidad2));

        // ACT: llamamos al método real del service
        List<Especialidad> resultado = especialidadService.listar();

        // ASSERT: la lista debe tener 2 elementos y mantener el orden/datos esperados
        assertEquals(2, resultado.size());
        assertEquals("Cirugia", resultado.get(0).getNombre());
        assertEquals("Dermatologia", resultado.get(1).getNombre());

        // Verificamos que efectivamente se llamó al repositorio 1 sola vez
        verify(especialidadRepository, times(1)).findAll();
    }

    // ===================== BUSCAR POR ID =====================

    @Test
    void buscarPorId_encontrado() {

        // ARRANGE: el repo "encuentra" la especialidad de ejemplo con id=1
        when(especialidadRepository.findById(1)).thenReturn(Optional.of(especialidadEjemplo));

        // ACT
        Especialidad resultado = especialidadService.buscarPorId(1);

        // ASSERT
        assertEquals(1, resultado.getId());
        assertEquals("Cirugia", resultado.getNombre());

        verify(especialidadRepository, times(1)).findById(1);
    }

    /**
     * Caso de error: el repositorio NO encuentra el id solicitado (Optional vacío),
     * por lo que el service debe lanzar una RuntimeException con un mensaje específico.
     */
    @Test
    void buscarPorId_noEncontrado() {

        // ARRANGE: el repo devuelve un Optional vacío para el id 99
        when(especialidadRepository.findById(99)).thenReturn(Optional.empty());

        // ACT: ejecutamos el método y capturamos la excepción lanzada
        RuntimeException error = assertThrows(RuntimeException.class, () -> {
            especialidadService.buscarPorId(99);
        });

        // ASSERT: el mensaje de la excepción debe coincidir con el del service
        assertEquals("Especialidad no encontrada con id: 99", error.getMessage());
    }

    // ===================== GUARDAR =====================

    /**
     * Caso feliz: se crea una especialidad nueva. El service simplemente
     * delega en especialidadRepository.save(), sin validaciones adicionales.
     */
    @Test
    void testGuardar_Exito() {

        // ARRANGE: nueva especialidad a guardar (sin id, como llegaría desde el JSON)
        Especialidad nuevaEspecialidad = new Especialidad();
        nuevaEspecialidad.setNombre("Oftalmologia");

        // Simulamos que save() devuelve la especialidad ya "guardada" (con id asignado)
        Especialidad especialidadGuardada = new Especialidad();
        especialidadGuardada.setId(3);
        especialidadGuardada.setNombre("Oftalmologia");

        when(especialidadRepository.save(nuevaEspecialidad)).thenReturn(especialidadGuardada);

        // ACT
        Especialidad resultado = especialidadService.guardar(nuevaEspecialidad);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(3, resultado.getId());
        assertEquals("Oftalmologia", resultado.getNombre());

        verify(especialidadRepository, times(1)).save(nuevaEspecialidad);
    }

    // ===================== ACTUALIZAR =====================

    /**
     * Caso feliz: se actualiza el nombre de una especialidad existente.
     * El service primero busca la especialidad (buscarPorId), le cambia
     * el nombre, y guarda los cambios.
     */
    @Test
    void testActualizar_Exito() {

        // ARRANGE: datos nuevos a aplicar
        Especialidad datosActualizados = new Especialidad();
        datosActualizados.setNombre("Cirugia General");

        // La especialidad con id=1 existe (es nuestra especialidadEjemplo)
        when(especialidadRepository.findById(1)).thenReturn(Optional.of(especialidadEjemplo));
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(especialidadEjemplo);

        // ACT
        Especialidad resultado = especialidadService.actualizar(1, datosActualizados);

        // ASSERT: el nombre debe haberse actualizado sobre la especialidad existente
        assertEquals("Cirugia General", resultado.getNombre());
        assertEquals(1, resultado.getId()); // el id no cambia

        verify(especialidadRepository, times(1)).findById(1);
        verify(especialidadRepository, times(1)).save(especialidadEjemplo);
    }

    /**
     * Caso de error: se intenta actualizar una especialidad que no existe (id no encontrado).
     * El service debe lanzar RuntimeException y no debe llamar a save().
     */
    @Test
    void testActualizar_NoEncontrada() {

        // ARRANGE: no existe ninguna especialidad con id=99
        when(especialidadRepository.findById(99)).thenReturn(Optional.empty());

        Especialidad datosActualizados = new Especialidad();
        datosActualizados.setNombre("X");

        // ACT
        RuntimeException error = assertThrows(RuntimeException.class, () -> {
            especialidadService.actualizar(99, datosActualizados);
        });

        // ASSERT
        assertEquals("Especialidad no encontrada con id: 99", error.getMessage());

        // Si no existe la especialidad, jamás se debe llamar a save()
        verify(especialidadRepository, never()).save(any(Especialidad.class));
    }

    // ===================== ELIMINAR =====================

    /**
     * Caso feliz: la especialidad existe, por lo tanto se llama a deleteById().
     */
    @Test
    void testEliminar_Exito() {

        // ARRANGE: la especialidad con id=1 existe
        when(especialidadRepository.existsById(1)).thenReturn(true);

        // ACT
        especialidadService.eliminar(1);

        // ASSERT: se verificó la existencia y luego se eliminó
        verify(especialidadRepository, times(1)).existsById(1);
        verify(especialidadRepository, times(1)).deleteById(1);
    }

    /**
     * Caso de error: la especialidad NO existe, por lo tanto el service
     * debe lanzar una excepción y NUNCA llamar a deleteById().
     */
    @Test
void testEliminar_NoEncontrada() {

    // ARRANGE: la especialidad con id=99 no existe
    when(especialidadRepository.existsById(99)).thenReturn(false);

    // ACT
    RuntimeException error = assertThrows(RuntimeException.class, () -> {
        especialidadService.eliminar(99);   // ← aquí, "especialidadService" (minúscula), no "Especialidad"
    });

    // ASSERT
    assertEquals("Especialidad no encontrada con id: 99", error.getMessage());

    verify(especialidadRepository, never()).deleteById(any());
}
}