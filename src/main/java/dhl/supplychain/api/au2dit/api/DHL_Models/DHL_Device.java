package dhl.supplychain.api.au2dit.api.DHL_Models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
public class DHL_Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_device")
    private Long id_device;
    @Column(length = 11, nullable = false, unique = true,name = "name")
    private String name;
    @Column(length = 1000, name = "description")
    private String description;
    @Column(length = 20, nullable = false, unique = true, name = "ip_Address")
    private String ip_Address;
    @Column(length = 20, nullable = false, unique = true, name = "mac_Address")
    private String mac_Address;
    @Column(updatable = true, columnDefinition = "boolean default true", name = "status")
    private byte status;
    @Column(length = 9, nullable = false, name = "deviceType")
    private String deviceType;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private DHL_Users user;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public DHL_Device() {
    }

	public Long getId_device() {
		return id_device;
	}

	public void setId_device(Long id_device) {
		this.id_device = id_device;
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

	public String getIp_Address() {
		return ip_Address;
	}

	public void setIp_Address(String ip_Address) {
		this.ip_Address = ip_Address;
	}

	public String getMac_Address() {
		return mac_Address;
	}

	public void setMac_Address(String mac_Address) {
		this.mac_Address = mac_Address;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public DHL_Users getUser() {
		return user;
	}

	public void setUser(DHL_Users user) {
		this.user = user;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((deviceType == null) ? 0 : deviceType.hashCode());
		result = prime * result + ((id_device == null) ? 0 : id_device.hashCode());
		result = prime * result + ((ip_Address == null) ? 0 : ip_Address.hashCode());
		result = prime * result + ((mac_Address == null) ? 0 : mac_Address.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + status;
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
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
		DHL_Device other = (DHL_Device) obj;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (deviceType == null) {
			if (other.deviceType != null)
				return false;
		} else if (!deviceType.equals(other.deviceType))
			return false;
		if (id_device == null) {
			if (other.id_device != null)
				return false;
		} else if (!id_device.equals(other.id_device))
			return false;
		if (ip_Address == null) {
			if (other.ip_Address != null)
				return false;
		} else if (!ip_Address.equals(other.ip_Address))
			return false;
		if (mac_Address == null) {
			if (other.mac_Address != null)
				return false;
		} else if (!mac_Address.equals(other.mac_Address))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (status != other.status)
			return false;
		if (updatedAt == null) {
			if (other.updatedAt != null)
				return false;
		} else if (!updatedAt.equals(other.updatedAt))
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
		return "DHL_Device [id_device=" + id_device + ", name=" + name + ", description=" + description
				+ ", ip_Address=" + ip_Address + ", mac_Address=" + mac_Address + ", status=" + status + ", deviceType="
				+ deviceType + ", user=" + user + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
    
}