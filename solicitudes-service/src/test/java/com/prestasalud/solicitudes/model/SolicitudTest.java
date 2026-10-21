package com.prestasalud.solicitudes.model;

import com.prestasalud.solicitudes.exception.EstadoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudTest {

    @Test
    void nuevaSolicitud_quedaPendiente() {
        var solicitud = new Solicitud(45L, "Alta de prestador", "Cardiología");

        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE);
        assertThat(solicitud.getFechaCreacion()).isNotNull();
        assertThat(solicitud.getFechaAprobacion()).isNull();
    }

    @Test
    void aprobar_solicitudPendiente_quedaAprobada() {
        var solicitud = new Solicitud(45L, "Alta de prestador", "Cardiología");

        solicitud.aprobar();

        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.APROBADA);
        assertThat(solicitud.getFechaAprobacion()).isNotNull();
    }

    @Test
    void aprobar_solicitudYaAprobada_lanzaExcepcion() {
        var solicitud = new Solicitud(45L, "Alta de prestador", "Cardiología");
        solicitud.aprobar();

        assertThatThrownBy(solicitud::aprobar)
                .isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void rechazar_solicitudPendiente_quedaRechazadaConMotivo() {
        var solicitud = new Solicitud(45L, "Alta de prestador", "Cardiología");

        solicitud.rechazar("Documentación incompleta");

        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.RECHAZADA);
        assertThat(solicitud.getMotivoRechazo()).isEqualTo("Documentación incompleta");
    }

    @Test
    void rechazar_solicitudYaAprobada_lanzaExcepcion() {
        var solicitud = new Solicitud(45L, "Alta de prestador", "Cardiología");
        solicitud.aprobar();

        assertThatThrownBy(() -> solicitud.rechazar("Motivo"))
                .isInstanceOf(EstadoInvalidoException.class);
    }
}