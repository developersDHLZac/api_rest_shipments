package dhl.supplychain.api.au2dit.api.DHL_Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dhl.supplychain.api.au2dit.api.DHL_Models.DHL_Users;

@Repository
public interface UserRepository extends JpaRepository<DHL_Users, Integer>{
    DHL_Users findByEmail(String email);
	DHL_Users findByUserName(String userName);
    Void deleteById(int id);
    DHL_Users findById(int id);
    @Query(value="SELECT * FROM users where active is not null", nativeQuery = true)
    List<DHL_Users> findAllByStatus();
    @Query(value="SELECT * FROM dhl_users WHERE barcode_login_access = :barcode", nativeQuery = true)
    DHL_Users findByBarcode(@Param(value = "barcode") String barcode);
    
}
