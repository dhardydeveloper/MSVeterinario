package cl.duoc.atencionClinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AtencionClinicaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtencionClinicaApplication.class, args);
    }

    /*
     * RestTemplate permite que Atención Clínica llame a Agenda.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}