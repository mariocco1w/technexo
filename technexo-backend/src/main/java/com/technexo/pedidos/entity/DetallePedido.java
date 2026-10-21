package com.technexo.pedidos.entity;

import com.technexo.catalogo.entity.Producto;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "DetallePedido")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    @Column(name = "precio_unitario_historico", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitarioHistorico;

    public static class DetallePedidoBuilder {
        private Long idDetalle;
        private Pedido pedido;
        private Producto producto;
        private Integer cantidad;
        private BigDecimal precioUnitarioHistorico;

        DetallePedidoBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public DetallePedido.DetallePedidoBuilder idDetalle(final Long idDetalle) {
            this.idDetalle = idDetalle;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DetallePedido.DetallePedidoBuilder pedido(final Pedido pedido) {
            this.pedido = pedido;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DetallePedido.DetallePedidoBuilder producto(final Producto producto) {
            this.producto = producto;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DetallePedido.DetallePedidoBuilder cantidad(final Integer cantidad) {
            this.cantidad = cantidad;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DetallePedido.DetallePedidoBuilder precioUnitarioHistorico(final BigDecimal precioUnitarioHistorico) {
            this.precioUnitarioHistorico = precioUnitarioHistorico;
            return this;
        }

        public DetallePedido build() {
            return new DetallePedido(this.idDetalle, this.pedido, this.producto, this.cantidad, this.precioUnitarioHistorico);
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "DetallePedido.DetallePedidoBuilder(idDetalle=" + this.idDetalle + ", pedido=" + this.pedido + ", producto=" + this.producto + ", cantidad=" + this.cantidad + ", precioUnitarioHistorico=" + this.precioUnitarioHistorico + ")";
        }
    }

    public static DetallePedido.DetallePedidoBuilder builder() {
        return new DetallePedido.DetallePedidoBuilder();
    }

    public Long getIdDetalle() {
        return this.idDetalle;
    }

    public Pedido getPedido() {
        return this.pedido;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public BigDecimal getPrecioUnitarioHistorico() {
        return this.precioUnitarioHistorico;
    }

    public void setIdDetalle(final Long idDetalle) {
        this.idDetalle = idDetalle;
    }

    public void setPedido(final Pedido pedido) {
        this.pedido = pedido;
    }

    public void setProducto(final Producto producto) {
        this.producto = producto;
    }

    public void setCantidad(final Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitarioHistorico(final BigDecimal precioUnitarioHistorico) {
        this.precioUnitarioHistorico = precioUnitarioHistorico;
    }

    public DetallePedido() {
    }

    public DetallePedido(final Long idDetalle, final Pedido pedido, final Producto producto, final Integer cantidad, final BigDecimal precioUnitarioHistorico) {
        this.idDetalle = idDetalle;
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitarioHistorico = precioUnitarioHistorico;
    }
}
