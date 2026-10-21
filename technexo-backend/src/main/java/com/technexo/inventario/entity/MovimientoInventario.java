package com.technexo.inventario.entity;

import com.technexo.catalogo.entity.Producto;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MovimientoInventario")
public class MovimientoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private String tipoMovimiento;
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
    @Column(name = "motivo", nullable = false, length = 300)
    private String motivo;
    @Column(name = "referencia", length = 100)
    private String referencia;
    @Column(name = "creado_por", length = 120)
    private String creadoPor;

    public static class MovimientoInventarioBuilder {
        private Long idMovimiento;
        private Producto producto;
        private String tipoMovimiento;
        private Integer cantidad;
        private LocalDateTime fecha;
        private String motivo;
        private String referencia;
        private String creadoPor;

        MovimientoInventarioBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public MovimientoInventario.MovimientoInventarioBuilder idMovimiento(final Long idMovimiento) {
            this.idMovimiento = idMovimiento;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MovimientoInventario.MovimientoInventarioBuilder producto(final Producto producto) {
            this.producto = producto;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MovimientoInventario.MovimientoInventarioBuilder tipoMovimiento(final String tipoMovimiento) {
            this.tipoMovimiento = tipoMovimiento;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MovimientoInventario.MovimientoInventarioBuilder cantidad(final Integer cantidad) {
            this.cantidad = cantidad;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MovimientoInventario.MovimientoInventarioBuilder fecha(final LocalDateTime fecha) {
            this.fecha = fecha;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MovimientoInventario.MovimientoInventarioBuilder motivo(final String motivo) {
            this.motivo = motivo;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MovimientoInventario.MovimientoInventarioBuilder referencia(final String referencia) {
            this.referencia = referencia;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MovimientoInventario.MovimientoInventarioBuilder creadoPor(final String creadoPor) {
            this.creadoPor = creadoPor;
            return this;
        }

        public MovimientoInventario build() {
            return new MovimientoInventario(this.idMovimiento, this.producto, this.tipoMovimiento, this.cantidad, this.fecha, this.motivo, this.referencia, this.creadoPor);
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "MovimientoInventario.MovimientoInventarioBuilder(idMovimiento=" + this.idMovimiento + ", producto=" + this.producto + ", tipoMovimiento=" + this.tipoMovimiento + ", cantidad=" + this.cantidad + ", fecha=" + this.fecha + ", motivo=" + this.motivo + ", referencia=" + this.referencia + ", creadoPor=" + this.creadoPor + ")";
        }
    }

    public static MovimientoInventario.MovimientoInventarioBuilder builder() {
        return new MovimientoInventario.MovimientoInventarioBuilder();
    }

    public Long getIdMovimiento() {
        return this.idMovimiento;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public String getTipoMovimiento() {
        return this.tipoMovimiento;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public LocalDateTime getFecha() {
        return this.fecha;
    }

    public String getMotivo() {
        return this.motivo;
    }

    public String getReferencia() {
        return this.referencia;
    }

    public String getCreadoPor() {
        return this.creadoPor;
    }

    public void setIdMovimiento(final Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public void setProducto(final Producto producto) {
        this.producto = producto;
    }

    public void setTipoMovimiento(final String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public void setCantidad(final Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setFecha(final LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setMotivo(final String motivo) {
        this.motivo = motivo;
    }

    public void setReferencia(final String referencia) {
        this.referencia = referencia;
    }

    public void setCreadoPor(final String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public MovimientoInventario() {
    }

    public MovimientoInventario(final Long idMovimiento, final Producto producto, final String tipoMovimiento, final Integer cantidad, final LocalDateTime fecha, final String motivo, final String referencia, final String creadoPor) {
        this.idMovimiento = idMovimiento;
        this.producto = producto;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.motivo = motivo;
        this.referencia = referencia;
        this.creadoPor = creadoPor;
    }
}
