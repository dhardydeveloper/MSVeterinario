package cl.duoc.pago.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.pago.model.Pago;
import cl.duoc.pago.model.TipoPago;
import cl.duoc.pago.repository.PagoRepository;
import cl.duoc.pago.repository.TipoPagoRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner cargarDatosIniciales(
            TipoPagoRepository tipoPagoRepository,
            PagoRepository pagoRepository) {

        return args -> {

            if (tipoPagoRepository.count() == 0) {

                TipoPago tipo1 = new TipoPago();
                tipo1.setNombreTipoPago("EFECTIVO");
                tipo1.setDescripcion("Pago realizado en efectivo");

                TipoPago tipo2 = new TipoPago();
                tipo2.setNombreTipoPago("TARJETA");
                tipo2.setDescripcion("Pago realizado con tarjeta bancaria");

                TipoPago tipo3 = new TipoPago();
                tipo3.setNombreTipoPago("TRANSFERENCIA");
                tipo3.setDescripcion("Pago realizado por transferencia bancaria");

                tipoPagoRepository.save(tipo1);
                tipoPagoRepository.save(tipo2);
                tipoPagoRepository.save(tipo3);
            }

            if (pagoRepository.count() == 0) {

                TipoPago tipo1 = tipoPagoRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("TipoPago 1 no encontrado"));

                TipoPago tipo2 = tipoPagoRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("TipoPago 2 no encontrado"));

                TipoPago tipo3 = tipoPagoRepository.findById(3)
                        .orElseThrow(() -> new RuntimeException("TipoPago 3 no encontrado"));

                Pago pago1 = new Pago();
                pago1.setIdAtencion(1);
                pago1.setTipoPago(tipo1);
                pago1.setMonto(15000.0);
                pago1.setFechaPago(LocalDate.of(2026, 5, 9));
                pago1.setEstadoPago("PAGADO");

                Pago pago2 = new Pago();
                pago2.setIdAtencion(2);
                pago2.setTipoPago(tipo2);
                pago2.setMonto(22000.0);
                pago2.setFechaPago(LocalDate.of(2026, 5, 9));
                pago2.setEstadoPago("PAGADO");

                Pago pago3 = new Pago();
                pago3.setIdAtencion(3);
                pago3.setTipoPago(tipo3);
                pago3.setMonto(30000.0);
                pago3.setFechaPago(LocalDate.of(2026, 5, 9));
                pago3.setEstadoPago("PENDIENTE");

                pagoRepository.save(pago1);
                pagoRepository.save(pago2);
                pagoRepository.save(pago3);
            }

            System.out.println("Datos iniciales cargados correctamente en Pago.");
        };
    }
}