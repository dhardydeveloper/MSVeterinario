package cl.duoc.fichaclinica.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.fichaclinica.model.FichaClinica;
import cl.duoc.fichaclinica.repository.FichaClinicaRepository;
import cl.duoc.fichaclinica.service.FichaClinicaService;

@ExtendWith(MockitoExtension.class)
class FichaClinicaServiceTest {

    @Mock
    private FichaClinicaRepository fichaClinicaRepository;

    @InjectMocks
    private FichaClinicaService fichaClinicaService;

    private FichaClinica fichaClinica;

    @BeforeEach
    void setUp() {
        fichaClinica = new FichaClinica();
        fichaClinica.setIdFicha(1);
        fichaClinica.setIdMascota(1);
        fichaClinica.setIdVeterinario("VET001");
        fichaClinica.setAntecedentes("Sin antecedentes graves");
        fichaClinica.setAlergias("Sin alergias");
        fichaClinica.setEnfermedadesPrevias("Ninguna");
        fichaClinica.setObservaciones("Todo normal");
        fichaClinica.setFechaCreacion(LocalDate.of(2026, 5, 9));
    }

    @Test
    void listar_deberiaRetornarListaDeFichas() {
        when(fichaClinicaRepository.findAll()).thenReturn(Arrays.asList(fichaClinica));

        List<FichaClinica> resultado = fichaClinicaService.listar();

        assertEquals(1, resultado.size());
        verify(fichaClinicaRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarFicha() {
        when(fichaClinicaRepository.findById(1)).thenReturn(Optional.of(fichaClinica));

        FichaClinica resultado = fichaClinicaService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdMascota());
        verify(fichaClinicaRepository, times(1)).findById(1);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(fichaClinicaRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fichaClinicaService.buscarPorId(99));

        assertEquals("Ficha clínica no encontrada con id: 99", ex.getMessage());
    }

    @Test
    void buscarPorMascota_deberiaRetornarListaDeFichas() {
        when(fichaClinicaRepository.findByIdMascota(1)).thenReturn(Arrays.asList(fichaClinica));

        List<FichaClinica> resultado = fichaClinicaService.buscarPorMascota(1);

        assertEquals(1, resultado.size());
        verify(fichaClinicaRepository, times(1)).findByIdMascota(1);
    }

    @Test
    void guardar_cuandoDatosValidos_deberiaGuardarYRetornarFicha() {
        when(fichaClinicaRepository.save(any(FichaClinica.class))).thenReturn(fichaClinica);

        FichaClinica resultado = fichaClinicaService.guardar(fichaClinica);

        assertNotNull(resultado);
        assertEquals("Sin antecedentes graves", resultado.getAntecedentes());
        verify(fichaClinicaRepository, times(1)).save(fichaClinica);
    }

    @Test
    void guardar_cuandoIdMascotaEsNulo_deberiaLanzarExcepcion() {
        fichaClinica.setIdMascota(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fichaClinicaService.guardar(fichaClinica));

        assertEquals("El id de la mascota es obligatorio", ex.getMessage());
        verify(fichaClinicaRepository, never()).save(any(FichaClinica.class));
    }

    @Test
    void guardar_cuandoFechaCreacionEsNula_deberiaLanzarExcepcion() {
        fichaClinica.setFechaCreacion(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fichaClinicaService.guardar(fichaClinica));

        assertEquals("La fecha de creación es obligatoria", ex.getMessage());
        verify(fichaClinicaRepository, never()).save(any(FichaClinica.class));
    }

    @Test
    void actualizar_cuandoExiste_deberiaActualizarYRetornarFicha() {
        FichaClinica datosActualizados = new FichaClinica();
        datosActualizados.setIdMascota(2);
        datosActualizados.setAntecedentes("Nuevos antecedentes");
        datosActualizados.setAlergias("Alergia al polen");
        datosActualizados.setEnfermedadesPrevias("Ninguna");
        datosActualizados.setObservaciones("Requiere control");
        datosActualizados.setFechaCreacion(LocalDate.of(2026, 5, 10));

        when(fichaClinicaRepository.findById(1)).thenReturn(Optional.of(fichaClinica));
        when(fichaClinicaRepository.save(any(FichaClinica.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FichaClinica resultado = fichaClinicaService.actualizar(1, datosActualizados);

        assertEquals(2, resultado.getIdMascota());
        assertEquals("Nuevos antecedentes", resultado.getAntecedentes());
        assertEquals("Alergia al polen", resultado.getAlergias());
        verify(fichaClinicaRepository, times(1)).findById(1);
        verify(fichaClinicaRepository, times(1)).save(fichaClinica);
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminar() {
        when(fichaClinicaRepository.existsById(1)).thenReturn(true);

        fichaClinicaService.eliminar(1);

        verify(fichaClinicaRepository, times(1)).existsById(1);
        verify(fichaClinicaRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(fichaClinicaRepository.existsById(99)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fichaClinicaService.eliminar(99));

        assertEquals("Ficha clínica no encontrada con id: 99", ex.getMessage());
        verify(fichaClinicaRepository, never()).deleteById(any());
    }
}
