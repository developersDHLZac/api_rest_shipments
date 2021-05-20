package dhl.supplychain.api.au2dit.api.registro;



import dhl.supplychain.api.au2dit.api.productoentrada.ProductoEntrada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistroService {

    @Autowired
    private RegistroRepository registroRepository;

    public Registro findByProductoEntrada(ProductoEntrada pe){
        return registroRepository.findByProductoEntrada(pe);
    }

    public void addRegistro(Registro registro){
        registroRepository.save(registro);
    }
}
