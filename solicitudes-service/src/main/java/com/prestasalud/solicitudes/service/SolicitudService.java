package com.prestasalud.solicitudes.service;

import com.prestasalud.solicitudes.config.KafkaConfig;
import com.prestasalud.solicitudes.event.SolicitudAprobadaEvent;
import com.prestasalud.solicitudes.exception.SolicitudNoEncontradaException;
import com.prestasalud.solicitudes.model.Solicitud;
import com.prestasalud.solicitudes.repository.SolicitudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SolicitudService {

    private static final Logger log = LoggerFactory.getLogger(SolicitudService.class);

    private final SolicitudRepository solicitudRepository;
    private final KafkaTemplate<String, SolicitudAprobadaEvent> kafkaTemplate;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            KafkaTemplate<String, SolicitudAprobadaEvent> kafkaTemplate) {
        this.solicitudRepository = solicitudRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Solicitud crear(Long medicoId, String tipo, String descripcion) {
        return solicitudRepository.save(new Solicitud(medicoId, tipo, descripcion));
    }

    @Transactional(readOnly = true)
    public Solicitud obtener(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new SolicitudNoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listarPorMedico(Long medicoId) {
        return solicitudRepository.findByMedicoId(medicoId);
    }

    @Transactional
    public Solicitud aprobar(Long id) {
        Solicitud solicitud = obtener(id);
        solicitud.aprobar();
        // No hace falta llamar a save(): al terminar la transacción,
        // Hibernate detecta el cambio y hace el UPDATE solo (dirty checking).
        publicarEvento(solicitud);
        return solicitud;
    }

    private void publicarEvento(Solicitud solicitud) {
        var evento = new SolicitudAprobadaEvent(
                solicitud.getId(),
                solicitud.getMedicoId(),
                solicitud.getFechaAprobacion().toString());

        kafkaTemplate.send(KafkaConfig.TOPIC_SOLICITUD_APROBADA, solicitud.getId().toString(), evento)
                .whenComplete((resultado, error) -> {
                    if (error != null) {
                        log.error("Error publicando evento de la solicitud {}", solicitud.getId(), error);
                    } else {
                        log.info("Evento publicado: solicitud {} -> partición {}, offset {}",
                                solicitud.getId(),
                                resultado.getRecordMetadata().partition(),
                                resultado.getRecordMetadata().offset());
                    }
                });
    }
}