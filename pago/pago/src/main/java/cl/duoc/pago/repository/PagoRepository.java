package cl.duoc.pago.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.pago.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    List<Pago> findByIdAtencion(Integer idAtencion); // Busca todos los pagos asociados a una atención clínica.

    List<Pago> findByEstadoPago(String estadoPago); // Busca pagos según su estado.

    List<Pago> findByFechaPago(LocalDate fechaPago); // Busca pagos por fecha.
}