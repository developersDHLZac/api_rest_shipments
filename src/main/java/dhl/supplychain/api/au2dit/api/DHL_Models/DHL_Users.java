package dhl.supplychain.api.au2dit.api.DHL_Models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "DHL_Users")
public class DHL_Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_User")
    private int id;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "name")
    private String name;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "barcode_LoginAccess")
    private String barcode_LoginAccess;
    @Column(name = "status")
    private char status;

    // @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
	private Date created_at;
	
	// @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
	private Date updated_at;

    @Transient
    private String newPassword;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="fk_Id_Rol", referencedColumnName = "id_Role",nullable=false)
    private DHL_Roles rol;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="fk_Id_Department", referencedColumnName = "id_Department",nullable=false)
    private DHL_Department department;

    private LocalDate passwordExpireDate;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBarcode_LoginAccess() {
        return this.barcode_LoginAccess;
    }

    public void setBarcode_LoginAccess(String barcode_LoginAccess) {
        this.barcode_LoginAccess = barcode_LoginAccess;
    }

    public char getStatus() {
        return this.status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public Date getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public DHL_Roles getRol() {
        return this.rol;
    }

    public void setRol(DHL_Roles rol) {
        this.rol = rol;
    }

    public LocalDate getPasswordExpireDate() {
        return this.passwordExpireDate;
    }

    public void setPasswordExpireDate(LocalDate passwordExpireDate) {
        this.passwordExpireDate = passwordExpireDate;
    }

    
    public DHL_Department getDepartment() {
		return department;
	}

	public void setDepartment(DHL_Department department) {
		this.department = department;
	}

	@Override
	public String toString() {
		return "DHL_Users [id=" + id + ", userName=" + userName + ", email=" + email + ", password=" + password
				+ ", name=" + name + ", lastName=" + lastName + ", barcode_LoginAccess=" + barcode_LoginAccess
				+ ", status=" + status + ", created_at=" + created_at + ", updated_at=" + updated_at + ", newPassword="
				+ newPassword + ", rol=" + rol + ", department=" + department
				+ ", passwordExpireDate=" + passwordExpireDate + "]";
	}

}
