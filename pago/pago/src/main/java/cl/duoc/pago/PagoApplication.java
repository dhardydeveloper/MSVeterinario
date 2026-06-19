package cl.duoc.pago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PagoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PagoApplication.class, args);
    }

    //RestTemplate > permite que Pago consulte Atención Clínica.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}