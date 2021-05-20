package dhl.supplychain.api.au2dit.api.pedimento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PedimentoService {
    @Autowired
    private PedimentoRepository pedimentoRepository;

    public List<Pedimento> listAll() {
        return pedimentoRepository.findAll();
    }

    public Pedimento findByNoPedimento(String noPedimento){
        return pedimentoRepository.findByNoPedimento(noPedimento);
    }

    public List<Pedimento> findByStatus(String status){
        return pedimentoRepository.findByStatus(status);
    }
    public void guardar(Pedimento pedimento){
        pedimentoRepository.save(pedimento);
    }

}
