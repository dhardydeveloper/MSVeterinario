package cl.duoc.pago.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.pago.model.TipoPago;
import cl.duoc.pago.repository.TipoPagoRepository;

@Service
public class TipoPagoService {

    @Autowired
    private TipoPagoRepository tipoPagoRepository;


    // •  Listar tipo pagos
    public List<TipoPago> listar() {
        return tipoPagoRepository.findAll();
    }

    // •  Buscar tipo pago por ID
    public TipoPago buscarPorId(Integer id) {
        return tipoPagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de pago no encontrado con id: " + id));
    }

    // •  Crear tipo pago
    public TipoPago guardar(TipoPago tipoPago) {

        validarTipoPago(tipoPago);

        return tipoPagoRepository.save(tipoPago);
    }

    // •  Actualizar tipo pago
    public TipoPago actualizar(Integer id, TipoPago tipoPago) {

        TipoPago tipoExistente = buscarPorId(id);

        validarTipoPago(tipoPago);

        tipoExistente.setNombreTipoPago(tipoPago.getNombreTipoPago());
        tipoExistente.setDescripcion(tipoPago.getDescripcion());

        return tipoPagoRepository.save(tipoExistente);
    }

    // •  Eliminar tipo pago
    public void eliminar(Integer id) {

        if (!tipoPagoRepository.existsById(id)) {
            throw new RuntimeException("Tipo de pago no encontrado con id: " + id);
        }

        tipoPagoRepository.deleteById(id);
    }

    // validaciones
    private void validarTipoPago(TipoPago tipoPago) {

        if (tipoPago.getNombreTipoPago() == null || tipoPago.getNombreTipoPago().isBlank()) {
            throw new RuntimeException("El nombre del tipo de pago es obligatorio");
        }

        if (tipoPago.getDescripcion() == null || tipoPago.getDescripcion().isBlank()) {
            throw new RuntimeException("La descripción del tipo de pago es obligatoria");
        }
    }
}