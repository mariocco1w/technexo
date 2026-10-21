package com.technexo.reservas.entity;

import com.technexo.catalogo.entity.Producto;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
    @Column(name = "identificador_cliente", length = 150)
    private String identificadorCliente;
    @Column(name = "id_conversacion")
    private Long idConversacion;

    public static class ReservaBuilder {
        private Long idReserva;
        private Producto producto;
        private Integer cantidad;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaExpiracion;
        private String estado;
        private String identificadorCliente;
        private Long idConversacion;

        ReservaBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public Reserva.ReservaBuilder idReserva(final Long idReserva) {
            this.idReserva = idReserva;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Reserva.ReservaBuilder producto(final Producto producto) {
            this.producto = producto;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Reserva.ReservaBuilder cantidad(final Integer cantidad) {
            this.cantidad = cantidad;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Reserva.ReservaBuilder fechaCreacion(final LocalDateTime fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Reserva.ReservaBuilder fechaExpiracion(final LocalDateTime fechaExpiracion) {
            this.fechaExpiracion = fechaExpiracion;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Reserva.ReservaBuilder estado(final String estado) {
            this.estado = estado;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Reserva.ReservaBuilder identificadorCliente(final String identificadorCliente) {
            this.identificadorCliente = identificadorCliente;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Reserva.ReservaBuilder idConversacion(final Long idConversacion) {
            this.idConversacion = idConversacion;
            return this;
        }

        public Reserva build() {
            return new Reserva(this.idReserva, this.producto, this.cantidad, this.fechaCreacion, this.fechaExpiracion, this.estado, this.identificadorCliente, this.idConversacion);
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "Reserva.ReservaBuilder(idReserva=" + this.idReserva + ", producto=" + this.producto + ", cantidad=" + this.cantidad + ", fechaCreacion=" + this.fechaCreacion + ", fechaExpiracion=" + this.fechaExpiracion + ", estado=" + this.estado + ", identificadorCliente=" + this.identificadorCliente + ", idConversacion=" + this.idConversacion + ")";
        }
    }

    public static Reserva.ReservaBuilder builder() {
        return new Reserva.ReservaBuilder();
    }

    public Long getIdReserva() {
        return this.idReserva;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public LocalDateTime getFechaCreacion() {
        return this.fechaCreacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return this.fechaExpiracion;
    }

    public String getEstado() {
        return this.estado;
    }

    public String getIdentificadorCliente() {
        return this.identificadorCliente;
    }

    public Long getIdConversacion() {
        return this.idConversacion;
    }

    public void setIdReserva(final Long idReserva) {
        this.idReserva = idReserva;
    }

    public void setProducto(final Producto producto) {
        this.producto = producto;
    }

    public void setCantidad(final Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setFechaCreacion(final LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setFechaExpiracion(final LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public void setEstado(final String estado) {
        this.estado = estado;
    }

    public void setIdentificadorCliente(final String identificadorCliente) {
        this.identificadorCliente = identificadorCliente;
    }

    public void setIdConversacion(final Long idConversacion) {
        this.idConversacion = idConversacion;
    }

    public Reserva() {
    }

    public Reserva(final Long idReserva, final Producto producto, final Integer cantidad, final LocalDateTime fechaCreacion, final LocalDateTime fechaExpiracion, final String estado, final String identificadorCliente, final Long idConversacion) {
        this.idReserva = idReserva;
        this.producto = producto;
        this.cantidad = cantidad;
        this.fechaCreacion = fechaCreacion;
        this.fechaExpiracion = fechaExpiracion;
        this.estado = estado;
        this.identificadorCliente = identificadorCliente;
        this.idConversacion = idConversacion;
    }
}
