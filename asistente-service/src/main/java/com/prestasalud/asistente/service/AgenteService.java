package com.prestasalud.asistente.service;

import com.prestasalud.asistente.gemini.GeminiClient;
import com.prestasalud.asistente.gemini.GeminiDtos.*;
import com.prestasalud.asistente.herramientas.SolicitudesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AgenteService {

    private static final Logger log = LoggerFactory.getLogger(AgenteService.class);
    private static final int MAX_PASOS = 5;

    private static final String SYSTEM_PROMPT = """
            Sos el asistente virtual de PrestaSalud, un portal para prestadores de salud.
            Tenés herramientas para consultar solicitudes reales del sistema.
            Usalas siempre que te pregunten por una solicitud o por las solicitudes de un médico.
            Respondé solo con la información que devuelven las herramientas. Nunca inventes datos.
            Si una herramienta devuelve un error, explicáselo al usuario con amabilidad.
            Respondé en español, de forma breve y clara.
            """;

    // Las herramientas que el modelo puede pedir usar: nombre, descripción y parámetros
    private static final Tool HERRAMIENTAS = new Tool(List.of(
            new FunctionDeclaration(
                    "consultar_solicitud",
                    "Obtiene el detalle y el estado de una solicitud a partir de su número de id.",
                    Map.of("type", "object",
                            "properties", Map.of("id", Map.of("type", "integer", "description", "Número de la solicitud")),
                            "required", List.of("id"))),
            new FunctionDeclaration(
                    "listar_solicitudes_medico",
                    "Lista todas las solicitudes de un médico a partir de su id.",
                    Map.of("type", "object",
                            "properties", Map.of("medicoId", Map.of("type", "integer", "description", "Id del médico")),
                            "required", List.of("medicoId")))
    ));

    private final GeminiClient geminiClient;
    private final SolicitudesClient solicitudesClient;

    public AgenteService(GeminiClient geminiClient, SolicitudesClient solicitudesClient) {
        this.geminiClient = geminiClient;
        this.solicitudesClient = solicitudesClient;
    }

    public String preguntar(String pregunta) {
        List<Content> conversacion = new ArrayList<>();
        conversacion.add(new Content("user", List.of(Part.texto(pregunta))));

        // El loop del agente
        for (int paso = 1; paso <= MAX_PASOS; paso++) {
            var request = new GeminiRequest(
                    new SystemInstruction(List.of(Part.texto(SYSTEM_PROMPT))),
                    conversacion,
                    List.of(HERRAMIENTAS),
                    new GenerationConfig(0.2));

            GeminiResponse respuesta = geminiClient.generar(request);
            List<FunctionCall> llamadas = respuesta.llamadasAFunciones();

            // Si no pide herramientas, es la respuesta final
            if (llamadas.isEmpty()) {
                log.info("✅ Paso {}: respuesta final", paso);
                return respuesta.texto();
            }

            // Guardamos lo que pidió el modelo, tal cual (incluye su thoughtSignature)
            conversacion.add(respuesta.contenido());

            // Ejecutamos cada herramienta y le devolvemos los resultados
            List<Part> resultados = new ArrayList<>();
            for (FunctionCall llamada : llamadas) {
                log.info("🛠️ Paso {}: el modelo pide '{}' con {}", paso, llamada.name(), llamada.args());
                Map<String, Object> resultado = ejecutar(llamada);
                log.info("📦 Resultado: {}", resultado);
                resultados.add(Part.respuestaFuncion(llamada.name(), resultado));
            }
            conversacion.add(new Content("user", resultados));
        }

        return "No pude resolver tu consulta. Probá de nuevo más tarde.";
    }

    private Map<String, Object> ejecutar(FunctionCall llamada) {
        return switch (llamada.name()) {
            case "consultar_solicitud" ->
                    solicitudesClient.consultarSolicitud(aLong(llamada.args().get("id")));
            case "listar_solicitudes_medico" ->
                    solicitudesClient.listarPorMedico(aLong(llamada.args().get("medicoId")));
            default -> Map.of("error", "Herramienta desconocida: " + llamada.name());
        };
    }

    private Long aLong(Object valor) {
        return ((Number) valor).longValue();
    }
}