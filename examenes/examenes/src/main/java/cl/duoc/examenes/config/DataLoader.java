package cl.duoc.examenes.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.examenes.model.OrdenExamen;
import cl.duoc.examenes.model.ResultadoExamen;
import cl.duoc.examenes.model.TipoExamen;
import cl.duoc.examenes.repository.OrdenExamenRepository;
import cl.duoc.examenes.repository.ResultadoExamenRepository;
import cl.duoc.examenes.repository.TipoExamenRepository;

@Configuration // Clase de configuración para cargar datos iniciales en la base de datos
public class DataLoader {

    @Bean // Bean de tipo CommandLineRunner que se ejecuta al iniciar la aplicación para cargar datos iniciales
    CommandLineRunner cargarDatosIniciales(
            TipoExamenRepository tipoExamenRepository,
            OrdenExamenRepository ordenExamenRepository,
            ResultadoExamenRepository resultadoExamenRepository) {

        return args -> {

            if (tipoExamenRepository.count() == 0) { // Verifica si no hay tipos de examen en la base de datos antes de cargar los datos iniciales

                // Crea y guarda algunos tipos de examen iniciales en la base de datos
                TipoExamen tipo1 = new TipoExamen();
                tipo1.setNombreExamen("Hemograma");
                tipo1.setDescripcion("Examen de sangre para revisar estado general");
                tipo1.setPrecio(12000.0);

                TipoExamen tipo2 = new TipoExamen();
                tipo2.setNombreExamen("Radiografia");
                tipo2.setDescripcion("Examen de imagen para revision interna");
                tipo2.setPrecio(25000.0);

                TipoExamen tipo3 = new TipoExamen();
                tipo3.setNombreExamen("Examen Dermatologico");
                tipo3.setDescripcion("Evaluacion de piel y pelaje");
                tipo3.setPrecio(18000.0);

                // Guarda los tipos de examen en la base de datos
                tipoExamenRepository.save(tipo1);
                tipoExamenRepository.save(tipo2);
                tipoExamenRepository.save(tipo3);
            }

            if (ordenExamenRepository.count() == 0) { // Verifica si no hay órdenes de examen en la base de datos antes de cargar los datos iniciales

                TipoExamen tipo1 = tipoExamenRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("TipoExamen 1 no encontrado"));

                TipoExamen tipo2 = tipoExamenRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("TipoExamen 2 no encontrado"));

                TipoExamen tipo3 = tipoExamenRepository.findById(3)
                        .orElseThrow(() -> new RuntimeException("TipoExamen 3 no encontrado"));

                // Crea y guarda algunas órdenes de examen iniciales en la base de datos
                OrdenExamen orden1 = new OrdenExamen();
                orden1.setIdAtencion(1);
                orden1.setIdMascota(1);
                orden1.setIdVeterinario(1);
                orden1.setTipoExamen(tipo1);
                orden1.setFechaSolicitud(LocalDate.of(2026, 5, 9));
                orden1.setEstado("SOLICITADO");

                OrdenExamen orden2 = new OrdenExamen();
                orden2.setIdAtencion(2);
                orden2.setIdMascota(2);
                orden2.setIdVeterinario(2);
                orden2.setTipoExamen(tipo2);
                orden2.setFechaSolicitud(LocalDate.of(2026, 5, 9));
                orden2.setEstado("SOLICITADO");

                OrdenExamen orden3 = new OrdenExamen();
                orden3.setIdAtencion(3);
                orden3.setIdMascota(3);
                orden3.setIdVeterinario(3);
                orden3.setTipoExamen(tipo3);
                orden3.setFechaSolicitud(LocalDate.of(2026, 5, 9));
                orden3.setEstado("SOLICITADO");

                // Guarda las órdenes de examen en la base de datos
                ordenExamenRepository.save(orden1);
                ordenExamenRepository.save(orden2);
                ordenExamenRepository.save(orden3);
            }

            if (resultadoExamenRepository.count() == 0) { // Verifica si no hay resultados de examen en la base de datos antes de cargar los datos iniciales

                OrdenExamen orden1 = ordenExamenRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("OrdenExamen 1 no encontrada"));

                OrdenExamen orden2 = ordenExamenRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("OrdenExamen 2 no encontrada"));

                OrdenExamen orden3 = ordenExamenRepository.findById(3)
                        .orElseThrow(() -> new RuntimeException("OrdenExamen 3 no encontrada"));

                // Crea y guarda algunos resultados de examen iniciales en la base de datos
                ResultadoExamen resultado1 = new ResultadoExamen();
                resultado1.setOrdenExamen(orden1);
                resultado1.setResultado("Parametros dentro de rango normal");
                resultado1.setObservacion("No se observan alteraciones relevantes");
                resultado1.setFechaResultado(LocalDate.of(2026, 5, 10));

                ResultadoExamen resultado2 = new ResultadoExamen();
                resultado2.setOrdenExamen(orden2);
                resultado2.setResultado("Imagen sin lesiones visibles");
                resultado2.setObservacion("Se recomienda control si persisten sintomas");
                resultado2.setFechaResultado(LocalDate.of(2026, 5, 10));

                ResultadoExamen resultado3 = new ResultadoExamen();
                resultado3.setOrdenExamen(orden3);
                resultado3.setResultado("Irritacion leve detectada");
                resultado3.setObservacion("Aplicar tratamiento indicado por veterinario");
                resultado3.setFechaResultado(LocalDate.of(2026, 5, 10));

                // Guarda los resultados de examen en la base de datos
                resultadoExamenRepository.save(resultado1);
                resultadoExamenRepository.save(resultado2);
                resultadoExamenRepository.save(resultado3);
            }

            System.out.println("Datos iniciales cargados correctamente en Exámenes."); // Mensaje de confirmación en la consola al finalizar la carga de datos iniciales
        };
    }
}