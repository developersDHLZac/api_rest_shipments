package dhl.supplychain.api.au2dit.api.pedimento;





import dhl.supplychain.api.au2dit.api.factura.Factura;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Pedimento {
    @Id
    protected String noPedimento;
    //0 - Ingreseda - Pendiente - rojo
    //1 - En proceso - En proceso- amarillo
    //2 - Auditada - Temrminada - verde
    protected String status;

    protected Date fechaIngreso;

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    @OneToMany(mappedBy="pedimento",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Factura> facturas;

    public String getNoPedimento() {
        return noPedimento;
    }

    public void setNoPedimento(String noPedimento) {
        this.noPedimento = noPedimento;
    }

    public Set<Factura> getFacturas() {
        return facturas;
    }

    public void setFacturas(Set<Factura> facturas) {
        this.facturas = facturas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Pedimento :" + noPedimento + " Estatus: " + status;
    }
}
