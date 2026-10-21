package com.prestasalud.solicitudes.controller;

import jakarta.validation.constraints.NotNull;

public record AprobarSolicitudRequest(@NotNull Long medicoId) {}