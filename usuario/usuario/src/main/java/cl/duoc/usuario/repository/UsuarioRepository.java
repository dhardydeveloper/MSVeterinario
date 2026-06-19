package cl.duoc.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Importa la clase Usuario desde el paquete model

import cl.duoc.usuario.model.Usuario;

@Repository 
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> { //JpaRepository para realizar operaciones CRUD en la entidad Usuario


    //Método personalizado para buscar un usuario por su correo electrónico
    Optional<Usuario> findByCorreo(String correo);
}