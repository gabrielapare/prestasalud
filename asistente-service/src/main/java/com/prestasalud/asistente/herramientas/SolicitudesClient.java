package com.prestasalud.asistente.herramientas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Map;

@Component
public class SolicitudesClient {

    private final RestClient restClient;

    public SolicitudesClient(@Value("${solicitudes.url}") String url) {
        this.restClient = RestClient.builder().baseUrl(url).build();
    }

    public Map<String, Object> consultarSolicitud(Long id) {
        try {
            return restClient.get()
                    .uri("/api/solicitudes/{id}", id)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException.NotFound e) {
            return Map.of("error", "No existe la solicitud " + id);
        } catch (ResourceAccessException e) {
            return Map.of("error", "El sistema de solicitudes no está disponible en este momento");
        }
    }

    public Map<String, Object> listarPorMedico(Long medicoId) {
        try {
            List<Map<String, Object>> solicitudes = restClient.get()
                    .uri(uri -> uri.path("/api/solicitudes").queryParam("medicoId", medicoId).build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return Map.of("solicitudes", solicitudes);
        } catch (ResourceAccessException e) {
            return Map.of("error", "El sistema de solicitudes no está disponible en este momento");
        }
    }
}