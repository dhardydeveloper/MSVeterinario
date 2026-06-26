package cl.duoc.examenes.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import cl.duoc.examenes.dto.AtencionDTO;
import cl.duoc.examenes.model.OrdenExamen;
import cl.duoc.examenes.model.TipoExamen;
import cl.duoc.examenes.repository.OrdenExamenRepository;
import cl.duoc.examenes.repository.TipoExamenRepository;
import cl.duoc.examenes.service.OrdenExamenService;

@ExtendWith(MockitoExtension.class)
class OrdenExamenServiceTest {

    @Mock
    private OrdenExamenRepository ordenExamenRepository;

    @Mock
    private TipoExamenRepository tipoExamenRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrdenExamenService ordenExamenService;

    private OrdenExamen ordenExamen;
    private TipoExamen tipoExamen;
    private AtencionDTO atencionDTO;

    @BeforeEach
    void setUp() {
        tipoExamen = new TipoExamen();
        tipoExamen.setIdTipoExamen(1);
        tipoExamen.setNombreExamen("Examen de sangre");
        tipoExamen.setDescripcion("Examen para detectar anomalías en la sangre");
        tipoExamen.setPrecio(10000.0);

        ordenExamen = new OrdenExamen();
        ordenExamen.setIdOrdenExamen(1);
        ordenExamen.setIdAtencion(1);
        ordenExamen.setIdMascota(1);
        ordenExamen.setIdVeterinario(1);
        ordenExamen.setTipoExamen(tipoExamen);
        ordenExamen.setFechaSolicitud(LocalDate.of(2026, 5, 9));
        ordenExamen.setEstado("Pendiente");

        atencionDTO = new AtencionDTO();
        atencionDTO.setIdAtencion(1);
        atencionDTO.setIdMascota(1);
        atencionDTO.setIdVeterinario(1);
    }


    // Listar órdenes de examen
    @Test
    void listar_deberiaRetornarListaDeOrdenes() {
        when(ordenExamenRepository.findAll()).thenReturn(Arrays.asList(ordenExamen));

        List<OrdenExamen> resultado = ordenExamenService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Pendiente", resultado.get(0).getEstado());
        verify(ordenExamenRepository, times(1)).findAll();
    }


    // Buscar por Id (orden encontrada)
    // El repositorio retorna la orden con el id buscado
    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarOrden() {
        when(ordenExamenRepository.findById(1)).thenReturn(Optional.of(ordenExamen));

        OrdenExamen resultado = ordenExamenService.buscarPorId(1);

        // ASSERT: El resultado no es nulo y el estado coincide con el de la orden de ejemplo
        assertNotNull(resultado);
        assertEquals("Pendiente", resultado.getEstado());
        verify(ordenExamenRepository, times(1)).findById(1);
    }

    // Buscar por Id (orden no encontrada)
    // El repositorio no encuentra la orden
    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarExcepcion() {

        // ARRANGE: Optional.empty() simula que el registro NO existe en la base de datos
        when(ordenExamenRepository.findById(99)).thenReturn(Optional.empty());

        // ACT: assertThrows captura la excepción lanzada por el servicio sin detener el test
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ordenExamenService.buscarPorId(99));

