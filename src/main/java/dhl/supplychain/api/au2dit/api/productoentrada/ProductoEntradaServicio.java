package dhl.supplychain.api.au2dit.api.productoentrada;


import dhl.supplychain.api.au2dit.api.factura.Factura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoEntradaServicio {

    @Autowired
    private ProductoEntradaRepository productoEntradaRepository;


    public void guardar(ProductoEntrada productoEntrada){
        productoEntradaRepository.save(productoEntrada);
    }

    public List<ProductoEntrada> findByFactura(Factura factura){
        return productoEntradaRepository.findByFactura(factura);
    }

    public ProductoEntrada findByIdProductoEntrada(String id){
        return productoEntradaRepository.findByIdProductoEntrada(id);
    }

    public void save(ProductoEntrada pr){
        productoEntradaRepository.save(pr);
    }
}
