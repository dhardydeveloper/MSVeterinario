package cl.duoc.registro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.registro.dto.RegistroDTO;
import cl.duoc.registro.model.Cliente;
import cl.duoc.registro.model.Mascota;
import cl.duoc.registro.repository.ClienteRepository;
import cl.duoc.registro.repository.MascotaRepository;

@Service
public class RegistroService { // Servicio que contiene la lógica de negocio para manejar clientes y mascotas

    @Autowired
    private ClienteRepository clienteRepository; // Inyectamos el repositorio de Cliente para acceder a la base de datos

    @Autowired
    private MascotaRepository mascotaRepository; // Inyectamos el repositorio de Mascota para acceder a la base de datos

  

    // CLIENTE //

    // •  Listar clientes
    public List<Cliente> listarClientes() { // Método para listar todos los clientes, devuelve una lista de objetos Cliente
        return clienteRepository.findAll(); // Utilizamos el método findAll() del repositorio para obtener todos los clientes de la base de datos
    }

    // •  Buscar cliente por ID
    public Cliente buscarClientePorId(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
    }

    // •  Buscar cliente por RUT
    public Cliente buscarClientePorRut(String rut) {
        return clienteRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con rut: " + rut));
    }

    // •  Crear cliente
    public Cliente guardarCliente(Cliente cliente) { // Método para guardar un nuevo cliente, recibe un objeto Cliente y lo guarda en la base de datos
        return clienteRepository.save(cliente); // Utilizamos el método save() del repositorio para guardar el cliente en la base de datos, devuelve el cliente guardado con su ID generado
    }

    // •  Actualizar cliente
    public Cliente actualizarCliente(Integer id, Cliente cliente) {

        Cliente clienteExistente = buscarClientePorId(id);

        clienteExistente.setRut(cliente.getRut());
        clienteExistente.setNombre(cliente.getNombre());
        clienteExistente.setApellido(cliente.getApellido());
        clienteExistente.setTelefono(cliente.getTelefono());
        clienteExistente.setCorreo(cliente.getCorreo());
        clienteExistente.setDireccion(cliente.getDireccion());
        clienteExistente.setComuna(cliente.getComuna());
        clienteExistente.setRegion(cliente.getRegion());

        return clienteRepository.save(clienteExistente);
    }

    // •  Eliminar cliente
    public void eliminarCliente(Integer id) {

        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con id: " + id);
        }

