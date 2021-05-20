package dhl.supplychain.api.au2dit.api.pedimento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedimentoRepository extends JpaRepository<Pedimento, String> {

    public Pedimento findByNoPedimento(String noPedimento);
    public List<Pedimento> findByStatus(String noPedimento);
}
