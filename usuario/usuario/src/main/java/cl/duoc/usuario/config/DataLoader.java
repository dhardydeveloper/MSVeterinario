package cl.duoc.usuario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.usuario.model.Rol;
import cl.duoc.usuario.model.Usuario;
import cl.duoc.usuario.repository.RolRepository;
import cl.duoc.usuario.repository.UsuarioRepository;

// Clase de configuración para cargar datos iniciales en la base de datos al iniciar la aplicación. Utiliza CommandLineRunner para ejecutar el código de carga de datos después de que la aplicación se haya iniciado. Verifica si ya existen roles y usuarios antes de cargar los datos para evitar duplicados.
@Configuration
public class DataLoader { 

    @Bean // @Bean crea un objeto que se administra por el contenedor de Spring, en este caso un CommandLineRunner que se ejecutará al iniciar la aplicación para cargar los datos iniciales en la base de datos.
    CommandLineRunner cargarDatosIniciales(// Inyección de dependencias de los repositorios de Rol y Usuario para acceder a la base de datos y cargar los datos iniciales
            RolRepository rolRepository, // Inyección de dependencias del repositorio de roles para acceder a la base de datos y cargar los datos iniciales de roles
            UsuarioRepository usuarioRepository) { // Inyección de dependencias del repositorio de usuarios para acceder a la base de datos y cargar los datos iniciales de usuarios

        return args -> { // args es un arreglo de argumentos que se pueden pasar al ejecutar la aplicación, pero en este caso no se utilizan para cargar los datos iniciales

            if (rolRepository.count() == 0) { // Si no hay roles registrados, cargar los datos iniciales de roles

                
                Rol admin = new Rol();
                admin.setNombreRol("ADMIN");
                admin.setDescripcion("Administrador del sistema");

                Rol veterinario = new Rol();
                veterinario.setNombreRol("VETERINARIO");
                veterinario.setDescripcion("Usuario veterinario del sistema");

                Rol recepcionista = new Rol();
                recepcionista.setNombreRol("RECEPCIONISTA");
                recepcionista.setDescripcion("Usuario encargado de recepción y agenda");

                // Guardar los roles en la base de datos utilizando el repositorio de roles
                rolRepository.save(admin);
                rolRepository.save(veterinario);
                rolRepository.save(recepcionista);
            }

            // Verificar si ya existen usuarios antes de cargar los datos iniciales de usuarios para evitar duplicados
            if (usuarioRepository.count() == 0) { // Si no hay usuarios registrados, cargar los datos iniciales de usuarios

                Rol rolAdmin = rolRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado")); // Buscar el rol ADMIN por su ID (1) para asignarlo al usuario administrador. Si no se encuentra, lanzar una excepción.

                Rol rolVeterinario = rolRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("Rol VETERINARIO no encontrado")); // Buscar el rol VETERINARIO por su ID (2) para asignarlo al usuario veterinario. Si no se encuentra, lanzar una excepción.

                Rol rolRecepcionista = rolRepository.findById(3)
                        .orElseThrow(() -> new RuntimeException("Rol RECEPCIONISTA no encontrado")); // Buscar el rol RECEPCIONISTA por su ID (3) para asignarlo al usuario recepcionista. Si no se encuentra, lanzar una excepción.

                
                // Crear usuarios con los roles correspondientes y asignar sus datos personales, estado y contraseña
                Usuario usuario1 = new Usuario();
                usuario1.setNombre("Camila");
                usuario1.setApellido("Rojas");
                usuario1.setCorreo("camila@gmail.com");
                usuario1.setPassword("1234");
                usuario1.setEstado(true);
                usuario1.setRol(rolAdmin);

                Usuario usuario2 = new Usuario();
                usuario2.setNombre("Makarena");
                usuario2.setApellido("Soto");
                usuario2.setCorreo("makarena@gmail.com");
                usuario2.setPassword("1234");
                usuario2.setEstado(true);
                usuario2.setRol(rolVeterinario);

                Usuario usuario3 = new Usuario();
                usuario3.setNombre("Valentina");
                usuario3.setApellido("Muñoz");
                usuario3.setCorreo("valentina@gmail.com");
                usuario3.setPassword("1234");
                usuario3.setEstado(true);
                usuario3.setRol(rolRecepcionista);

                // Guardar los usuarios en la base de datos utilizando el repositorio de usuarios
                usuarioRepository.save(usuario1);
                usuarioRepository.save(usuario2);
                usuarioRepository.save(usuario3);
            }

            System.out.println("Datos iniciales cargados correctamente en Usuario."); // Imprimir un mensaje en la consola para indicar que los datos iniciales se han cargado correctamente
        };
    }
}