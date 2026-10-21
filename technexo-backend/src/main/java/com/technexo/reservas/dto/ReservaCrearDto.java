package com.technexo.reservas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReservaCrearDto {
    @NotNull
    private Integer idProducto;
    @NotNull
    @Min(1)
    private Integer cantidad;
    @Size(max = 150)
    private String identificadorCliente;

    public ReservaCrearDto() {
    }

    public Integer getIdProducto() {
        return this.idProducto;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public String getIdentificadorCliente() {
        return this.identificadorCliente;
    }

    public void setIdProducto(final Integer idProducto) {
        this.idProducto = idProducto;
    }

    public void setCantidad(final Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setIdentificadorCliente(final String identificadorCliente) {
        this.identificadorCliente = identificadorCliente;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof ReservaCrearDto)) return false;
        final ReservaCrearDto other = (ReservaCrearDto) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$idProducto = this.getIdProducto();
        final java.lang.Object other$idProducto = other.getIdProducto();
        if (this$idProducto == null ? other$idProducto != null : !this$idProducto.equals(other$idProducto)) return false;
        final java.lang.Object this$cantidad = this.getCantidad();
        final java.lang.Object other$cantidad = other.getCantidad();
        if (this$cantidad == null ? other$cantidad != null : !this$cantidad.equals(other$cantidad)) return false;
        final java.lang.Object this$identificadorCliente = this.getIdentificadorCliente();
        final java.lang.Object other$identificadorCliente = other.getIdentificadorCliente();
        if (this$identificadorCliente == null ? other$identificadorCliente != null : !this$identificadorCliente.equals(other$identificadorCliente)) return false;
        return true;
    }

    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof ReservaCrearDto;
    }

    @java.lang.Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $idProducto = this.getIdProducto();
        result = result * PRIME + ($idProducto == null ? 43 : $idProducto.hashCode());
        final java.lang.Object $cantidad = this.getCantidad();
        result = result * PRIME + ($cantidad == null ? 43 : $cantidad.hashCode());
        final java.lang.Object $identificadorCliente = this.getIdentificadorCliente();
        result = result * PRIME + ($identificadorCliente == null ? 43 : $identificadorCliente.hashCode());
        return result;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "ReservaCrearDto(idProducto=" + this.getIdProducto() + ", cantidad=" + this.getCantidad() + ", identificadorCliente=" + this.getIdentificadorCliente() + ")";
    }
}