        clienteRepository.deleteById(id);
    }



    // MASCOTA //

    // •  Listar mascotas
    public List<Mascota> listarMascotas() {
        return mascotaRepository.findAll(); // retorna una lista de todas las mascotas registradas en la base de datos utilizando el método findAll() del repositorio de Mascota
    }

    // •  Buscar mascota por ID
    public Mascota buscarMascotaPorId(Integer id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con id: " + id));
    }

    // •  Buscar mascotas por cliente
    public List<Mascota> buscarMascotasPorCliente(Integer clienteId) {
        return mascotaRepository.findByClienteId(clienteId);
    }

    // •  Buscar mascotas por nombre
    public List<Mascota> buscarMascotasPorNombre(String nombre) {
        return mascotaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // •  Buscar mascota por chip
    public Mascota buscarMascotaPorNumeroChip(String numeroChip) {
        return mascotaRepository.findByNumeroChip(numeroChip)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con número de chip: " + numeroChip));
    }

    // •  Crear mascota
    public Mascota guardarMascota(Mascota mascota) {

    // Primero validamos que el cliente exista > Una mascota no puede registrarse sin dueño.
        Integer idCliente = mascota.getCliente().getId(); // Obtenemos el ID del cliente desde el objeto mascota que recibimos como parámetro. Asumimos que el JSON de la mascota incluye un objeto cliente con su ID.

        Cliente clienteExistente = buscarClientePorId(idCliente);

        mascota.setCliente(clienteExistente);

        return mascotaRepository.save(mascota); // Guardamos la mascota en la base de datos utilizando el método save() del repositorio de Mascota, devuelve la mascota guardada con su ID generado
    }

    // •  Actualizar mascota
    public Mascota actualizarMascota(Integer id, Mascota mascota) {

        // Validamos que la mascota exista antes de actualizarla
        Mascota mascotaExistente = buscarMascotaPorId(id);

        // Actualizamos los campos de la mascota existente con los valores del objeto mascota que recibimos como parámetro. Solo actualizamos los campos básicos, la relación con el cliente se maneja aparte.
        mascotaExistente.setNombre(mascota.getNombre());
        mascotaExistente.setEspecie(mascota.getEspecie());
        mascotaExistente.setRaza(mascota.getRaza());
        mascotaExistente.setEdad(mascota.getEdad());
        mascotaExistente.setSexo(mascota.getSexo());
        mascotaExistente.setColor(mascota.getColor());
        mascotaExistente.setPeso(mascota.getPeso());
        mascotaExistente.setNumeroChip(mascota.getNumeroChip());

        // Si el JSON de la mascota incluye un objeto cliente con su ID, actualizamos la relación con el cliente. Esto permite cambiar el dueño de la mascota si es necesario.
        if (mascota.getCliente() != null && mascota.getCliente().getId() != null) { // esta condición verifica que el objeto mascota que recibimos como parámetro incluye un objeto cliente con un ID válido. Si es así, procedemos a actualizar la relación con el cliente.
            Cliente clienteExistente = buscarClientePorId(mascota.getCliente().getId()); // buscamos el cliente existente en la base de datos utilizando el ID proporcionado en el objeto mascota. Esto asegura que el cliente al que queremos asociar la mascota realmente exista.
            mascotaExistente.setCliente(clienteExistente); // actualizamos la relación de la mascota existente con el cliente encontrado. Esto permite cambiar el dueño de la mascota si es necesario.
        }

        return mascotaRepository.save(mascotaExistente); // Guardamos la mascota actualizada en la base de datos utilizando el método save() del repositorio de Mascota, devuelve la mascota actualizada
    }

    // •  Eliminar mascota
    public void eliminarMascota(Integer id) {

        if (!mascotaRepository.existsById(id)) { // Validamos que la mascota exista antes de eliminarla, utilizando el método existsById() del repositorio de Mascota para verificar si existe una mascota con el ID proporcionado. Si no existe, lanzamos una excepción indicando que la mascota no fue encontrada.
            throw new RuntimeException("Mascota no encontrada con id: " + id);
        }

        mascotaRepository.deleteById(id); // Si la mascota existe, procedemos a eliminarla utilizando el método deleteById() del repositorio de Mascota, que elimina la mascota con el ID proporcionado de la base de datos.
    }



    // DTO: CLIENTE + MASCOTA //

    public RegistroDTO obtenerRegistroDTO(Integer idMascota) { // Método para obtener un DTO que combine los datos de un cliente y su mascota, recibe el ID de la mascota como parámetro

        Mascota mascota = buscarMascotaPorId(idMascota); // Primero buscamos la mascota utilizando el método buscarMascotaPorId() que ya tenemos implementado. Esto nos devuelve un objeto Mascota que incluye la información de la mascota y su relación con el cliente.
        Cliente cliente = mascota.getCliente(); // Luego obtenemos el cliente asociado a la mascota utilizando el método getCliente() del objeto Mascota. Esto nos devuelve un objeto Cliente con la información del dueño de la mascota.

        
        RegistroDTO dto = new RegistroDTO(); // Creamos una instancia del DTO que vamos a llenar con los datos del cliente y la mascota

        // Datos cliente
        dto.setIdCliente(cliente.getId());
        dto.setRutCliente(cliente.getRut());
        dto.setNombreCliente(cliente.getNombre());
        dto.setApellidoCliente(cliente.getApellido());
        dto.setTelefonoCliente(cliente.getTelefono());
        dto.setCorreoCliente(cliente.getCorreo());
        dto.setDireccionCliente(cliente.getDireccion());
        dto.setComunaCliente(cliente.getComuna());
        dto.setRegionCliente(cliente.getRegion());

        // Datos mascota
        dto.setIdMascota(mascota.getId());
        dto.setNombreMascota(mascota.getNombre());
        dto.setEspecieMascota(mascota.getEspecie());
        dto.setRazaMascota(mascota.getRaza());
        dto.setEdadMascota(mascota.getEdad());
        dto.setSexoMascota(mascota.getSexo());
        dto.setColorMascota(mascota.getColor());
        dto.setPesoMascota(mascota.getPeso());
        dto.setNumeroChipMascota(mascota.getNumeroChip());

        return dto; // Retornamos el DTO con los datos combinados del cliente y la mascota
    }
}