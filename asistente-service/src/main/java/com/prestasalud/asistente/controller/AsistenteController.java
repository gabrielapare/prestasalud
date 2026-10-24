package com.prestasalud.asistente.controller;

import com.prestasalud.asistente.service.AgenteService;
import com.prestasalud.asistente.service.AsistenteService;
import com.prestasalud.asistente.service.AsistenteService.RespuestaAsistente;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asistente")
public class AsistenteController {

    private final AsistenteService asistenteService;
    private final AgenteService agenteService;

    public AsistenteController(AsistenteService asistenteService, AgenteService agenteService) {
        this.asistenteService = asistenteService;
        this.agenteService = agenteService;
    }

    @PostMapping("/preguntar")
    public RespuestaAsistente preguntar(@RequestBody PreguntaRequest request) {
        return asistenteService.preguntar(request.pregunta());
    }

    @PostMapping("/agente")
    public RespuestaAgente agente(@RequestBody PreguntaRequest request) {
        return new RespuestaAgente(agenteService.preguntar(request.pregunta()));
    }

    public record PreguntaRequest(String pregunta) {}

    public record RespuestaAgente(String respuesta) {}
}