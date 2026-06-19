package cl.duoc.usuario.repository; 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; //Importa la clase Rol desde el paquete model

import cl.duoc.usuario.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> { //JpaRepository para realizar operaciones CRUD en la entidad Rol
}