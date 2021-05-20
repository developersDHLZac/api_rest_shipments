package dhl.supplychain.api.au2dit.api.token;


import dhl.supplychain.api.au2dit.api.factura.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Integer> {
    public Token findByFactura(Factura factura);
}
