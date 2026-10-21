package com.prestasalud.solicitudes.exception;

import com.prestasalud.solicitudes.model.EstadoSolicitud;

public class EstadoInvalidoException extends RuntimeException {
    public EstadoInvalidoException(Long id, EstadoSolicitud estadoActual, String accion) {
        super("La solicitud " + id + " no se puede " + accion + " porque está " + estadoActual);
    }
}