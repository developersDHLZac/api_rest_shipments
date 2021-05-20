package dhl.supplychain.api.au2dit.api.orden;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, String> {
    public Orden findByIdOrden(String id);

}
