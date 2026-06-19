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
import cl.duoc.veterinario.model.Veterinario;
import cl.duoc.veterinario.repository.EspecialidadRepository;
import cl.duoc.veterinario.repository.VeterinarioRepository;
import cl.duoc.veterinario.service.VeterinarioService;

/**
 * Pruebas unitarias para {@link VeterinarioService}.
 *
 * Se utiliza Mockito para simular el comportamiento de los repositorios
 * (VeterinarioRepository y EspecialidadRepository), de manera que las
 * pruebas no dependan de una base de datos real: solo se testea la
 * lógica de negocio que vive dentro del service.
 */
@ExtendWith(MockitoExtension.class) // habilita la integración de Mockito con JUnit 5
public class VeterinarioServiceTest {

    @Mock
    private VeterinarioRepository veterinarioRepository; // repositorio simulado (sin BD real)

    @Mock
    private EspecialidadRepository especialidadRepository; // repositorio simulado de especialidades

    @InjectMocks
    private VeterinarioService veterinarioService; // instancia real del service, con los mocks inyectados automáticamente

    // Objetos de ejemplo reutilizados en varios tests
    private Veterinario veterinarioEjemplo;
    private Especialidad especialidadEjemplo;

    /**
     * Se ejecuta antes de CADA test (@BeforeEach).
     * Aquí preparamos datos "limpios" de ejemplo para que cada prueba
     * empiece desde el mismo estado, sin que un test afecte a otro.
     */
    @BeforeEach
    void setUp() {

        // Especialidad de ejemplo, asociada al veterinario de ejemplo
        especialidadEjemplo = new Especialidad();
        especialidadEjemplo.setId(1);
        especialidadEjemplo.setNombre("Cirugia");

        // Veterinario de ejemplo, usado como "respuesta simulada" del repositorio
        veterinarioEjemplo = new Veterinario();
        veterinarioEjemplo.setId(1);
        veterinarioEjemplo.setNombre("Juan");
        veterinarioEjemplo.setApellido("perez");
        veterinarioEjemplo.setRut("17621180-6");
        veterinarioEjemplo.setEspecialidad(especialidadEjemplo);
    }

    // ===================== LISTAR =====================

    /**
     * Verifica que listar() devuelva todos los veterinarios
     * que entrega el repositorio (findAll), sin alterarlos.
     */
    @Test
    void testListar() {

        // ARRANGE: creamos un segundo veterinario para simular una lista con 2 elementos
        Veterinario veterinario2 = new Veterinario();
        veterinario2.setId(2);
        veterinario2.setNombre("Maria");
        veterinario2.setApellido("Gonzalez");
        veterinario2.setRut("18222333-4");
        veterinario2.setEspecialidad(especialidadEjemplo);

        // Simulamos que el repositorio devuelve 2 veterinarios
        when(veterinarioRepository.findAll()).thenReturn(Arrays.asList(veterinarioEjemplo, veterinario2));

        // ACT: llamamos al método real del service
        List<Veterinario> resultado = veterinarioService.listar();

        // ASSERT: la lista debe tener 2 elementos y mantener el orden/datos esperados
        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("Maria", resultado.get(1).getNombre());

        // Verificamos que efectivamente se llamó al repositorio 1 sola vez
        verify(veterinarioRepository, times(1)).findAll();
    }

    // ===================== BUSCAR POR ID =====================

    /**
     * Caso feliz: el repositorio encuentra el veterinario con el id solicitado.
     */
    @Test
    void buscarPorId_encontrado() {

        // ARRANGE: el repo "encuentra" el veterinario de ejemplo con id=1
        when(veterinarioRepository.findById(1)).thenReturn(Optional.of(veterinarioEjemplo));

        // ACT: llamamos al método real del service
        Veterinario resultado = veterinarioService.buscarPorId(1);

        // ASSERT: validamos que los datos devueltos sean los esperados
        assertEquals(1, resultado.getId());
        assertEquals("Juan", resultado.getNombre());

        verify(veterinarioRepository, times(1)).findById(1);
    }

    /**
     * Caso de error: el repositorio NO encuentra el id solicitado (Optional vacío),
     * por lo que el service debe lanzar una RuntimeException con un mensaje específico.
     */
    @Test
    void buscarPorId_noEncontrado() {

        // ARRANGE: el repo devuelve un Optional vacío para el id 99
        when(veterinarioRepository.findById(99)).thenReturn(Optional.empty());

        // ACT: ejecutamos el método y capturamos la excepción lanzada
        RuntimeException error = assertThrows(RuntimeException.class, () -> {
            veterinarioService.buscarPorId(99);
        });

        // ASSERT: el mensaje de la excepción debe coincidir con el del service
        assertEquals("Veterinario no encontrado con id: 99", error.getMessage());
    }

