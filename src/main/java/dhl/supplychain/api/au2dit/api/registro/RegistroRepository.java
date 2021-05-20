package dhl.supplychain.api.au2dit.api.registro;


import dhl.supplychain.api.au2dit.api.models.User;
import dhl.supplychain.api.au2dit.api.productoentrada.ProductoEntrada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroRepository  extends JpaRepository<Registro,Long> {
    public Registro findByProductoEntrada(ProductoEntrada pe);
    public Registro findByUser(User user);

}
