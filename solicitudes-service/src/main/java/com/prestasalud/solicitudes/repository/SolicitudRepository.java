package com.prestasalud.solicitudes.repository;

import com.prestasalud.solicitudes.model.EstadoSolicitud;
import com.prestasalud.solicitudes.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByMedicoId(Long medicoId);

    List<Solicitud> findByEstado(EstadoSolicitud estado);
}