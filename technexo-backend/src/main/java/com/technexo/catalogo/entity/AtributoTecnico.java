package com.technexo.catalogo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "AtributoTecnico")
public class AtributoTecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_atributo")
    private Integer idAtributo;
    @Column(name = "nombre", nullable = false, unique = true, length = 120)
    private String nombre;
    @Column(name = "tipo_dato", nullable = false, length = 20)
    private String tipoDato;
    @Column(name = "unidad", length = 40)
    private String unidad;
    @Column(name = "descripcion", length = 400)
    private String descripcion;

    public static class AtributoTecnicoBuilder {
        private Integer idAtributo;
        private String nombre;
        private String tipoDato;
        private String unidad;
        private String descripcion;

        AtributoTecnicoBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public AtributoTecnico.AtributoTecnicoBuilder idAtributo(final Integer idAtributo) {
            this.idAtributo = idAtributo;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AtributoTecnico.AtributoTecnicoBuilder nombre(final String nombre) {
            this.nombre = nombre;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AtributoTecnico.AtributoTecnicoBuilder tipoDato(final String tipoDato) {
            this.tipoDato = tipoDato;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AtributoTecnico.AtributoTecnicoBuilder unidad(final String unidad) {
            this.unidad = unidad;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AtributoTecnico.AtributoTecnicoBuilder descripcion(final String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public AtributoTecnico build() {
            return new AtributoTecnico(this.idAtributo, this.nombre, this.tipoDato, this.unidad, this.descripcion);
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "AtributoTecnico.AtributoTecnicoBuilder(idAtributo=" + this.idAtributo + ", nombre=" + this.nombre + ", tipoDato=" + this.tipoDato + ", unidad=" + this.unidad + ", descripcion=" + this.descripcion + ")";
        }
    }

    public static AtributoTecnico.AtributoTecnicoBuilder builder() {
        return new AtributoTecnico.AtributoTecnicoBuilder();
    }

    public Integer getIdAtributo() {
        return this.idAtributo;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getTipoDato() {
        return this.tipoDato;
    }

    public String getUnidad() {
        return this.unidad;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setIdAtributo(final Integer idAtributo) {
        this.idAtributo = idAtributo;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public void setTipoDato(final String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public void setUnidad(final String unidad) {
        this.unidad = unidad;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    public AtributoTecnico() {
    }

    public AtributoTecnico(final Integer idAtributo, final String nombre, final String tipoDato, final String unidad, final String descripcion) {
        this.idAtributo = idAtributo;
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.unidad = unidad;
        this.descripcion = descripcion;
    }
}
