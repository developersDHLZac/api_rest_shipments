package dhl.supplychain.api.au2dit.api.models;



import java.util.Set;

public class Facture {
    protected String numFactura;
    protected String status;
    protected String referenciaCliente;
    protected String empaqueList;
    protected String empaqueNo;
    protected String auditor;
    protected Set<Item> items;
    protected String noPedimento;


    public Facture() {
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(String numFactura) {
        this.numFactura = numFactura;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferenciaCliente() {
        return referenciaCliente;
    }

    public void setReferenciaCliente(String referenciaCliente) {
        this.referenciaCliente = referenciaCliente;
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

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public String getNoPedimento() {
        return noPedimento;
    }

    public void setNoPedimento(String noPedimento) {
        this.noPedimento = noPedimento;
    }
}
