package com.technexo.chat.repository;

import com.technexo.chat.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByIdConversacionOrderByFechaAscIdMensajeAsc(Long idConversacion);
}
