package dhl.supplychain.api.au2dit.api.models;

import javax.persistence.Id;

public class Item {
    @Id
    private String id;
    private String codigo;
    private String descripcion;
    private int cantidad;
    private int cantidadRegistrada;
    private String localidad;
    private Boolean pieza; // Pieza = true || Bulto = false
    private int maltratados;
    private int cambiados;
    private int sobrantes;
    private String orden;
    private String consecutivo;

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public int getMaltratados() {
        return maltratados;
    }

    public void setMaltratados(int maltratados) {
        this.maltratados = maltratados;
    }

    public int getCambiados() {
        return cambiados;
    }

    public void setCambiados(int cambiados) {
        this.cambiados = cambiados;
    }

    public int getSobrantes() {
        return sobrantes;
    }

    public void setSobrantes(int sobrantes) {
        this.sobrantes = sobrantes;
    }

    public Boolean getPieza() {
        return pieza;
    }

    public void setPieza(Boolean pieza) {
        this.pieza = pieza;
    }

    public int getCantidadRegistrada() {
        return cantidadRegistrada;
    }

    public void setCantidadRegistrada(int cantidadRegistrada) {
        this.cantidadRegistrada = cantidadRegistrada;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
