package dhl.supplychain.api.au2dit.api.factura;



import dhl.supplychain.api.au2dit.api.pedimento.Pedimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, String> {

    public List<Factura> findByPedimento(Pedimento p);
    public Factura findByNumFactura(String noFactura);
    public List<Factura> findByStatus(String status);
    public List<Factura> findByEmpaqueList(String code);

}
