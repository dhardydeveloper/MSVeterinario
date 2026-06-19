package cl.duoc.atencionClinica.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.atencionClinica.model.Atencion;
import cl.duoc.atencionClinica.model.Box;
import cl.duoc.atencionClinica.model.TipoAtencion;
import cl.duoc.atencionClinica.repository.AtencionRepository;
import cl.duoc.atencionClinica.repository.BoxRepository;
import cl.duoc.atencionClinica.repository.TipoAtencionRepository;

@Configuration // Clase de configuración para cargar datos iniciales en la base de datos
public class DataLoader {

    @Bean // Método que se ejecuta al iniciar la aplicación para cargar datos iniciales
    CommandLineRunner cargarDatosIniciales(
            TipoAtencionRepository tipoAtencionRepository,
            BoxRepository boxRepository,
            AtencionRepository atencionRepository) {

        return args -> { 

            if (tipoAtencionRepository.count() == 0) { // Cargar tipos de atención solo si no existen en la base de datos

                // Crear instancias de TipoAtencion con datos iniciales
                // Tipo de atención 1: Consulta General
                TipoAtencion tipo1 = new TipoAtencion();
                tipo1.setNombreTipo("Consulta General");
                tipo1.setDescripcion("Atencion clinica basica para revision general");
                tipo1.setPrecioBase(15000.0);

                // Tipo de atención 2: Control Especializado
                TipoAtencion tipo2 = new TipoAtencion();
                tipo2.setNombreTipo("Control Especializado");
                tipo2.setDescripcion("Atencion especializada segun diagnostico");
                tipo2.setPrecioBase(22000.0);

                // Tipo de atención 3: Urgencia
                TipoAtencion tipo3 = new TipoAtencion();
                tipo3.setNombreTipo("Urgencia");
                tipo3.setDescripcion("Atencion prioritaria por urgencia veterinaria");
                tipo3.setPrecioBase(30000.0);

                // Guardar los tipos de atención en la base de datos
                tipoAtencionRepository.save(tipo1);
                tipoAtencionRepository.save(tipo2);
                tipoAtencionRepository.save(tipo3);
            }

            
            if (boxRepository.count() == 0) { // Cargar boxes solo si no existen en la base de datos

                // Crear instancias de Box con datos iniciales
                Box box1 = new Box();
                box1.setNombreBox("Box 1");
                box1.setDescripcion("Box de atencion general");
                box1.setEstado("DISPONIBLE");

                // Box 2: Box de procedimiento
                Box box2 = new Box();
                box2.setNombreBox("Box 2");
                box2.setDescripcion("Box de procedimiento");
                box2.setEstado("DISPONIBLE");

                // Box 3: Box de urgencia
                Box box3 = new Box();
                box3.setNombreBox("Box 3");
                box3.setDescripcion("Box de urgencia");
                box3.setEstado("DISPONIBLE");

                // Guardar los boxes en la base de datos
                boxRepository.save(box1);
                boxRepository.save(box2);
                boxRepository.save(box3);
            }

            if (atencionRepository.count() == 0) { //| Cargar atenciones solo si no existen en la base de datos

                TipoAtencion tipo1 = tipoAtencionRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("TipoAtencion 1 no encontrado"));

                TipoAtencion tipo2 = tipoAtencionRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("TipoAtencion 2 no encontrado"));

                TipoAtencion tipo3 = tipoAtencionRepository.findById(3)
                        .orElseThrow(() -> new RuntimeException("TipoAtencion 3 no encontrado"));

                Box box1 = boxRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Box 1 no encontrado"));

                Box box2 = boxRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("Box 2 no encontrado"));

                Box box3 = boxRepository.findById(3)
                        .orElseThrow(() -> new RuntimeException("Box 3 no encontrado"));

                // Crear instancias de Atencion con datos iniciales
                Atencion atencion1 = new Atencion();
                atencion1.setIdCita(1);
                atencion1.setIdMascota(1);
                atencion1.setIdVeterinario(1);
                atencion1.setTipoAtencion(tipo1);
                atencion1.setBox(box1);
                atencion1.setFechaAtencion(LocalDate.of(2026, 5, 9));
                atencion1.setDiagnostico("Mascota en buen estado general");
                atencion1.setTratamiento("Control preventivo y revision general");
                atencion1.setObservaciones("Se recomienda control en 6 meses");
                atencion1.setPesoActual(28.5);

                Atencion atencion2 = new Atencion();
                atencion2.setIdCita(2);
                atencion2.setIdMascota(2);
                atencion2.setIdVeterinario(2);
                atencion2.setTipoAtencion(tipo2);
                atencion2.setBox(box2);
                atencion2.setFechaAtencion(LocalDate.of(2026, 5, 9));
                atencion2.setDiagnostico("Paciente estable despues de procedimiento");
                atencion2.setTratamiento("Reposo y medicamento indicado");
                atencion2.setObservaciones("Revisar evolucion en 7 dias");
                atencion2.setPesoActual(6.8);

                Atencion atencion3 = new Atencion();
                atencion3.setIdCita(3);
                atencion3.setIdMascota(3);
                atencion3.setIdVeterinario(3);
                atencion3.setTipoAtencion(tipo3);
                atencion3.setBox(box3);
                atencion3.setFechaAtencion(LocalDate.of(2026, 5, 9));
                atencion3.setDiagnostico("Irritacion leve en piel");
                atencion3.setTratamiento("Tratamiento dermatologico inicial");
                atencion3.setObservaciones("Evitar contacto con agentes irritantes");
                atencion3.setPesoActual(7.2);

                // Guardar las atenciones en la base de datos
                atencionRepository.save(atencion1);
                atencionRepository.save(atencion2);
                atencionRepository.save(atencion3);
            }

            System.out.println("Datos iniciales cargados correctamente en Atención Clínica."); // Mensaje de confirmación en la consola
        };
    }
}