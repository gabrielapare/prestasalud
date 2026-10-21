package com.prestasalud.solicitudes.controller.dto;

import com.prestasalud.solicitudes.model.EstadoSolicitud;
import com.prestasalud.solicitudes.model.Solicitud;

import java.time.LocalDateTime;

public record SolicitudResponse(
        Long id,
        Long medicoId,
        String tipo,
        String descripcion,
        EstadoSolicitud estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaAprobacion) {

    public static SolicitudResponse from(Solicitud s) {
        return new SolicitudResponse(s.getId(), s.getMedicoId(), s.getTipo(), s.getDescripcion(),
                s.getEstado(), s.getFechaCreacion(), s.getFechaAprobacion());
    }
}