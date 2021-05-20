package dhl.supplychain.api.au2dit.api.orden;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    public Orden getByIdOrder(String id){
        return ordenRepository.findByIdOrden(id);
    }

    public void save(Orden orden){
        ordenRepository.save(orden);
    }

}