        // ASSERT: El mensaje de error debe indicar el id que no fue encontrado       
        assertEquals("Orden de examen no encontrada con id: 99", ex.getMessage());
    }


    // Buscar por atención
    // El repositorio retorna las órdenes asociadas a una atención
    @Test
    void buscarPorAtencion_deberiaRetornarListaDeOrdenes() {
        when(ordenExamenRepository.findByIdAtencion(1)).thenReturn(Arrays.asList(ordenExamen));

        List<OrdenExamen> resultado = ordenExamenService.buscarPorAtencion(1);

        assertEquals(1, resultado.size());
        verify(ordenExamenRepository, times(1)).findByIdAtencion(1);
    }


    // Buscar por mascota
    // El repositorio retorna las órdenes asociadas a una mascota
    @Test
    void buscarPorMascota_deberiaRetornarListaDeOrdenes() {
        when(ordenExamenRepository.findByIdMascota(1)).thenReturn(Arrays.asList(ordenExamen));

        List<OrdenExamen> resultado = ordenExamenService.buscarPorMascota(1);

        assertEquals(1, resultado.size());
        verify(ordenExamenRepository, times(1)).findByIdMascota(1);
    }


    // Buscar por veterinario
    // El repositorio retorna las órdenes del veterinario indicado
    @Test
    void buscarPorVeterinario_deberiaRetornarListaDeOrdenes() {
        when(ordenExamenRepository.findByIdVeterinario(1)).thenReturn(Arrays.asList(ordenExamen));

        List<OrdenExamen> resultado = ordenExamenService.buscarPorVeterinario(1);

        assertEquals(1, resultado.size());
        verify(ordenExamenRepository, times(1)).findByIdVeterinario(1);
    }


    // Guardar orden con datos válidos
    // Se simula la consulta al microservicio de atenciones, la validación del tipo de examen y el guardado
    @Test
    void guardar_cuandoDatosValidos_deberiaGuardarYRetornarOrden() {
        // ARRANGE: El microservicio externo retorna un AtencionDTO válido
        // anyString() acepta cualquier URL para no depender de la URL exacta configurada
        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);

        // ARRANGE: El tipo de examen con id=1 existe en el repositorio
        when(tipoExamenRepository.findById(1)).thenReturn(Optional.of(tipoExamen));
        // El repositorio confirma que la orden fue guardada correctamente
        when(ordenExamenRepository.save(any(OrdenExamen.class))).thenReturn(ordenExamen);

        // ACT: Se ejecuta el guardado de la orden
        OrdenExamen resultado = ordenExamenService.guardar(ordenExamen);

        // ASSERT: El resultado no es nulo, el estado es correcto y save() fue llamado una vez
        assertNotNull(resultado);
        assertEquals("Pendiente", resultado.getEstado());
        verify(ordenExamenRepository, times(1)).save(ordenExamen);
    }


    // Guardar orden sin id de atención
    // El servicio debe lanzar una excepción y nunca intentar guardar
    @Test
    void guardar_cuandoIdAtencionEsNulo_deberiaLanzarExcepcion() {
        ordenExamen.setIdAtencion(null);

         // ACT: El servicio debe detectar el campo nulo y lanzar una excepción
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ordenExamenService.guardar(ordenExamen));

        assertEquals("El id de atención es obligatorio", ex.getMessage());
        // El repositorio nunca debe intentar guardar si los datos son inválidos
        verify(ordenExamenRepository, never()).save(any(OrdenExamen.class));
    }


    // Guardar orden con tipo de examen inexistente
    // El repositorio no encuentra el tipo de examen, lanza una excepción y nunca se guarda
    @Test
    void guardar_cuandoTipoExamenNoExiste_deberiaLanzarExcepcion() {
        // ARRANGE: Se crea una orden con un tipo de examen con id=99 que no existe
        OrdenExamen nueva = new OrdenExamen();
        nueva.setIdAtencion(1);
        nueva.setIdMascota(1);
        nueva.setIdVeterinario(1);
        TipoExamen tipoRef = new TipoExamen();
        tipoRef.setIdTipoExamen(99);
        nueva.setTipoExamen(tipoRef);
        nueva.setFechaSolicitud(LocalDate.now());
        nueva.setEstado("Pendiente");

        // ARRANGE: El microservicio externo retorna un AtencionDTO válido
        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);
        // ARRANGE: Optional.empty() simula que el tipo de examen con id=99 NO existe
        when(tipoExamenRepository.findById(99)).thenReturn(Optional.empty());

        // ACT: el servicio debe detectar que el tipo de examen no existe y lanzar una excepción
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ordenExamenService.guardar(nueva));

        // ASSERT: el mensaje indica el id del tipo de examen que no fue encontrado
        assertEquals("Tipo de examen no encontrado con id: 99", ex.getMessage());
        // El repositorio nunca debe intentar guardar si el tipo de examen no existe
        verify(ordenExamenRepository, never()).save(any(OrdenExamen.class));
    }


    // Actualizar orden existente con datos válidos
    // Se simula la consulta al microservicio, la validación del tipo de examen y el guardado de los cambios
    @Test
    void actualizar_cuandoExiste_deberiaActualizarYRetornarOrden() {
        OrdenExamen datosActualizados = new OrdenExamen();
        datosActualizados.setIdAtencion(1);
        datosActualizados.setIdMascota(1);
        datosActualizados.setIdVeterinario(1);
        datosActualizados.setTipoExamen(tipoExamen);
        datosActualizados.setFechaSolicitud(LocalDate.of(2026, 5, 10));
        datosActualizados.setEstado("Completada");

        when(ordenExamenRepository.findById(1)).thenReturn(Optional.of(ordenExamen));
        // ARRANGE: El microservicio externo retorna un AtencionDTO válido
        when(restTemplate.getForObject(anyString(), eq(AtencionDTO.class))).thenReturn(atencionDTO);
        // ARRANGE: El tipo de examen con id=1 existe en el repositorio
        when(tipoExamenRepository.findById(1)).thenReturn(Optional.of(tipoExamen));
        // ARRANGE: thenAnswer retorna el mismo objeto que recibe, simulando que se guardaron los cambios
        when(ordenExamenRepository.save(any(OrdenExamen.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT: Actualización con los nuevos datos
        OrdenExamen resultado = ordenExamenService.actualizar(1, datosActualizados);

        // ASSERT: El estado fue actualizado correctamente y el repositorio guardó exactamente una vez
        assertEquals("Completada", resultado.getEstado());
        verify(ordenExamenRepository, times(1)).findById(1);
        verify(ordenExamenRepository, times(1)).save(ordenExamen);
    }


    // Eliminar orden existente
    // La orden existe, se verifica que el repositorio ejecute el borrado exactamente una vez
    @Test
    void eliminar_cuandoExiste_deberiaEliminar() {
        // ARRANGE: existsById retorna true indicando que la orden sí existe
        when(ordenExamenRepository.existsById(1)).thenReturn(true);

        // ACT: Eliminación de la orden
        ordenExamenService.eliminar(1);

        // ASSERT: existsById y deleteById fueron llamados exactamente una vez con el id correcto
        verify(ordenExamenRepository, times(1)).existsById(1);
        verify(ordenExamenRepository, times(1)).deleteById(1);
    }

    // Eliminar orden no existente
    // El repositorio no encuentra la orden, lanza una excepción y nunca se intenta borrar
    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarExcepcion() {
        // ARRANGE: existsById retorna false indicando que la orden no existe
        when(ordenExamenRepository.existsById(99)).thenReturn(false);

        // ACT: Lanza una excepción al no encontrar la orden 
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ordenExamenService.eliminar(99));

        assertEquals("Orden de examen no encontrada con id: 99", ex.getMessage());
        verify(ordenExamenRepository, never()).deleteById(any());
    }
}
