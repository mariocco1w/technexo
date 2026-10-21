package com.technexo.catalogo.entity;

import java.io.Serializable;
import java.util.Objects;

public class ProductoAtributoId implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idProducto;
    private Integer idAtributo;

    public ProductoAtributoId() {
    }

    public ProductoAtributoId(Integer idProducto, Integer idAtributo) {
        this.idProducto = idProducto;
        this.idAtributo = idAtributo;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoAtributoId that = (ProductoAtributoId) o;
        return Objects.equals(idProducto, that.idProducto)
                && Objects.equals(idAtributo, that.idAtributo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProducto, idAtributo);
    }
}