package com.prestasalud.solicitudes.service;

import com.prestasalud.solicitudes.config.KafkaConfig;
import com.prestasalud.solicitudes.event.SolicitudAprobadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SolicitudService {

    private static final Logger log = LoggerFactory.getLogger(SolicitudService.class);

    private final KafkaTemplate<String, SolicitudAprobadaEvent> kafkaTemplate;

    public SolicitudService(KafkaTemplate<String, SolicitudAprobadaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void aprobar(Long solicitudId, Long medicoId) {
        // Más adelante: actualizar la solicitud en PostgreSQL con Spring Data

        var evento = new SolicitudAprobadaEvent(solicitudId, medicoId, LocalDateTime.now().toString());

        kafkaTemplate.send(KafkaConfig.TOPIC_SOLICITUD_APROBADA, solicitudId.toString(), evento)
                .whenComplete((resultado, error) -> {
                    if (error != null) {
                        log.error("Error publicando evento de la solicitud {}", solicitudId, error);
                    } else {
                        log.info("Evento publicado: solicitud {} -> partición {}, offset {}",
                                solicitudId,
                                resultado.getRecordMetadata().partition(),
                                resultado.getRecordMetadata().offset());
                    }
                });
    }
}