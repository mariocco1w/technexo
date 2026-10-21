package com.technexo.catalogo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca", nullable = false)
    private Marca marca;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;
    @Column(name = "sku", nullable = false, unique = true, length = 60)
    private String sku;
    @Column(name = "nombre", nullable = false, length = 180)
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;
    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public static class ProductoBuilder {
        private Integer idProducto;
        private Marca marca;
        private Categoria categoria;
        private String sku;
        private String nombre;
        private String descripcion;
        private BigDecimal precio;
        private String moneda;
        private String estado;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

        ProductoBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public Producto.ProductoBuilder idProducto(final Integer idProducto) {
            this.idProducto = idProducto;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Producto.ProductoBuilder marca(final Marca marca) {
            this.marca = marca;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Producto.ProductoBuilder categoria(final Categoria categoria) {
            this.categoria = categoria;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Producto.ProductoBuilder sku(final String sku) {
            this.sku = sku;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Producto.ProductoBuilder nombre(final String nombre) {
            this.nombre = nombre;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Producto.ProductoBuilder descripcion(final String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Producto.ProductoBuilder precio(final BigDecimal precio) {
            this.precio = precio;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Producto.ProductoBuilder moneda(final String moneda) {
            this.moneda = moneda;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Producto.ProductoBuilder estado(final String estado) {
            this.estado = estado;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Producto.ProductoBuilder fechaCreacion(final LocalDateTime fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Producto.ProductoBuilder fechaActualizacion(final LocalDateTime fechaActualizacion) {
            this.fechaActualizacion = fechaActualizacion;
            return this;
        }

        public Producto build() {
            return new Producto(this.idProducto, this.marca, this.categoria, this.sku, this.nombre, this.descripcion, this.precio, this.moneda, this.estado, this.fechaCreacion, this.fechaActualizacion);
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "Producto.ProductoBuilder(idProducto=" + this.idProducto + ", marca=" + this.marca + ", categoria=" + this.categoria + ", sku=" + this.sku + ", nombre=" + this.nombre + ", descripcion=" + this.descripcion + ", precio=" + this.precio + ", moneda=" + this.moneda + ", estado=" + this.estado + ", fechaCreacion=" + this.fechaCreacion + ", fechaActualizacion=" + this.fechaActualizacion + ")";
        }
    }

    public static Producto.ProductoBuilder builder() {
        return new Producto.ProductoBuilder();
    }

    public Integer getIdProducto() {
        return this.idProducto;
    }

    public Marca getMarca() {
        return this.marca;
    }

    public Categoria getCategoria() {
        return this.categoria;
    }

    public String getSku() {
        return this.sku;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public BigDecimal getPrecio() {
        return this.precio;
    }

    public String getMoneda() {
        return this.moneda;
    }

    public String getEstado() {
        return this.estado;
    }

    public LocalDateTime getFechaCreacion() {
        return this.fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return this.fechaActualizacion;
    }

    public void setIdProducto(final Integer idProducto) {
        this.idProducto = idProducto;
    }

    public void setMarca(final Marca marca) {
        this.marca = marca;
    }

    public void setCategoria(final Categoria categoria) {
        this.categoria = categoria;
    }

    public void setSku(final String sku) {
        this.sku = sku;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecio(final BigDecimal precio) {
        this.precio = precio;
    }

    public void setMoneda(final String moneda) {
        this.moneda = moneda;
    }

    public void setEstado(final String estado) {
        this.estado = estado;
    }

    public void setFechaCreacion(final LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setFechaActualizacion(final LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Producto() {
    }

    public Producto(final Integer idProducto, final Marca marca, final Categoria categoria, final String sku, final String nombre, final String descripcion, final BigDecimal precio, final String moneda, final String estado, final LocalDateTime fechaCreacion, final LocalDateTime fechaActualizacion) {
        this.idProducto = idProducto;
        this.marca = marca;
        this.categoria = categoria;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.moneda = moneda;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }
}
