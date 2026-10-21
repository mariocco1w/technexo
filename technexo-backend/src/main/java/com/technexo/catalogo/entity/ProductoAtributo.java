package com.technexo.catalogo.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ProductoAtributo")
@IdClass(ProductoAtributoId.class)
public class ProductoAtributo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_producto")
    private Integer idProducto;

    @Id
    @Column(name = "id_atributo")
    private Integer idAtributo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", insertable = false, updatable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atributo", insertable = false, updatable = false)
    private AtributoTecnico atributoTecnico;

    @Column(name = "valor_texto", length = 1000)
    private String valorTexto;

    @Column(name = "valor_numero", precision = 18, scale = 4)
    private BigDecimal valorNumero;

    @Column(name = "valor_booleano")
    private Boolean valorBooleano;

    @Column(name = "valor_fecha")
    private LocalDate valorFecha;

    public ProductoAtributo() {
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdAtributo() {
        return idAtributo;
    }

    public void setIdAtributo(Integer idAtributo) {
        this.idAtributo = idAtributo;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public AtributoTecnico getAtributoTecnico() {
        return atributoTecnico;
    }

    public void setAtributoTecnico(AtributoTecnico atributoTecnico) {
        this.atributoTecnico = atributoTecnico;
    }

    public String getValorTexto() {
        return valorTexto;
    }

    public void setValorTexto(String valorTexto) {
        this.valorTexto = valorTexto;
    }

    public BigDecimal getValorNumero() {
        return valorNumero;
    }

    public void setValorNumero(BigDecimal valorNumero) {
        this.valorNumero = valorNumero;
    }

    public Boolean getValorBooleano() {
        return valorBooleano;
    }

    public void setValorBooleano(Boolean valorBooleano) {
        this.valorBooleano = valorBooleano;
    }

    public LocalDate getValorFecha() {
        return valorFecha;
    }

    public void setValorFecha(LocalDate valorFecha) {
        this.valorFecha = valorFecha;
    }
}