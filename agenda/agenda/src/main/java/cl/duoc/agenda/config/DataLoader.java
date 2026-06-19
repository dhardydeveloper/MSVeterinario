package cl.duoc.agenda.config;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.agenda.model.Agenda;
import cl.duoc.agenda.model.Cita;
import cl.duoc.agenda.repository.AgendaRepository;
import cl.duoc.agenda.repository.CitaRepository;

@Configuration
public class DataLoader {

    @Bean // Este método se ejecutará al iniciar la aplicación para cargar datos iniciales
    CommandLineRunner cargarDatosIniciales(
            AgendaRepository agendaRepository,
            CitaRepository citaRepository) {

        return args -> { // Verificar si ya existen datos en la base de datos para evitar duplicados

            if (agendaRepository.count() == 0) { // Solo cargar datos si no hay agendas existentes

                Agenda agenda1 = new Agenda();
                agenda1.setIdVeterinario(1);
                agenda1.setFecha(LocalDate.of(2026, 5, 9));
                agenda1.setHoraInicio(LocalTime.of(11, 0));
                agenda1.setHoraFin(LocalTime.of(12, 0));
                agenda1.setEstado("DISPONIBLE");

                Agenda agenda2 = new Agenda();
                agenda2.setIdVeterinario(2);
                agenda2.setFecha(LocalDate.of(2026, 5, 9));
                agenda2.setHoraInicio(LocalTime.of(12, 0));
                agenda2.setHoraFin(LocalTime.of(13, 0));
                agenda2.setEstado("DISPONIBLE");

                Agenda agenda3 = new Agenda();
                agenda3.setIdVeterinario(3);
                agenda3.setFecha(LocalDate.of(2026, 5, 9));
                agenda3.setHoraInicio(LocalTime.of(15, 0));
                agenda3.setHoraFin(LocalTime.of(16, 0));
                agenda3.setEstado("DISPONIBLE");

                agendaRepository.save(agenda1);
                agendaRepository.save(agenda2);
                agendaRepository.save(agenda3);
            }

            if (citaRepository.count() == 0) { // Solo cargar datos si no hay citas existentes

                Agenda agenda1 = agendaRepository.findById(1) // Obtener las agendas recién creadas para asociarlas a las citas
                        .orElseThrow(() -> new RuntimeException("Agenda 1 no encontrada")); // Manejo de error si la agenda no se encuentra

                Agenda agenda2 = agendaRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("Agenda 2 no encontrada"));

                Agenda agenda3 = agendaRepository.findById(3)
                        .orElseThrow(() -> new RuntimeException("Agenda 3 no encontrada"));

                // Crear citas asociadas a las agendas correspondientes
                Cita cita1 = new Cita();
                cita1.setIdMascota(1);
                cita1.setIdVeterinario(1);
                cita1.setAgenda(agenda1);
                cita1.setMotivo("Control general");
                cita1.setFecha(LocalDate.of(2026, 5, 9));
                cita1.setHora(LocalTime.of(11, 30));
                cita1.setEstado("AGENDADA");

                Cita cita2 = new Cita();
                cita2.setIdMascota(2);
                cita2.setIdVeterinario(2);
                cita2.setAgenda(agenda2);
                cita2.setMotivo("Revision post operatoria");
                cita2.setFecha(LocalDate.of(2026, 5, 9));
                cita2.setHora(LocalTime.of(12, 30));
                cita2.setEstado("AGENDADA");

                Cita cita3 = new Cita();
                cita3.setIdMascota(3);
                cita3.setIdVeterinario(3);
                cita3.setAgenda(agenda3);
                cita3.setMotivo("Consulta dermatologica");
                cita3.setFecha(LocalDate.of(2026, 5, 9));
                cita3.setHora(LocalTime.of(15, 30));
                cita3.setEstado("AGENDADA");

                // Guardar las citas en la base de datos
                citaRepository.save(cita1);
                citaRepository.save(cita2);
                citaRepository.save(cita3);
            }

            System.out.println("Datos iniciales cargados correctamente en Agenda y Cita."); // Mensaje de confirmación en la consola
        };
    }
}