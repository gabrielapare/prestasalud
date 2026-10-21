package com.prestasalud.notificaciones.event;

public record SolicitudAprobadaEvent(Long solicitudId, Long medicoId, String fechaAprobacion) {}