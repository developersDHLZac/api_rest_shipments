package dhl.supplychain.api.au2dit.api.token;


import dhl.supplychain.api.au2dit.api.factura.Factura;

import javax.persistence.*;

@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String token;


    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "numFactura", nullable = true)
    private Factura factura;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }
}
