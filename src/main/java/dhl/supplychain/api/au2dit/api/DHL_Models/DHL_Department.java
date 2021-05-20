package dhl.supplychain.api.au2dit.api.DHL_Models;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;

@Entity
@Table(name="DHL_Department")
public class DHL_Department {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_Department")
    protected int id_Department;
	
    @Column(name = "name")
    protected String name;

    @Column(name = "description")
    protected String description;
    
    @OneToMany(mappedBy="department")
	@JsonIgnore
    protected Set<DHL_Users> user;

	@Column(name = "status")
	protected boolean status;

	public boolean isStatus() {
		return this.status;
	}

	public boolean getStatus() {
		return this.status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getId_Department() {
		return id_Department;
	}

	public void setId_Department(int id_Department) {
		this.id_Department = id_Department;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<DHL_Users> getUser() {
		return user;
	}

	public void setUser(Set<DHL_Users> user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + id_Department;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DHL_Department other = (DHL_Department) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id_Department != other.id_Department)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DHL_Department [id_Department=" + id_Department + ", name=" + name + ", description=" + description
				+ ", user=" + user + "]";
	}
}