package dhl.supplychain.api.au2dit.api.repository;

import dhl.supplychain.api.au2dit.api.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByRole(String role);
	Role findById(int id);
}
