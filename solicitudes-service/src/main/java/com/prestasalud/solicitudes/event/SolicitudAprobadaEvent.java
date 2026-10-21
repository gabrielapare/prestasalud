package com.prestasalud.solicitudes.event;

public record SolicitudAprobadaEvent(Long solicitudId, Long medicoId, String fechaAprobacion) {}