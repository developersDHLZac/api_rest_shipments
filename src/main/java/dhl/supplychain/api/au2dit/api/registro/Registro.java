package dhl.supplychain.api.au2dit.api.registro;



import dhl.supplychain.api.au2dit.api.models.User;
import dhl.supplychain.api.au2dit.api.productoentrada.ProductoEntrada;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Registro {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="pe_id", nullable=false)
    private ProductoEntrada productoEntrada;

    private Date fechaEntrada;


    private String descripcion;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ProductoEntrada getProductoEntrada() {
        return productoEntrada;
    }

    public void setProductoEntrada(ProductoEntrada productoEntrada) {
        this.productoEntrada = productoEntrada;
    }

    public Date getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(Date fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
