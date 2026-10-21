package com.prestasalud.solicitudes.service;

import com.prestasalud.solicitudes.event.SolicitudAprobadaEvent;
import com.prestasalud.solicitudes.exception.SolicitudNoEncontradaException;
import com.prestasalud.solicitudes.model.EstadoSolicitud;
import com.prestasalud.solicitudes.model.Solicitud;
import com.prestasalud.solicitudes.repository.SolicitudRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private KafkaTemplate<String, SolicitudAprobadaEvent> kafkaTemplate;

    @InjectMocks
    private SolicitudService solicitudService;

    @Test
    void aprobar_solicitudExistente_laApruebaYPublicaEvento() {
        var solicitud = new Solicitud(45L, "Alta de prestador", null);
        ReflectionTestUtils.setField(solicitud, "id", 1L);   // ← NUEVO: simula una solicitud guardada en la base
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(kafkaTemplate.send(anyString(), any(), any())).thenReturn(new CompletableFuture<>());

        var resultado = solicitudService.aprobar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoSolicitud.APROBADA);
        verify(kafkaTemplate).send(eq("solicitud-aprobada"), any(), any(SolicitudAprobadaEvent.class));
    }

    @Test
    void aprobar_solicitudInexistente_lanzaExcepcionYNoPublica() {
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> solicitudService.aprobar(99L))
                .isInstanceOf(SolicitudNoEncontradaException.class);

        verifyNoInteractions(kafkaTemplate);
    }
}