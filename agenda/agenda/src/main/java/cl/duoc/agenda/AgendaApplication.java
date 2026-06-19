package cl.duoc.agenda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AgendaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgendaApplication.class, args);
    }

    /*
     * RestTemplate permite que este microservicio llame a otros microservicios.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}