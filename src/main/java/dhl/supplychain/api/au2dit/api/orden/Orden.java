package dhl.supplychain.api.au2dit.api.orden;



import dhl.supplychain.api.au2dit.api.productoentrada.ProductoEntrada;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Orden {
    @Id
    protected String idOrden;
    protected String numeroOrden;
    protected int numeroDespacho;

    @OneToMany(mappedBy="orden",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected Set<ProductoEntrada> productoEntradas;

    public Orden() {
    }

    public String getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(String idOrden) {
        this.idOrden = idOrden;
    }

    public Set<ProductoEntrada> getProductoEntradas() {
        return productoEntradas;
    }

    public void setProductoEntradas(Set<ProductoEntrada> productoEntradas) {
        this.productoEntradas = productoEntradas;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public int getNumeroDespacho() {
        return numeroDespacho;
    }

    public void setNumeroDespacho(int numeroDespacho) {
        this.numeroDespacho = numeroDespacho;
    }
}
