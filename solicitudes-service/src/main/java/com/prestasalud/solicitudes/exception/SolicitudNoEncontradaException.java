package com.prestasalud.solicitudes.exception;

public class SolicitudNoEncontradaException extends RuntimeException {
    public SolicitudNoEncontradaException(Long id) {
        super("No existe la solicitud " + id);
    }
}