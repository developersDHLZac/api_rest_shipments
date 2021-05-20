package dhl.supplychain.api.au2dit.api.DHL_Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dhl.supplychain.api.au2dit.api.DHL_Models.DHL_Users;
import dhl.supplychain.api.au2dit.api.DHL_Repository.RolRepository;
import dhl.supplychain.api.au2dit.api.DHL_Repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;
    private RolRepository rolRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RolRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.rolRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public DHL_Users findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public DHL_Users findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
    
    public DHL_Users findById(int id) {
    	return userRepository.findById(id);
    }

    public DHL_Users saveUser(DHL_Users user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setStatus('C');
        return userRepository.save(user);
    }

    public DHL_Users saveEditedUser(DHL_Users user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        LocalDate futureDate = LocalDate.now().plusMonths(12);
        user.setPasswordExpireDate(futureDate);
        return userRepository.save(user);
    }
    
    public DHL_Users updateUser(DHL_Users user) {
    	String pass=user.getPassword();
    	if(pass!= null && pass!= "") {
    		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    	}else {
    		user.setPassword(user.getNewPassword());
    	}
    	return userRepository.save(user);
    }

    public void deactiveUser(DHL_Users user){
        user.setStatus('D');
        userRepository.save(user);
    }

    public void deleteUser(int id){
    	DHL_Users user= userRepository.findById(id);
    	user.setStatus('D');
    	userRepository.save(user);
    }

    public List<DHL_Users> allUsers(){
        return userRepository.findAll();
    }
    
    public List<DHL_Users> findByStatus(){
        return userRepository.findAllByStatus();
    }

    public DHL_Users findUserById(int id)
    {
        return userRepository.findById(id);
    }

    public DHL_Users findUserByBarcode(String barcode)
    {
        return userRepository.findByBarcode(barcode);
    }
}
