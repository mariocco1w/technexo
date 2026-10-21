package com.technexo.catalogo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_padre")
    private Categoria categoriaPadre;
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    @Column(name = "estado", nullable = false, length = 15)
    private String estado;

    public static class CategoriaBuilder {
        private Integer idCategoria;
        private Categoria categoriaPadre;
        private String nombre;
        private String descripcion;
        private String estado;

        CategoriaBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public Categoria.CategoriaBuilder idCategoria(final Integer idCategoria) {
            this.idCategoria = idCategoria;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Categoria.CategoriaBuilder categoriaPadre(final Categoria categoriaPadre) {
            this.categoriaPadre = categoriaPadre;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Categoria.CategoriaBuilder nombre(final String nombre) {
            this.nombre = nombre;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Categoria.CategoriaBuilder descripcion(final String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Categoria.CategoriaBuilder estado(final String estado) {
            this.estado = estado;
            return this;
        }

        public Categoria build() {
            return new Categoria(this.idCategoria, this.categoriaPadre, this.nombre, this.descripcion, this.estado);
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "Categoria.CategoriaBuilder(idCategoria=" + this.idCategoria + ", categoriaPadre=" + this.categoriaPadre + ", nombre=" + this.nombre + ", descripcion=" + this.descripcion + ", estado=" + this.estado + ")";
        }
    }

    public static Categoria.CategoriaBuilder builder() {
        return new Categoria.CategoriaBuilder();
    }

    public Integer getIdCategoria() {
        return this.idCategoria;
    }

    public Categoria getCategoriaPadre() {
        return this.categoriaPadre;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setIdCategoria(final Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setCategoriaPadre(final Categoria categoriaPadre) {
        this.categoriaPadre = categoriaPadre;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    public void setEstado(final String estado) {
        this.estado = estado;
    }

    public Categoria() {
    }

    public Categoria(final Integer idCategoria, final Categoria categoriaPadre, final String nombre, final String descripcion, final String estado) {
        this.idCategoria = idCategoria;
        this.categoriaPadre = categoriaPadre;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }
}
