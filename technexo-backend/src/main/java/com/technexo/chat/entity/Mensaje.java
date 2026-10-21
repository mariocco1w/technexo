package com.technexo.chat.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Mensaje")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Long idMensaje;

    @Column(name = "id_conversacion", nullable = false)
    private Long idConversacion;

    @Column(name = "rol", nullable = false, length = 20)
    private String rol;

    @Column(name = "contenido", nullable = false)
    private String contenido;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "referencias_datos")
    private String referenciasDatos;

    public Long getIdMensaje() {
        return idMensaje;
    }

    public Long getIdConversacion() {
        return idConversacion;
    }

    public void setIdConversacion(Long idConversacion) {
        this.idConversacion = idConversacion;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getReferenciasDatos() {
        return referenciasDatos;
    }

    public void setReferenciasDatos(String referenciasDatos) {
        this.referenciasDatos = referenciasDatos;
    }
}
