package com.technexo.inventario.entity;

import com.technexo.catalogo.entity.Producto;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Inventario")
public class Inventario {
    @Id
    @Column(name = "id_producto")
    private Integer idProducto;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", insertable = false, updatable = false)
    private Producto producto;
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;
    @Column(name = "ubicacion", length = 120)
    private String ubicacion;
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public static class InventarioBuilder {
        private Integer idProducto;
        private Producto producto;
        private Integer stockActual;
        private Integer stockMinimo;
        private String ubicacion;
        private LocalDateTime fechaActualizacion;

        InventarioBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public Inventario.InventarioBuilder idProducto(final Integer idProducto) {
            this.idProducto = idProducto;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Inventario.InventarioBuilder producto(final Producto producto) {
            this.producto = producto;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Inventario.InventarioBuilder stockActual(final Integer stockActual) {
            this.stockActual = stockActual;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Inventario.InventarioBuilder stockMinimo(final Integer stockMinimo) {
            this.stockMinimo = stockMinimo;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Inventario.InventarioBuilder ubicacion(final String ubicacion) {
            this.ubicacion = ubicacion;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Inventario.InventarioBuilder fechaActualizacion(final LocalDateTime fechaActualizacion) {
            this.fechaActualizacion = fechaActualizacion;
            return this;
        }

        public Inventario build() {
            return new Inventario(this.idProducto, this.producto, this.stockActual, this.stockMinimo, this.ubicacion, this.fechaActualizacion);
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "Inventario.InventarioBuilder(idProducto=" + this.idProducto + ", producto=" + this.producto + ", stockActual=" + this.stockActual + ", stockMinimo=" + this.stockMinimo + ", ubicacion=" + this.ubicacion + ", fechaActualizacion=" + this.fechaActualizacion + ")";
        }
    }

    public static Inventario.InventarioBuilder builder() {
        return new Inventario.InventarioBuilder();
    }

    public Integer getIdProducto() {
        return this.idProducto;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public Integer getStockActual() {
        return this.stockActual;
    }

    public Integer getStockMinimo() {
        return this.stockMinimo;
    }

    public String getUbicacion() {
        return this.ubicacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return this.fechaActualizacion;
    }

    public void setIdProducto(final Integer idProducto) {
        this.idProducto = idProducto;
    }

    public void setProducto(final Producto producto) {
        this.producto = producto;
    }

    public void setStockActual(final Integer stockActual) {
        this.stockActual = stockActual;
    }

    public void setStockMinimo(final Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public void setUbicacion(final String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setFechaActualizacion(final LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Inventario() {
    }

    public Inventario(final Integer idProducto, final Producto producto, final Integer stockActual, final Integer stockMinimo, final String ubicacion, final LocalDateTime fechaActualizacion) {
        this.idProducto = idProducto;
        this.producto = producto;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.ubicacion = ubicacion;
        this.fechaActualizacion = fechaActualizacion;
    }
}
