package cl.duoc.fichaclinica.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.fichaclinica.model.FichaClinica;
import cl.duoc.fichaclinica.model.Medicamento;
import cl.duoc.fichaclinica.model.Receta;
import cl.duoc.fichaclinica.repository.FichaClinicaRepository;
import cl.duoc.fichaclinica.repository.MedicamentoRepository;
import cl.duoc.fichaclinica.repository.RecetaRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner cargarDatosIniciales(
            FichaClinicaRepository fichaClinicaRepository,
            MedicamentoRepository medicamentoRepository,
            RecetaRepository recetaRepository) {

        return args -> {

            if (fichaClinicaRepository.count() == 0) {

                FichaClinica ficha1 = new FichaClinica();
                ficha1.setIdMascota(1);
                ficha1.setIdVeterinario("VET001");
                ficha1.setAntecedentes("Mascota sin antecedentes graves");
                ficha1.setAlergias("Sin alergias conocidas");
                ficha1.setEnfermedadesPrevias("Ninguna registrada");
                ficha1.setObservaciones("Ficha creada en primera atencion clinica");
                ficha1.setFechaCreacion(LocalDate.of(2026, 5, 9));

                FichaClinica ficha2 = new FichaClinica();
                ficha2.setIdMascota(2);
                ficha2.setIdVeterinario("VET002");
                ficha2.setAntecedentes("Paciente con procedimiento previo");
                ficha2.setAlergias("Alergia leve a ciertos alimentos");
                ficha2.setEnfermedadesPrevias("Ninguna enfermedad grave registrada");
                ficha2.setObservaciones("Requiere control de evolucion");
                ficha2.setFechaCreacion(LocalDate.of(2026, 5, 9));

                FichaClinica ficha3 = new FichaClinica();
                ficha3.setIdMascota(3);
                ficha3.setIdVeterinario("VET003");
                ficha3.setAntecedentes("Mascota con irritacion de piel");
                ficha3.setAlergias("Sin alergias conocidas");
                ficha3.setEnfermedadesPrevias("Dermatitis leve previa");
                ficha3.setObservaciones("Mantener seguimiento dermatologico");
                ficha3.setFechaCreacion(LocalDate.of(2026, 5, 9));

                fichaClinicaRepository.save(ficha1);
                fichaClinicaRepository.save(ficha2);
                fichaClinicaRepository.save(ficha3);
            }

            if (medicamentoRepository.count() == 0) {

                Medicamento medicamento1 = new Medicamento();
                medicamento1.setNombreMedicamento("Antiparasitario");
                medicamento1.setDescripcion("Medicamento para control de parasitos internos");
                medicamento1.setDosisRecomendada("1 comprimido cada 24 horas por 3 dias");

                Medicamento medicamento2 = new Medicamento();
                medicamento2.setNombreMedicamento("Antiinflamatorio");
                medicamento2.setDescripcion("Medicamento para disminuir inflamacion");
                medicamento2.setDosisRecomendada("Administrar cada 12 horas por 5 dias");

                Medicamento medicamento3 = new Medicamento();
                medicamento3.setNombreMedicamento("Crema Dermatologica");
                medicamento3.setDescripcion("Tratamiento topico para irritacion de piel");
                medicamento3.setDosisRecomendada("Aplicar dos veces al dia por 7 dias");

                medicamentoRepository.save(medicamento1);
                medicamentoRepository.save(medicamento2);
                medicamentoRepository.save(medicamento3);
            }

            if (recetaRepository.count() == 0) {

                Medicamento medicamento1 = medicamentoRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Medicamento 1 no encontrado"));

                Medicamento medicamento2 = medicamentoRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("Medicamento 2 no encontrado"));

                Medicamento medicamento3 = medicamentoRepository.findById(3)
                        .orElseThrow(() -> new RuntimeException("Medicamento 3 no encontrado"));

                Receta receta1 = new Receta();
                receta1.setIdAtencion(1);
                receta1.setIdMascota(1);
                receta1.setIdVeterinario(1);
                receta1.setMedicamento(medicamento1);
                receta1.setFechaEmision(LocalDate.of(2026, 5, 9));
                receta1.setIndicaciones("Administrar despues de la comida");

                Receta receta2 = new Receta();
                receta2.setIdAtencion(2);
                receta2.setIdMascota(2);
                receta2.setIdVeterinario(2);
                receta2.setMedicamento(medicamento2);
                receta2.setFechaEmision(LocalDate.of(2026, 5, 9));
                receta2.setIndicaciones("Administrar segun indicacion veterinaria");

                Receta receta3 = new Receta();
                receta3.setIdAtencion(3);
                receta3.setIdMascota(3);
                receta3.setIdVeterinario(3);
                receta3.setMedicamento(medicamento3);
                receta3.setFechaEmision(LocalDate.of(2026, 5, 9));
                receta3.setIndicaciones("Aplicar en zona afectada evitando contacto con ojos");

                recetaRepository.save(receta1);
                recetaRepository.save(receta2);
                recetaRepository.save(receta3);
            }

            System.out.println("Datos iniciales cargados correctamente en Ficha Clínica.");
        };
    }
}