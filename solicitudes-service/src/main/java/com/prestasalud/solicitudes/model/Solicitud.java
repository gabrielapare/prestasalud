package com.prestasalud.solicitudes.model;

import com.prestasalud.solicitudes.exception.EstadoInvalidoException;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long medicoId;

    @Column(nullable = false)
    private String tipo;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaAprobacion;

    protected Solicitud() {
        // requerido por JPA
    }

    public Solicitud(Long medicoId, String tipo, String descripcion) {
        this.medicoId = medicoId;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    public void aprobar() {
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new EstadoInvalidoException(id, estado);
        }
        this.estado = EstadoSolicitud.APROBADA;
        this.fechaAprobacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getMedicoId() { return medicoId; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public EstadoSolicitud getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaAprobacion() { return fechaAprobacion; }
}