package com.technexo.pedidos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
    @Column(name = "estado", nullable = false, length = 25)
    private String estado;
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;
    @Column(name = "nombre_cliente", length = 150)
    private String nombreCliente;
    @Column(name = "correo_cliente", length = 254)
    private String correoCliente;
    @Column(name = "telefono_cliente", length = 40)
    private String telefonoCliente;
    @Column(name = "clave_idempotencia", nullable = false, unique = true)
    private UUID claveIdempotencia;

    public static class PedidoBuilder {
        private Long idPedido;
        private LocalDateTime fecha;
        private String estado;
        private BigDecimal subtotal;
        private BigDecimal total;
        private String moneda;
        private String nombreCliente;
        private String correoCliente;
        private String telefonoCliente;
        private UUID claveIdempotencia;

        PedidoBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public Pedido.PedidoBuilder idPedido(final Long idPedido) {
            this.idPedido = idPedido;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Pedido.PedidoBuilder fecha(final LocalDateTime fecha) {
            this.fecha = fecha;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Pedido.PedidoBuilder estado(final String estado) {
            this.estado = estado;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Pedido.PedidoBuilder subtotal(final BigDecimal subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Pedido.PedidoBuilder total(final BigDecimal total) {
            this.total = total;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Pedido.PedidoBuilder moneda(final String moneda) {
            this.moneda = moneda;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Pedido.PedidoBuilder nombreCliente(final String nombreCliente) {
            this.nombreCliente = nombreCliente;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Pedido.PedidoBuilder correoCliente(final String correoCliente) {
            this.correoCliente = correoCliente;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Pedido.PedidoBuilder telefonoCliente(final String telefonoCliente) {
            this.telefonoCliente = telefonoCliente;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Pedido.PedidoBuilder claveIdempotencia(final UUID claveIdempotencia) {
            this.claveIdempotencia = claveIdempotencia;
            return this;
        }

        public Pedido build() {
            return new Pedido(this.idPedido, this.fecha, this.estado, this.subtotal, this.total, this.moneda, this.nombreCliente, this.correoCliente, this.telefonoCliente, this.claveIdempotencia);
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "Pedido.PedidoBuilder(idPedido=" + this.idPedido + ", fecha=" + this.fecha + ", estado=" + this.estado + ", subtotal=" + this.subtotal + ", total=" + this.total + ", moneda=" + this.moneda + ", nombreCliente=" + this.nombreCliente + ", correoCliente=" + this.correoCliente + ", telefonoCliente=" + this.telefonoCliente + ", claveIdempotencia=" + this.claveIdempotencia + ")";
        }
    }

    public static Pedido.PedidoBuilder builder() {
        return new Pedido.PedidoBuilder();
    }

    public Long getIdPedido() {
        return this.idPedido;
    }

    public LocalDateTime getFecha() {
        return this.fecha;
    }

    public String getEstado() {
        return this.estado;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public String getMoneda() {
        return this.moneda;
    }

    public String getNombreCliente() {
        return this.nombreCliente;
    }

    public String getCorreoCliente() {
        return this.correoCliente;
    }

    public String getTelefonoCliente() {
        return this.telefonoCliente;
    }

    public UUID getClaveIdempotencia() {
        return this.claveIdempotencia;
    }

    public void setIdPedido(final Long idPedido) {
        this.idPedido = idPedido;
    }

    public void setFecha(final LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setEstado(final String estado) {
        this.estado = estado;
    }

    public void setSubtotal(final BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setTotal(final BigDecimal total) {
        this.total = total;
    }

    public void setMoneda(final String moneda) {
        this.moneda = moneda;
    }

    public void setNombreCliente(final String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public void setCorreoCliente(final String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public void setTelefonoCliente(final String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public void setClaveIdempotencia(final UUID claveIdempotencia) {
        this.claveIdempotencia = claveIdempotencia;
    }

    public Pedido() {
    }

    public Pedido(final Long idPedido, final LocalDateTime fecha, final String estado, final BigDecimal subtotal, final BigDecimal total, final String moneda, final String nombreCliente, final String correoCliente, final String telefonoCliente, final UUID claveIdempotencia) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.estado = estado;
        this.subtotal = subtotal;
        this.total = total;
        this.moneda = moneda;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;
        this.telefonoCliente = telefonoCliente;
        this.claveIdempotencia = claveIdempotencia;
    }
}
