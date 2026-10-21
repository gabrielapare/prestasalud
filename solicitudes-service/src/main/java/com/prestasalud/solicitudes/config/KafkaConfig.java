package com.prestasalud.solicitudes.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_SOLICITUD_APROBADA = "solicitud-aprobada";

    @Bean
    public NewTopic solicitudAprobadaTopic() {
        return TopicBuilder.name(TOPIC_SOLICITUD_APROBADA)
                .partitions(3)
                .replicas(1)
                .build();
    }
}