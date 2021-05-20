package dhl.supplychain.api.au2dit.api.factura;


import dhl.supplychain.api.au2dit.api.pedimento.Pedimento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaService {
    @Autowired
    private FacturaRepository facturaRepository;

    public List<Factura> allFacturas(){
        return facturaRepository.findAll();
    }

    public List<Factura> findByPedimento(Pedimento pedimento){
        return facturaRepository.findByPedimento(pedimento);
    }

    public Factura findByNum(String noFactura){
        return facturaRepository.findByNumFactura(noFactura);
    }

    public List<Factura> findByRastreo(String code){
        return facturaRepository.findByEmpaqueList(code);
    }

    public List<Factura> findByStatus(String status){
        return facturaRepository.findByStatus(status);
    }

    public void guardar(Factura factura){
        facturaRepository.save(factura);
    }


}
