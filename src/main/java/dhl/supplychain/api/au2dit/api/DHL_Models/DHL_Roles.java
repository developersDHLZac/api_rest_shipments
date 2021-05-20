package dhl.supplychain.api.au2dit.api.DHL_Models;

import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "DHL_Roles")
public class DHL_Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Role")
    protected int id_Role;
    @Column(name = "name")
    protected String name;
    @Column(name = "description")
    protected String description;

    @Column(name = "status")
    protected byte status;

    @OneToMany(mappedBy="rol")
    protected Set<DHL_Users> user;


    public int getId_Role() {
        return this.id_Role;
    }

    public void setId_Role(int id_Role) {
        this.id_Role = id_Role;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte getStatus() {
        return this.status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public Set<DHL_Users> getUser() {
        return this.user;
    }

    public void setUser(Set<DHL_Users> user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return name;
    }
}