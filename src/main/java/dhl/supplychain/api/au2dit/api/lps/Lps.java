package dhl.supplychain.api.au2dit.api.lps;



import dhl.supplychain.api.au2dit.api.productoentrada.ProductoEntrada;

import javax.persistence.*;

@Entity
public class Lps {

    @Id
    private String idLps;

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "idProductoEntrada", nullable = true)
    private ProductoEntrada productoEntrada;

    public Lps() {
    }

    public ProductoEntrada getProductoEntrada() {
        return productoEntrada;
    }

    public void setProductoEntrada(ProductoEntrada productoEntrada) {
        this.productoEntrada = productoEntrada;
    }

    public String getIdLps() {
        return idLps;
    }

    public void setIdLps(String idLps) {
        this.idLps = idLps;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
