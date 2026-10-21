package com.prestasalud.notificaciones.listener;

import com.prestasalud.notificaciones.event.SolicitudAprobadaEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SolicitudAprobadaListener {

    private static final Logger log = LoggerFactory.getLogger(SolicitudAprobadaListener.class);

    @KafkaListener(topics = "solicitud-aprobada")
    public void escuchar(ConsumerRecord<String, SolicitudAprobadaEvent> registro) {
        SolicitudAprobadaEvent evento = registro.value();

        log.info("Recibido de partición {}, offset {}", registro.partition(), registro.offset());
        log.info("📩 Notificando al médico {}: su solicitud {} fue aprobada",
                evento.medicoId(), evento.solicitudId());

        // Más adelante: guardar la notificación en MongoDB y "enviar" el mail
    }
}