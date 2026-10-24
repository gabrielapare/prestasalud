package com.prestasalud.asistente.gemini;

import java.util.List;

public final class GeminiDtos {

    private GeminiDtos() {}

    // ---- Lo que mandamos ----
    public record GeminiRequest(SystemInstruction systemInstruction,
                                List<Content> contents,
                                GenerationConfig generationConfig) {}

    public record SystemInstruction(List<Part> parts) {}

    public record Content(String role, List<Part> parts) {}

    public record Part(String text) {}

    public record GenerationConfig(Double temperature) {}

    // ---- Lo que recibimos ----
    public record GeminiResponse(List<Candidate> candidates, UsageMetadata usageMetadata) {
        public String texto() {
            return candidates.get(0).content().parts().get(0).text();
        }
    }

    public record Candidate(Content content) {}

    public record UsageMetadata(Integer promptTokenCount,
                                Integer candidatesTokenCount,
                                Integer totalTokenCount) {}
}