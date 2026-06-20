package cl.duoc.atencionClinica.Service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.atencionClinica.repository.AtencionRepository;
import cl.duoc.atencionClinica.repository.BoxRepository;
import cl.duoc.atencionClinica.repository.TipoAtencionRepository;
import cl.duoc.atencionClinica.service.AtencionService;


@ExtendWith(MockitoExtension.class)
public class AtencionServiceTest {

    @Mock
    private AtencionRepository atencionRepository; // repositorio simulado

    @Mock
    private TipoAtencionRepository tipoAtencionRepository; 

    @Mock
    private BoxRepository boxRepository; 

    @Mock
    private AtencionService atencionService; 

    


}
