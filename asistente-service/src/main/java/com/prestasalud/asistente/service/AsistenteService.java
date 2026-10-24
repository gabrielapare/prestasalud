package com.prestasalud.asistente.service;

import com.prestasalud.asistente.gemini.GeminiClient;
import com.prestasalud.asistente.gemini.GeminiDtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsistenteService {

    private static final Logger log = LoggerFactory.getLogger(AsistenteService.class);

    private static final String SYSTEM_PROMPT = """
            Sos el asistente virtual de PrestaSalud, un portal para prestadores de salud.
            Ayudás a médicos y administrativos con dudas sobre solicitudes:
            altas de prestador, cambios de datos y estados (PENDIENTE, APROBADA, RECHAZADA).
            Respondé en español, de forma breve, clara y amable.
            Si te preguntan algo que no tiene que ver con PrestaSalud, explicá con amabilidad
            que solo podés ayudar con temas del portal.
            Nunca inventes datos de solicitudes concretas.
            """;

    private final GeminiClient geminiClient;

    public AsistenteService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    public RespuestaAsistente preguntar(String pregunta) {
        var request = new GeminiRequest(
                new SystemInstruction(List.of(Part.texto(SYSTEM_PROMPT))),
                List.of(new Content("user", List.of(Part.texto(pregunta)))),
                null,                       // sin herramientas
                new GenerationConfig(0.2));

        long inicio = System.currentTimeMillis();
        GeminiResponse respuesta = geminiClient.generar(request);
        long latenciaMs = System.currentTimeMillis() - inicio;

        var uso = respuesta.usageMetadata();
        log.info("LLM -> latencia {} ms | tokens entrada {} | tokens salida {} | total {}",
                latenciaMs, uso.promptTokenCount(), uso.candidatesTokenCount(), uso.totalTokenCount());

        return new RespuestaAsistente(respuesta.texto(), uso.totalTokenCount(), latenciaMs);
    }

    public record RespuestaAsistente(String respuesta, Integer tokens, long latenciaMs) {}
}