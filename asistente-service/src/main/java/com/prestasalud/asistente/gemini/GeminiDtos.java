package com.prestasalud.asistente.gemini;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

public final class GeminiDtos {

    private GeminiDtos() {}

    // ---- Request ----
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GeminiRequest(SystemInstruction systemInstruction,
                                List<Content> contents,
                                List<Tool> tools,
                                GenerationConfig generationConfig) {}

    public record SystemInstruction(List<Part> parts) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Content(String role, List<Part> parts) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Part(String text,
                       FunctionCall functionCall,
                       FunctionResponse functionResponse,
                       String thoughtSignature) {

        public static Part texto(String text) {
            return new Part(text, null, null, null);
        }

        public static Part respuestaFuncion(String nombre, Map<String, Object> resultado) {
            return new Part(null, null, new FunctionResponse(nombre, resultado), null);
        }
    }

    public record FunctionCall(String name, Map<String, Object> args) {}

    public record FunctionResponse(String name, Map<String, Object> response) {}

    public record Tool(List<FunctionDeclaration> functionDeclarations) {}

    public record FunctionDeclaration(String name, String description, Map<String, Object> parameters) {}

    public record GenerationConfig(Double temperature) {}

    // ---- Response ----
    public record GeminiResponse(List<Candidate> candidates, UsageMetadata usageMetadata) {

        public Content contenido() {
            return candidates.get(0).content();
        }

        public String texto() {
            return contenido().parts().stream()
                    .map(Part::text)
                    .filter(t -> t != null)
                    .findFirst()
                    .orElse("");
        }

        public List<FunctionCall> llamadasAFunciones() {
            return contenido().parts().stream()
                    .map(Part::functionCall)
                    .filter(f -> f != null)
                    .toList();
        }
    }

    public record Candidate(Content content) {}

    public record UsageMetadata(Integer promptTokenCount,
                                Integer candidatesTokenCount,
                                Integer totalTokenCount) {}
}