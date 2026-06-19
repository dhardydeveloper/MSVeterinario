package cl.duoc.veterinario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.veterinario.model.Especialidad;
import cl.duoc.veterinario.model.Veterinario;
import cl.duoc.veterinario.repository.EspecialidadRepository;
import cl.duoc.veterinario.repository.VeterinarioRepository;

@Configuration
public class DataLoader {

    @Bean // usado para indicar que este método devuelve un bean que debe ser gestionado por el contenedor de Spring, en este caso un CommandLineRunner que se ejecutará al iniciar la aplicación
    CommandLineRunner cargarDatosIniciales(
            EspecialidadRepository especialidadRepository, // Inyección de dependencia del repositorio de especialidades para interactuar con la base de datos
            VeterinarioRepository veterinarioRepository) { // Inyección de dependencia del repositorio de veterinarios para interactuar con la base de datos

        return args -> { // El CommandLineRunner se ejecuta al iniciar la aplicación, y el código dentro de este bloque se ejecutará después de que el contexto de Spring se haya inicializado

            if (especialidadRepository.count() == 0) { // Verifica si el repositorio de especialidades está vacío, es decir, si no hay especialidades en la base de datos. Si el conteo es 0, significa que no hay datos y se procede a cargar los datos iniciales.

                Especialidad especialidad1 = new Especialidad();
                especialidad1.setNombre("Medicina General");

                Especialidad especialidad2 = new Especialidad();
                especialidad2.setNombre("Cirugia");

                Especialidad especialidad3 = new Especialidad();
                especialidad3.setNombre("Dermatologia");

                // Guarda las especialidades en la base de datos utilizando el repositorio de especialidades
                especialidadRepository.save(especialidad1);
                especialidadRepository.save(especialidad2);
                especialidadRepository.save(especialidad3);
            }

            if (veterinarioRepository.count() == 0) { // Verifica si el repositorio de veterinarios está vacío, es decir, si no hay veterinarios en la base de datos. Si el conteo es 0, significa que no hay datos y se procede a cargar los datos iniciales.

                Especialidad especialidad1 = especialidadRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Especialidad 1 no encontrada"));

                Especialidad especialidad2 = especialidadRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("Especialidad 2 no encontrada"));

                Especialidad especialidad3 = especialidadRepository.findById(3)
                        .orElseThrow(() -> new RuntimeException("Especialidad 3 no encontrada"));

            
                // Crea instancias de veterinarios y les asigna las especialidades correspondientes
                Veterinario veterinario1 = new Veterinario();
                veterinario1.setRut("12345678-9");
                veterinario1.setNombre("Makarena");
                veterinario1.setApellido("Rojas");
                veterinario1.setEspecialidad(especialidad1);

                Veterinario veterinario2 = new Veterinario();
                veterinario2.setRut("11111111-1");
                veterinario2.setNombre("Sebastian");
                veterinario2.setApellido("Morales");
                veterinario2.setEspecialidad(especialidad2);

                Veterinario veterinario3 = new Veterinario();
                veterinario3.setRut("22222222-2");
                veterinario3.setNombre("Francisca");
                veterinario3.setApellido("Vega");
                veterinario3.setEspecialidad(especialidad3);

                // Guarda los veterinarios en la base de datos utilizando el repositorio de veterinarios
                veterinarioRepository.save(veterinario1);
                veterinarioRepository.save(veterinario2);
                veterinarioRepository.save(veterinario3);
            }

            System.out.println("Datos iniciales cargados correctamente en Veterinario."); // Imprime un mensaje en la consola indicando que los datos iniciales se han cargado correctamente
        };
    }
}