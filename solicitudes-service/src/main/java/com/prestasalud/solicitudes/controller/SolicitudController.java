package com.prestasalud.solicitudes.controller;

import com.prestasalud.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<String> aprobar(@PathVariable Long id,
                                          @Valid @RequestBody AprobarSolicitudRequest request) {
        solicitudService.aprobar(id, request.medicoId());
        return ResponseEntity.accepted().body("Solicitud " + id + " aprobada");
    }
}