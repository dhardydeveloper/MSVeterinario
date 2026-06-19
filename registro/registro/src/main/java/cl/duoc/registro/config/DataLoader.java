package cl.duoc.registro.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.registro.model.Cliente;
import cl.duoc.registro.model.Mascota;
import cl.duoc.registro.repository.ClienteRepository;
import cl.duoc.registro.repository.MascotaRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner cargarDatos(
            ClienteRepository clienteRepository,
            MascotaRepository mascotaRepository) {

        return args -> {

            // Evita que se dupliquen los datos cada vez que se levanta el microservicio
            if (clienteRepository.count() > 0 || mascotaRepository.count() > 0) {
                System.out.println("Los datos iniciales ya existen. No se cargaron nuevamente.");
                return;
            }

            // Crear clientes
            Cliente cliente1 = new Cliente(
                    null,
                    "12.345.678-9",
                    "Juan",
                    "Pérez",
                    "987654321",
                    "juan@gmail.com",
                    "Av. Los Leones 123",
                    "Providencia",
                    "Metropolitana");

            Cliente cliente2 = new Cliente(
                    null,
                    "11.222.333-4",
                    "María",
                    "González",
                    "912345678",
                    "maria@gmail.com",
                    "Pasaje Central 456",
                    "Maipú",
                    "Metropolitana");

            // Guardar clientes
            clienteRepository.save(cliente1);
            clienteRepository.save(cliente2);

            // Crear mascotas
            Mascota mascota1 = new Mascota(
                    null,
                    "Firulais",
                    "Perro",
                    "Labrador",
                    5,
                    "Macho",
                    "Café",
                    25.5,
                    "CHIP12345",
                    cliente1);

            Mascota mascota2 = new Mascota(
                    null,
                    "Mishi",
                    "Gato",
                    "Siames",
                    3,
                    "Hembra",
                    "Blanco",
                    4.2,
                    "CHIP67890",
                    cliente2);

            Mascota mascota3 = new Mascota(
                    null,
                    "Rocky",
                    "Perro",
                    "Poodle",
                    2,
                    "Macho",
                    "Negro",
                    8.0,
                    "CHIP54321",
                    cliente1);

            // Guardar mascotas
            mascotaRepository.save(mascota1);
            mascotaRepository.save(mascota2);
            mascotaRepository.save(mascota3);

            System.out.println("Datos iniciales cargados correctamente.");
        };
    }
}