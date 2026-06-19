package cl.duoc.registro.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import cl.duoc.registro.dto.RegistroDTO;
import cl.duoc.registro.model.Cliente;
import cl.duoc.registro.model.Mascota;
import cl.duoc.registro.repository.ClienteRepository;
import cl.duoc.registro.repository.MascotaRepository;
import cl.duoc.registro.service.RegistroService;

/**
 * Clase de pruebas unitarias para el servicio RegistroService.
 * Utiliza MockitoExtension para el aislamiento de dependencias mediante mocks.
 */
@ExtendWith(MockitoExtension.class)
class RegistroServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private RegistroService registroService;

    private Cliente cliente;
    private Mascota mascota;

    /**
     * Configuración inicial que se ejecuta antes de cada test.
     * Prepara instancias base de Cliente y Mascota con datos ficticios para las pruebas.
     */
    @BeforeEach
    void setUp() {
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
     * Test que verifica que listarClientes retorne la lista completa desde el repositorio.
     */
    @Test
    void listarClientes_deberiaRetornarListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente));

        List<Cliente> resultado = registroService.listarClientes();

        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        verify(clienteRepository, times(1)).findAll();
    }

    /**
     * Test que verifica la búsqueda exitosa de un cliente por su ID.
     */
    @Test
    void buscarClientePorId_cuandoExiste_deberiaRetornarCliente() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        Cliente resultado = registroService.buscarClientePorId(1);

        assertEquals(cliente.getId(), resultado.getId());
        assertEquals(cliente.getRut(), resultado.getRut());
        verify(clienteRepository, times(1)).findById(1);
    }

    /**
     * Test que verifica que se lance una excepción si el cliente no existe por ID.
     */
    @Test
    void buscarClientePorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> registroService.buscarClientePorId(99));

        assertEquals("Cliente no encontrado con id: 99", ex.getMessage());
    }

    /**
     * Test que verifica la búsqueda exitosa de un cliente mediante su RUT.
     */
    @Test
    void buscarClientePorRut_cuandoExiste_deberiaRetornarCliente() {
        when(clienteRepository.findByRut("11111111-1")).thenReturn(Optional.of(cliente));

        Cliente resultado = registroService.buscarClientePorRut("11111111-1");

        assertEquals(cliente.getRut(), resultado.getRut());
        verify(clienteRepository, times(1)).findByRut("11111111-1");
    }

    /**
     * Test que verifica que se lance una excepción si el cliente no existe por su RUT.
     */
    @Test
    void buscarClientePorRut_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(clienteRepository.findByRut("99999999-9")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> registroService.buscarClientePorRut("99999999-9"));
    }

    /**
     * Test que verifica el flujo correcto para persistir/guardar un nuevo cliente.
     */
    @Test
    void guardarCliente_deberiaGuardarYRetornarCliente() {
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente resultado = registroService.guardarCliente(cliente);

        assertEquals(cliente.getNombre(), resultado.getNombre());
        verify(clienteRepository, times(1)).save(cliente);
    }

    /**
     * Test que verifica la actualización exitosa de los campos de un cliente existente.
     */
    @Test
    void actualizarCliente_cuandoExiste_deberiaActualizarYRetornarCliente() {
        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setRut("22222222-2");
        clienteActualizado.setNombre("Pedro");
        clienteActualizado.setApellido("Soto");
        clienteActualizado.setTelefono("912345678");
        clienteActualizado.setCorreo("pedro.soto@mail.com");
        clienteActualizado.setDireccion("Av. Siempre Viva 742");
        clienteActualizado.setComuna("Providencia");
        clienteActualizado.setRegion("Metropolitana");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente resultado = registroService.actualizarCliente(1, clienteActualizado);

        assertEquals("Pedro", resultado.getNombre());
        assertEquals("Soto", resultado.getApellido());
        assertEquals("22222222-2", resultado.getRut());
        verify(clienteRepository, times(1)).save(cliente);
    }

    /**
     * Test que garantiza que no se intente guardar nada si se busca actualizar un cliente inexistente.
     */
    @Test
    void actualizarCliente_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> registroService.actualizarCliente(99, cliente));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /**
     * Test que verifica la eliminación física de un cliente si se confirma su existencia previa.
     */
    @Test
    void eliminarCliente_cuandoExiste_deberiaEliminar() {
        when(clienteRepository.existsById(1)).thenReturn(true);

        registroService.eliminarCliente(1);

        verify(clienteRepository, times(1)).deleteById(1);
    }

    /**
     * Test que impide el intento de eliminación si el cliente especificado no existe.
     */
    @Test
    void eliminarCliente_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(clienteRepository.existsById(99)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> registroService.eliminarCliente(99));

        verify(clienteRepository, never()).deleteById(anyInt());
    }

    // ===================== MASCOTA =====================

    /**
     * Test que verifica que listarMascotas retorne la lista completa desde el repositorio.
     */
    @Test
    void listarMascotas_deberiaRetornarListaDeMascotas() {
        when(mascotaRepository.findAll()).thenReturn(Arrays.asList(mascota));

        List<Mascota> resultado = registroService.listarMascotas();

        assertEquals(1, resultado.size());
        assertEquals("Firulais", resultado.get(0).getNombre());
        verify(mascotaRepository, times(1)).findAll();
    }

    /**
     * Test que verifica la búsqueda exitosa de una mascota por su ID.
     */
    @Test
    void buscarMascotaPorId_cuandoExiste_deberiaRetornarMascota() {
        when(mascotaRepository.findById(1)).thenReturn(Optional.of(mascota));

        Mascota resultado = registroService.buscarMascotaPorId(1);

        assertEquals(mascota.getId(), resultado.getId());
        verify(mascotaRepository, times(1)).findById(1);
    }

    /**
     * Test que verifica que se lance una excepción si la mascota no existe por ID.
     */
    @Test
    void buscarMascotaPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(mascotaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> registroService.buscarMascotaPorId(99));
    }

    /**
     * Test que verifica la obtención de todas las mascotas vinculadas al ID de un dueño/cliente.
     */
    @Test
    void buscarMascotasPorCliente_deberiaRetornarListaDeMascotas() {
        when(mascotaRepository.findByClienteId(1)).thenReturn(Arrays.asList(mascota));

        List<Mascota> resultado = registroService.buscarMascotasPorCliente(1);

        assertEquals(1, resultado.size());
        verify(mascotaRepository, times(1)).findByClienteId(1);
    }

    /**
     * Test que evalúa la búsqueda de mascotas mediante filtros parciales de texto (ignorando mayúsculas/minúsculas).
     */
    @Test
    void buscarMascotasPorNombre_deberiaRetornarListaDeMascotas() {
        when(mascotaRepository.findByNombreContainingIgnoreCase("firu")).thenReturn(Arrays.asList(mascota));

        List<Mascota> resultado = registroService.buscarMascotasPorNombre("firu");

        assertEquals(1, resultado.size());
        verify(mascotaRepository, times(1)).findByNombreContainingIgnoreCase("firu");
    }

    /**
     * Test que verifica la obtención correcta de una mascota mediante su identificador único de chip.
     */
    @Test
    void buscarMascotaPorNumeroChip_cuandoExiste_deberiaRetornarMascota() {
        when(mascotaRepository.findByNumeroChip("CHIP123")).thenReturn(Optional.of(mascota));

        Mascota resultado = registroService.buscarMascotaPorNumeroChip("CHIP123");

        assertEquals("CHIP123", resultado.getNumeroChip());
    }

    /**
     * Test que valida la excepción controlada al buscar un chip no registrado.
     */
    @Test
    void buscarMascotaPorNumeroChip_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(mascotaRepository.findByNumeroChip("NOEXISTE")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> registroService.buscarMascotaPorNumeroChip("NOEXISTE"));
    }

    /**
     * Test que verifica el guardado de una mascota ligada a un cliente que se encuentra activo y válido.
     */
    @Test
    void guardarMascota_cuandoClienteExiste_deberiaGuardarMascota() {
        Mascota nuevaMascota = new Mascota();
        nuevaMascota.setNombre("Rocky");
        Cliente clienteRef = new Cliente();
        clienteRef.setId(1);
        nuevaMascota.setCliente(clienteRef);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota resultado = registroService.guardarMascota(nuevaMascota);

        assertEquals("Rocky", resultado.getNombre());
        assertEquals(cliente.getId(), resultado.getCliente().getId());
        verify(mascotaRepository, times(1)).save(nuevaMascota);
    }

    /**
     * Test que impide guardar una mascota si el dueño asignado no existe en el sistema.
     */
    @Test
    void guardarMascota_cuandoClienteNoExiste_deberiaLanzarExcepcion() {
        Mascota nuevaMascota = new Mascota();
        Cliente clienteRef = new Cliente();
        clienteRef.setId(99);
        nuevaMascota.setCliente(clienteRef);

        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> registroService.guardarMascota(nuevaMascota));

        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    /**
     * Test que valida la actualización de los atributos propios de la mascota, manteniendo el mismo cliente intacto.
     */
    @Test
    void actualizarMascota_sinCambioDeCliente_deberiaActualizarDatosBasicos() {
        Mascota mascotaActualizada = new Mascota();
        mascotaActualizada.setNombre("Firulais II");
        mascotaActualizada.setEspecie("Perro");
        mascotaActualizada.setRaza("Mestizo");
        mascotaActualizada.setEdad(4);
        mascotaActualizada.setSexo("Macho");
        mascotaActualizada.setColor("Negro");
        mascotaActualizada.setPeso(22.0);
        mascotaActualizada.setNumeroChip("CHIP999");

        when(mascotaRepository.findById(1)).thenReturn(Optional.of(mascota));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota resultado = registroService.actualizarMascota(1, mascotaActualizada);

        assertEquals("Firulais II", resultado.getNombre());
        assertEquals("Mestizo", resultado.getRaza());
        assertEquals("CHIP999", resultado.getNumeroChip());
        verify(clienteRepository, never()).findById(anyInt());
        verify(mascotaRepository, times(1)).save(mascota);
    }

    /**
     * Test de lógica compleja donde se cambia/transfiere el dueño de la mascota a un cliente nuevo.
     */
    @Test
    void actualizarMascota_conCambioDeCliente_deberiaActualizarDuenio() {
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setId(2);
        nuevoCliente.setNombre("Maria");

        Mascota mascotaActualizada = new Mascota();
        mascotaActualizada.setNombre("Firulais");
        mascotaActualizada.setEspecie("Perro");
        mascotaActualizada.setRaza("Labrador");
        mascotaActualizada.setEdad(3);
        mascotaActualizada.setSexo("Macho");
        mascotaActualizada.setColor("Café");
        mascotaActualizada.setPeso(20.5);
        mascotaActualizada.setNumeroChip("CHIP123");
        mascotaActualizada.setCliente(nuevoCliente);

        when(mascotaRepository.findById(1)).thenReturn(Optional.of(mascota));
        when(clienteRepository.findById(2)).thenReturn(Optional.of(nuevoCliente));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mascota resultado = registroService.actualizarMascota(1, mascotaActualizada);

        assertEquals(2, resultado.getCliente().getId());
        verify(clienteRepository, times(1)).findById(2);
        verify(mascotaRepository, times(1)).save(mascota);
    }

    /**
     * Test que impide operaciones de actualización si el ID de la mascota evaluada no existe.
     */
    @Test
    void actualizarMascota_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(mascotaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> registroService.actualizarMascota(99, mascota));

        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    /**
     * Test que comprueba el borrado exitoso de una mascota tras corroborar su existencia.
     */
    @Test
    void eliminarMascota_cuandoExiste_deberiaEliminar() {
        when(mascotaRepository.existsById(1)).thenReturn(true);

        registroService.eliminarMascota(1);

        verify(mascotaRepository, times(1)).deleteById(1);
    }

    /**
     * Test que impide el borrado en el repositorio de una mascota que no está registrada.
     */
    @Test
    void eliminarMascota_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(mascotaRepository.existsById(99)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> registroService.eliminarMascota(99));

        verify(mascotaRepository, never()).deleteById(anyInt());
    }

    // ===================== DTO =====================

    /**
     * Test crítico del servicio que valida el mapeo correcto y combinación de los modelos 
     * interconectados (Mascota + Cliente) hacia un objeto plano RegistroDTO.
     */
    @Test
    void obtenerRegistroDTO_deberiaCombinarDatosDeClienteYMascota() {
        when(mascotaRepository.findById(1)).thenReturn(Optional.of(mascota));

        RegistroDTO dto = registroService.obtenerRegistroDTO(1);

        // Validaciones del bloque de datos del cliente
        assertEquals(cliente.getId(), dto.getIdCliente());
        assertEquals(cliente.getRut(), dto.getRutCliente());
        assertEquals(cliente.getNombre(), dto.getNombreCliente());
        assertEquals(cliente.getApellido(), dto.getApellidoCliente());

        // Validaciones del bloque de datos de la mascota
        assertEquals(mascota.getId(), dto.getIdMascota());
        assertEquals(mascota.getNombre(), dto.getNombreMascota());
        assertEquals(mascota.getNumeroChip(), dto.getNumeroChipMascota());

        verify(mascotaRepository, times(1)).findById(1);
    }
}