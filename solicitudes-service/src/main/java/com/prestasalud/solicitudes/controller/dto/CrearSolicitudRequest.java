package com.prestasalud.solicitudes.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearSolicitudRequest(
        @NotNull Long medicoId,
        @NotBlank String tipo,
        String descripcion) {}