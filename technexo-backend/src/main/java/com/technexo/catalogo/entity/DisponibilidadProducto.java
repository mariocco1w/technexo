package com.technexo.catalogo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Entidad de solo lectura mapeada a la vista dbo.vw_DisponibilidadProducto.
 * La vista calcula: stock_disponible = stock_fisico - reservas PENDIENTES vigentes.
 */
@Entity
@Table(name = "vw_DisponibilidadProducto")
public class DisponibilidadProducto {

    @Id
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "sku")
    private String sku;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "moneda")
    private String moneda;

    @Column(name = "estado_producto")
    private String estadoProducto;

    @Column(name = "stock_fisico")
    private Integer stockFisico;

    @Column(name = "cantidad_reservada")
    private Integer cantidadReservada;

    @Column(name = "stock_disponible")
    private Integer stockDisponible;

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public String getEstadoProducto() { return estadoProducto; }
    public void setEstadoProducto(String estadoProducto) { this.estadoProducto = estadoProducto; }

    public Integer getStockFisico() { return stockFisico; }
    public void setStockFisico(Integer stockFisico) { this.stockFisico = stockFisico; }

    public Integer getCantidadReservada() { return cantidadReservada; }
    public void setCantidadReservada(Integer cantidadReservada) { this.cantidadReservada = cantidadReservada; }

    public Integer getStockDisponible() { return stockDisponible; }
    public void setStockDisponible(Integer stockDisponible) { this.stockDisponible = stockDisponible; }
}
