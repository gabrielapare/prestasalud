package com.prestasalud.asistente.controller;

import com.prestasalud.asistente.service.AsistenteService;
import com.prestasalud.asistente.service.AsistenteService.RespuestaAsistente;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asistente")
public class AsistenteController {

    private final AsistenteService asistenteService;

    public AsistenteController(AsistenteService asistenteService) {
        this.asistenteService = asistenteService;
    }

    @PostMapping("/preguntar")
    public RespuestaAsistente preguntar(@RequestBody PreguntaRequest request) {
        return asistenteService.preguntar(request.pregunta());
    }

    public record PreguntaRequest(String pregunta) {}
}