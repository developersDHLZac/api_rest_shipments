package dhl.supplychain.api.au2dit.api.DHL_Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dhl.supplychain.api.au2dit.api.DHL_Models.DHL_Roles;

@Repository
public interface RolRepository extends JpaRepository<DHL_Roles, Integer> {
    @Query(value = "SELECT * FROM dhl_roles WHERE status = 1", nativeQuery = true)
    public List<DHL_Roles> findAllByStatusActive();
    @Query(value = "SELECT * FROM dhl_roles WHERE id_role = :id", nativeQuery = true)
    public DHL_Roles findRolById(@Param("id") int id);
}
