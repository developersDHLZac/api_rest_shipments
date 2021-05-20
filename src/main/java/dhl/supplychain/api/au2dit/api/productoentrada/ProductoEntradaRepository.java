package dhl.supplychain.api.au2dit.api.productoentrada;


import dhl.supplychain.api.au2dit.api.factura.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoEntradaRepository extends JpaRepository<ProductoEntrada,String> {
    public List<ProductoEntrada> findByFactura(Factura factura);
    public ProductoEntrada findByIdProductoEntrada(String id);

}
