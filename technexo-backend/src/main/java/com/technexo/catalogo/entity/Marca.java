package com.technexo.catalogo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Marca")
public class Marca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca")
    private Integer idMarca;
    @Column(name = "nombre", nullable = false, unique = true, length = 120)
    private String nombre;
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    @Column(name = "estado", nullable = false, length = 15)
    private String estado;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public static class MarcaBuilder {
        private Integer idMarca;
        private String nombre;
        private String descripcion;
        private String estado;
        private LocalDateTime fechaCreacion;

        MarcaBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public Marca.MarcaBuilder idMarca(final Integer idMarca) {
            this.idMarca = idMarca;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Marca.MarcaBuilder nombre(final String nombre) {
            this.nombre = nombre;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Marca.MarcaBuilder descripcion(final String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Marca.MarcaBuilder estado(final String estado) {
            this.estado = estado;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Marca.MarcaBuilder fechaCreacion(final LocalDateTime fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        public Marca build() {
            return new Marca(this.idMarca, this.nombre, this.descripcion, this.estado, this.fechaCreacion);
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "Marca.MarcaBuilder(idMarca=" + this.idMarca + ", nombre=" + this.nombre + ", descripcion=" + this.descripcion + ", estado=" + this.estado + ", fechaCreacion=" + this.fechaCreacion + ")";
        }
    }

    public static Marca.MarcaBuilder builder() {
        return new Marca.MarcaBuilder();
    }

    public Integer getIdMarca() {
        return this.idMarca;
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

    public LocalDateTime getFechaCreacion() {
        return this.fechaCreacion;
    }

    public void setIdMarca(final Integer idMarca) {
        this.idMarca = idMarca;
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

    public void setFechaCreacion(final LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Marca() {
    }

    public Marca(final Integer idMarca, final String nombre, final String descripcion, final String estado, final LocalDateTime fechaCreacion) {
        this.idMarca = idMarca;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }
}
