package com.prestasalud.solicitudes.controller;

import com.prestasalud.solicitudes.controller.dto.CrearSolicitudRequest;
import com.prestasalud.solicitudes.controller.dto.SolicitudResponse;
import com.prestasalud.solicitudes.model.Solicitud;
import com.prestasalud.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    public ResponseEntity<SolicitudResponse> crear(@Valid @RequestBody CrearSolicitudRequest request) {
        Solicitud creada = solicitudService.crear(request.medicoId(), request.tipo(), request.descripcion());
        return ResponseEntity
                .created(URI.create("/api/solicitudes/" + creada.getId()))
                .body(SolicitudResponse.from(creada));
    }

    @GetMapping("/{id}")
    public SolicitudResponse obtener(@PathVariable Long id) {
        return SolicitudResponse.from(solicitudService.obtener(id));
    }

    @GetMapping
    public List<SolicitudResponse> listarPorMedico(@RequestParam Long medicoId) {
        return solicitudService.listarPorMedico(medicoId).stream()
                .map(SolicitudResponse::from)
                .toList();
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<SolicitudResponse> aprobar(@PathVariable Long id) {
        return ResponseEntity.accepted().body(SolicitudResponse.from(solicitudService.aprobar(id)));
    }
}