package dhl.supplychain.api.au2dit.api.productoentrada;


import dhl.supplychain.api.au2dit.api.factura.Factura;
import dhl.supplychain.api.au2dit.api.lps.Lps;
import dhl.supplychain.api.au2dit.api.orden.Orden;

import javax.persistence.*;
import java.util.Set;

@Entity
public class ProductoEntrada {

    @Id
    private String idProductoEntrada;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "numFactura", nullable = false)
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idOrden", nullable = false)
    private Orden orden;

    @OneToMany(mappedBy="productoEntrada",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Lps> lps;
    private String codigo;
    private String descripcion;
    private int cantidad;
    private String localidad;
    private String status;
    private int cantidadReportada;
    private boolean editable;
    private int cambiado;
    private int sobrante;
    private int malEstado;
    private int faltante;
    private String paisOrigen;
    private String consecutivo;
    private String codigoEscaneo;

    public String getCodigoEscaneo() {
        return this.codigoEscaneo;
    }

    public void setCodigoEscaneo(String codigoEscaneo) {
        this.codigoEscaneo = codigoEscaneo;
    }


    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public int getCambiado() {
        return cambiado;
    }

    public void setCambiado(int cambiado) {
        this.cambiado = cambiado;
    }

    public int getSobrante() {
        return sobrante;
    }

    public void setSobrante(int sobrante) {
        this.sobrante = sobrante;
    }

    public int getMalEstado() {
        return malEstado;
    }

    public void setMalEstado(int malEstado) {
        this.malEstado = malEstado;
    }

    public int getFaltante() {
        return faltante;
    }

    public void setFaltante(int faltante) {
        this.faltante = faltante;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Set<Lps> getLps() {
        return lps;
    }

    public void setLps(Set<Lps> lps) {
        this.lps = lps;
    }

    public int getCantidadReportada() {
        return cantidadReportada;
    }

    public void setCantidadReportada(int cantidadReportada) {
        this.cantidadReportada = cantidadReportada;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdProductoEntrada() {
        return idProductoEntrada;
    }

    public void setIdProductoEntrada(String idProductoEntrada) {
        this.idProductoEntrada = idProductoEntrada;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }
}
