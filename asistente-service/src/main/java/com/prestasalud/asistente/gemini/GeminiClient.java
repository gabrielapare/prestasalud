package com.prestasalud.asistente.gemini;

import com.prestasalud.asistente.gemini.GeminiDtos.GeminiRequest;
import com.prestasalud.asistente.gemini.GeminiDtos.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeminiClient {

    private final RestClient restClient;
    private final String model;

    public GeminiClient(RestClient.Builder builder,
                        @Value("${gemini.api-key}") String apiKey,
                        @Value("${gemini.model}") String model) {
        this.model = model;
        this.restClient = builder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultHeader("x-goog-api-key", apiKey)
                .build();
    }

    public GeminiResponse generar(GeminiRequest request) {
        return restClient.post()
                .uri("/models/{model}:generateContent", model)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(GeminiResponse.class);
    }
}