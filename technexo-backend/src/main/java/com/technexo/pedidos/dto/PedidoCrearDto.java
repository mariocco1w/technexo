package com.technexo.pedidos.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class PedidoCrearDto {
    @NotBlank
    @Size(max = 150)
    private String nombreCliente;
    @Email
    @Size(max = 254)
    private String correoCliente;
    @Size(max = 40)
    private String telefonoCliente;
    @NotNull
    @Size(min = 1)
    private List<Item> items;

    public static class Item {
        @NotNull
        private Integer idProducto;
        @NotNull
        @Min(1)
        private Integer cantidad;

        public Item() {
        }

        public Integer getIdProducto() {
            return this.idProducto;
        }

        public Integer getCantidad() {
            return this.cantidad;
        }

        public void setIdProducto(final Integer idProducto) {
            this.idProducto = idProducto;
        }

        public void setCantidad(final Integer cantidad) {
            this.cantidad = cantidad;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof PedidoCrearDto.Item)) return false;
            final PedidoCrearDto.Item other = (PedidoCrearDto.Item) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$idProducto = this.getIdProducto();
            final java.lang.Object other$idProducto = other.getIdProducto();
            if (this$idProducto == null ? other$idProducto != null : !this$idProducto.equals(other$idProducto)) return false;
            final java.lang.Object this$cantidad = this.getCantidad();
            final java.lang.Object other$cantidad = other.getCantidad();
            if (this$cantidad == null ? other$cantidad != null : !this$cantidad.equals(other$cantidad)) return false;
            return true;
        }

        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof PedidoCrearDto.Item;
        }

        @java.lang.Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $idProducto = this.getIdProducto();
            result = result * PRIME + ($idProducto == null ? 43 : $idProducto.hashCode());
            final java.lang.Object $cantidad = this.getCantidad();
            result = result * PRIME + ($cantidad == null ? 43 : $cantidad.hashCode());
            return result;
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "PedidoCrearDto.Item(idProducto=" + this.getIdProducto() + ", cantidad=" + this.getCantidad() + ")";
        }
    }

    public PedidoCrearDto() {
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

    public List<Item> getItems() {
        return this.items;
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

    public void setItems(final List<Item> items) {
        this.items = items;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PedidoCrearDto)) return false;
        final PedidoCrearDto other = (PedidoCrearDto) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$nombreCliente = this.getNombreCliente();
        final java.lang.Object other$nombreCliente = other.getNombreCliente();
        if (this$nombreCliente == null ? other$nombreCliente != null : !this$nombreCliente.equals(other$nombreCliente)) return false;
        final java.lang.Object this$correoCliente = this.getCorreoCliente();
        final java.lang.Object other$correoCliente = other.getCorreoCliente();
        if (this$correoCliente == null ? other$correoCliente != null : !this$correoCliente.equals(other$correoCliente)) return false;
        final java.lang.Object this$telefonoCliente = this.getTelefonoCliente();
        final java.lang.Object other$telefonoCliente = other.getTelefonoCliente();
        if (this$telefonoCliente == null ? other$telefonoCliente != null : !this$telefonoCliente.equals(other$telefonoCliente)) return false;
        final java.lang.Object this$items = this.getItems();
        final java.lang.Object other$items = other.getItems();
        if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
        return true;
    }

    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PedidoCrearDto;
    }

    @java.lang.Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $nombreCliente = this.getNombreCliente();
        result = result * PRIME + ($nombreCliente == null ? 43 : $nombreCliente.hashCode());
        final java.lang.Object $correoCliente = this.getCorreoCliente();
        result = result * PRIME + ($correoCliente == null ? 43 : $correoCliente.hashCode());
        final java.lang.Object $telefonoCliente = this.getTelefonoCliente();
        result = result * PRIME + ($telefonoCliente == null ? 43 : $telefonoCliente.hashCode());
        final java.lang.Object $items = this.getItems();
        result = result * PRIME + ($items == null ? 43 : $items.hashCode());
        return result;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "PedidoCrearDto(nombreCliente=" + this.getNombreCliente() + ", correoCliente=" + this.getCorreoCliente() + ", telefonoCliente=" + this.getTelefonoCliente() + ", items=" + this.getItems() + ")";
    }
}
