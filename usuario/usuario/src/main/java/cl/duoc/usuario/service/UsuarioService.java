package cl.duoc.usuario.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service;

import cl.duoc.usuario.dto.UsuarioDTO;
import cl.duoc.usuario.model.Rol;
import cl.duoc.usuario.model.Usuario;
import cl.duoc.usuario.repository.RolRepository;
import cl.duoc.usuario.repository.UsuarioRepository;

@Service 
public class UsuarioService {

    @Autowired // @Autowired para inyectar la dependencia del repositorio de Usuario
    private UsuarioRepository usuarioRepository;

    @Autowired // @Autowired para inyectar la dependencia del repositorio de Rol, necesario para validar que el rol exista antes de guardar o actualizar un usuario
    private RolRepository rolRepository;


    // •  Listar usuarios
    public List<Usuario> listar() { 
        return usuarioRepository.findAll(); //findAll para obtener todos los usuarios de la base de datos
    }

    // •  Buscar usuario por id
    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id) 
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id)); // orElseThrow para lanzar una excepción si el usuario no se encuentra por su ID
    }

    // •  Buscar usuario por correo electrónico
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
    }

    // •  Crear usuario
    public Usuario guardar(Usuario usuario) {

        // Validar los datos del usuario antes de guardarlo
        validarUsuario(usuario);

       
        // Validar que el rol exista antes de guardar el usuario
        Integer idRol = usuario.getRol().getIdRol();

        // Buscar el rol en la base de datos, si no existe se lanza una excepción
        Rol rolExistente = rolRepository.findById(idRol) 
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + idRol)); 
        usuario.setRol(rolExistente); // asignar el rol existente al usuario antes de guardarlo

        
        // Si el estado no se especifica, se asume que el usuario está activo por defecto
        if (usuario.getEstado() == null) {
            usuario.setEstado(true); 
        }

        return usuarioRepository.save(usuario); //save para guardar el usuario en la base de datos
    }

    // •  Actualizar usuario
    public Usuario actualizar(Integer id, Usuario usuario) {

        Usuario usuarioExistente = buscarPorId(id);

        // Validar los datos del usuario antes de actualizarlo
        validarUsuario(usuario);

        // Actualizar los campos del usuario existente con los datos del usuario recibido
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setApellido(usuario.getApellido());
        usuarioExistente.setCorreo(usuario.getCorreo());
        usuarioExistente.setPassword(usuario.getPassword());
        usuarioExistente.setEstado(usuario.getEstado());

        // Validar que el rol exista antes de actualizar el usuario
        if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) { // esta fila dice que si el rol del usuario recibido no es nulo y el ID del rol no es nulo, entonces se procede a validar que el rol exista en la base de datos antes de actualizar el usuario

            Integer idRol = usuario.getRol().getIdRol(); //Obtener el ID del rol del usuario recibido

            Rol rolExistente = rolRepository.findById(idRol)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + idRol));

            usuarioExistente.setRol(rolExistente);
        }

        return usuarioRepository.save(usuarioExistente);
    }

    // •  Eliminar un usuario
    public void eliminar(Integer id) {

        // Validar que el usuario exista antes de eliminarlo
        if (!usuarioRepository.existsById(id)) { // existsById para verificar si el usuario existe por su ID
            throw new RuntimeException("Usuario no encontrado con id: " + id); // throw new RuntimeException para lanzar una excepción si el usuario no existe
        }

        usuarioRepository.deleteById(id); // deleteById para eliminar el usuario por su ID
    }

    // •   Obtener DTO usuario
    public UsuarioDTO obtenerUsuarioDTO(Integer id) { 

        Usuario usuario = buscarPorId(id);

        return convertirADTO(usuario); // convertir el usuario a DTO para exponer solo los datos necesarios en la respuesta de la API
    }

    // Método para convertir un Usuario a UsuarioDTO
    public UsuarioDTO convertirADTO(Usuario usuario) { // convertir un Usuario a UsuarioDTO para exponer solo los datos necesarios en la respuesta de la API

        UsuarioDTO dto = new UsuarioDTO(); // Crear una instancia de UsuarioDTO para mapear los datos del Usuario

        // Mapear los campos del Usuario al UsuarioDTO
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        dto.setEstado(usuario.getEstado());

        // Mapear los campos del Rol al UsuarioDTO
        dto.setIdRol(usuario.getRol().getIdRol());
        dto.setNombreRol(usuario.getRol().getNombreRol());

        return dto; // Retornar el UsuarioDTO con los datos mapeados
    }

    // Validación de datos del usuario
    private void validarUsuario(Usuario usuario) {

        // Validar que el rol exista antes de validar los datos del usuario, ya que el ID del rol es un campo obligatorio para el usuario
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del usuario es obligatorio");
        }

        // Validar que el apellido no sea nulo o esté vacío
        if (usuario.getApellido() == null || usuario.getApellido().isBlank()) {
            throw new RuntimeException("El apellido del usuario es obligatorio");
        }

        // Validar que el correo no sea nulo o esté vacío
        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            throw new RuntimeException("El correo del usuario es obligatorio");
        }

        // Validar que la contraseña no sea nula o esté vacía
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña del usuario es obligatoria");
        }

        // Validar que se haya especificado un rol válido (ID del rol no nulo)
        if (usuario.getRol() == null || usuario.getRol().getIdRol() == null) {
            throw new RuntimeException("Debe indicar un rol válido");
        }
    }
}