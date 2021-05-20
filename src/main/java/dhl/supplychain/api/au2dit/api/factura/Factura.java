package dhl.supplychain.api.au2dit.api.factura;



import dhl.supplychain.api.au2dit.api.pedimento.Pedimento;
import dhl.supplychain.api.au2dit.api.productoentrada.ProductoEntrada;
import dhl.supplychain.api.au2dit.api.token.Token;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Factura {
    @Id
    protected String numFactura;
    //0 - Ingreseda - Pendiente - rojo
    //1 - En proceso - En proceso- amarillo
    //2 - Auditada - Temrminada  Todo bien - verde
    //3 - Auditada- Terminada LPS Anomalia - azul
    protected String status;
    protected String referenciaCliente;
    protected String empaqueList;
    protected String empaqueNo;

    private String auditor;

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public Set<Token> getToken() {
        return token;
    }

    public void setToken(Set<Token> token) {
        this.token = token;
    }


    private Date startTime;

    private Date endTime;

    private long totalTime;

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "noPedimento", nullable = false)
    protected Pedimento pedimento;

    @OneToMany(mappedBy="factura",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected Set<ProductoEntrada> productoEntradas;

    @OneToMany(mappedBy="factura",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Token> token;


    public Set<ProductoEntrada> getProductoEntradas() {
        return productoEntradas;
    }

    public void setProductoEntradas(Set<ProductoEntrada> productoEntradas) {
        this.productoEntradas = productoEntradas;
    }

    public String getEmpaqueList() {
        return empaqueList;
    }

    public void setEmpaqueList(String empaqueList) {
        this.empaqueList = empaqueList;
    }

    public String getEmpaqueNo() {
        return empaqueNo;
    }

    public void setEmpaqueNo(String empaqueNo) {
        this.empaqueNo = empaqueNo;
    }

    public String getReferenciaCliente() {
        return referenciaCliente;
    }

    public void setReferenciaCliente(String referenciaCliente) {
        this.referenciaCliente = referenciaCliente;
    }



    public Factura() {
    }

    public String getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(String numFactura) {
        this.numFactura = numFactura;
    }


    public Pedimento getPedimento() {
        return pedimento;
    }

    public void setPedimento(Pedimento pedimento) {
        this.pedimento = pedimento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Factura: " + numFactura;
    }
}