    // ===================== BUSCAR POR RUT =====================

    /**
     * Caso feliz: el repositorio encuentra un veterinario por su RUT.
     */
    @Test
    void testBuscarPorRut_Exito() {

        // ARRANGE
        when(veterinarioRepository.findByRut("17621180-6")).thenReturn(Optional.of(veterinarioEjemplo));

        // ACT
        Veterinario resultado = veterinarioService.buscarPorRut("17621180-6");

        // ASSERT
        assertNotNull(resultado);
        assertEquals("17621180-6", resultado.getRut());

        verify(veterinarioRepository, times(1)).findByRut("17621180-6");
    }

    /**
     * Caso de error: no existe ningún veterinario con ese RUT.
     */
    @Test
    void testBuscarPorRut_NoEncontrado() {

        // ARRANGE: el repo no encuentra el RUT solicitado
        when(veterinarioRepository.findByRut("99999999-9")).thenReturn(Optional.empty());

        // ACT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            veterinarioService.buscarPorRut("99999999-9");
        });

        // ASSERT
        assertEquals("Veterinario no encontrado con rut: 99999999-9", exception.getMessage());
    }

    // ===================== GUARDAR =====================

    /**
     * Caso feliz: se crea un veterinario nuevo, indicando solo el id de la especialidad.
     * El service debe:
     *  1) Buscar esa especialidad en EspecialidadRepository.
     *  2) Reemplazar la especialidad "parcial" (solo id) por la especialidad completa encontrada.
     *  3) Guardar el veterinario con VeterinarioRepository.save().
     */
    @Test
    void testGuardar_Exito() {

        // ARRANGE: nuevo veterinario, con la especialidad referenciada solo por id (como llegaría desde el JSON)
        Veterinario nuevoVeterinario = new Veterinario();
        nuevoVeterinario.setNombre("Pedro");
        nuevoVeterinario.setApellido("Soto");
        nuevoVeterinario.setRut("19000000-1");

        Especialidad especialidadRef = new Especialidad();
        especialidadRef.setId(1); // solo viene el id desde el body del request
        nuevoVeterinario.setEspecialidad(especialidadRef);

        // Simulamos que la especialidad con id=1 sí existe
        when(especialidadRepository.findById(1)).thenReturn(Optional.of(especialidadEjemplo));
        // Simulamos que save() devuelve el mismo objeto que se le pasó
        when(veterinarioRepository.save(any(Veterinario.class))).thenReturn(nuevoVeterinario);

        // ACT
        Veterinario resultado = veterinarioService.guardar(nuevoVeterinario);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("Pedro", resultado.getNombre());
        // La especialidad "parcial" (solo id) debe haber sido reemplazada por la especialidad completa
        assertEquals(especialidadEjemplo, nuevoVeterinario.getEspecialidad());

        verify(especialidadRepository, times(1)).findById(1);
        verify(veterinarioRepository, times(1)).save(nuevoVeterinario);
    }

    /**
     * Caso de error: se intenta guardar un veterinario cuya especialidad
     * (por id) no existe en la base de datos. El service debe lanzar una
     * RuntimeException y NO debe llegar a llamar a veterinarioRepository.save().
     */
    @Test
    void testGuardar_EspecialidadNoEncontrada() {

        // ARRANGE
        Veterinario nuevoVeterinario = new Veterinario();
        nuevoVeterinario.setNombre("Pedro");
        nuevoVeterinario.setApellido("Soto");
        nuevoVeterinario.setRut("19000000-1");

        Especialidad especialidadRef = new Especialidad();
        especialidadRef.setId(99); // id de una especialidad que NO existe
        nuevoVeterinario.setEspecialidad(especialidadRef);

        // El repo de especialidades no encuentra el id 99
        when(especialidadRepository.findById(99)).thenReturn(Optional.empty());

        // ACT
        RuntimeException error = assertThrows(RuntimeException.class, () -> {
            veterinarioService.guardar(nuevoVeterinario);
        });

        // ASSERT
        assertEquals("Especialidad no encontrada con id: 99", error.getMessage());

        // No debería intentar guardar el veterinario si la especialidad no es válida
        verify(veterinarioRepository, never()).save(any(Veterinario.class));
    }

    // ===================== ACTUALIZAR =====================

    /**
     * Caso feliz: se actualizan nombre/apellido/rut de un veterinario existente,
     * SIN enviar una nueva especialidad (especialidad = null en el body).
     * El service debe mantener la especialidad original sin tocarla.
     */
    @Test
    void testActualizar_Exito_SinCambiarEspecialidad() {

        // ARRANGE: datos nuevos a aplicar, sin especialidad
        Veterinario datosActualizados = new Veterinario();
        datosActualizados.setRut("17621180-6");
        datosActualizados.setNombre("Juan Actualizado");
        datosActualizados.setApellido("Perez Actualizado");
        datosActualizados.setEspecialidad(null); // no se envía especialidad

        // El veterinario con id=1 existe (es nuestro veterinarioEjemplo)
        when(veterinarioRepository.findById(1)).thenReturn(Optional.of(veterinarioEjemplo));
        when(veterinarioRepository.save(any(Veterinario.class))).thenReturn(veterinarioEjemplo);

        // ACT
        Veterinario resultado = veterinarioService.actualizar(1, datosActualizados);

        // ASSERT: se actualizaron los campos básicos
        assertEquals("Juan Actualizado", resultado.getNombre());
        assertEquals("Perez Actualizado", resultado.getApellido());

        verify(veterinarioRepository, times(1)).findById(1);
        verify(veterinarioRepository, times(1)).save(veterinarioEjemplo);
        // Como no se envió especialidad, NUNCA se debe consultar EspecialidadRepository
        verify(especialidadRepository, never()).findById(any());
    }

    /**
     * Caso feliz: se actualiza un veterinario existente Y se le cambia
     * la especialidad (enviando el id de una especialidad válida).
     */
    @Test
    void testActualizar_Exito_CambiandoEspecialidad() {

        // ARRANGE: nueva especialidad que SÍ existe en la BD
        Especialidad nuevaEspecialidad = new Especialidad();
        nuevaEspecialidad.setId(2);
        nuevaEspecialidad.setNombre("Dermatologia");

        // En el body solo llega el id de la nueva especialidad
        Especialidad especialidadRef = new Especialidad();
        especialidadRef.setId(2);

        Veterinario datosActualizados = new Veterinario();
        datosActualizados.setRut("17621180-6");
        datosActualizados.setNombre("Juan");
        datosActualizados.setApellido("perez");
        datosActualizados.setEspecialidad(especialidadRef);

        when(veterinarioRepository.findById(1)).thenReturn(Optional.of(veterinarioEjemplo));
        when(especialidadRepository.findById(2)).thenReturn(Optional.of(nuevaEspecialidad));
        when(veterinarioRepository.save(any(Veterinario.class))).thenReturn(veterinarioEjemplo);

        // ACT
        Veterinario resultado = veterinarioService.actualizar(1, datosActualizados);

        // ASSERT: la especialidad del veterinario debe ser ahora la "nuevaEspecialidad" completa
        assertEquals(nuevaEspecialidad, resultado.getEspecialidad());

        verify(especialidadRepository, times(1)).findById(2);
        verify(veterinarioRepository, times(1)).save(veterinarioEjemplo);
    }

    /**
     * Caso de error: se intenta actualizar un veterinario que no existe (id no encontrado).
     * El service debe lanzar RuntimeException y no debe llamar a save().
     */
    @Test
    void testActualizar_VeterinarioNoEncontrado() {

        // ARRANGE: no existe ningún veterinario con id=99
        when(veterinarioRepository.findById(99)).thenReturn(Optional.empty());

        Veterinario datosActualizados = new Veterinario();
        datosActualizados.setNombre("X");

        // ACT
        RuntimeException error = assertThrows(RuntimeException.class, () -> {
            veterinarioService.actualizar(99, datosActualizados);
        });

        // ASSERT
        assertEquals("Veterinario no encontrado con id: 99", error.getMessage());

        // Si no existe el veterinario, jamás se debe llamar a save()
        verify(veterinarioRepository, never()).save(any(Veterinario.class));
    }

    // ===================== ELIMINAR =====================

    /**
     * Caso feliz: el veterinario existe, por lo tanto se llama a deleteById().
     */
    @Test
    void testEliminar_Exito() {

        // ARRANGE: el veterinario con id=1 existe
        when(veterinarioRepository.existsById(1)).thenReturn(true);

        // ACT
        veterinarioService.eliminar(1);

        // ASSERT: se verificó la existencia y luego se eliminó
        verify(veterinarioRepository, times(1)).existsById(1);
        verify(veterinarioRepository, times(1)).deleteById(1);
    }

    /**
     * Caso de error: el veterinario NO existe, por lo tanto el service
     * debe lanzar una excepción y NUNCA llamar a deleteById().
     */
    @Test
    void testEliminar_NoEncontrado() {

        // ARRANGE: el veterinario con id=99 no existe
        when(veterinarioRepository.existsById(99)).thenReturn(false);

        // ACT
        RuntimeException error = assertThrows(RuntimeException.class, () -> {
            veterinarioService.eliminar(99);
        });

        // ASSERT
        assertEquals("Veterinario no encontrado con id: 99", error.getMessage());

        // Nunca debe intentar borrar algo que no existe
        verify(veterinarioRepository, never()).deleteById(any());
    }
}