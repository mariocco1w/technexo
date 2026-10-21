package com.technexo.reservas.repository;

import com.technexo.reservas.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("SELECT COALESCE(SUM(r.cantidad), 0) FROM Reserva r " +
            "WHERE r.producto.idProducto = :idProducto " +
            "  AND r.estado = 'PENDIENTE' " +
            "  AND r.fechaExpiracion > :ahora")
    Integer sumReservasPendientesVigentes(@Param("idProducto") Integer idProducto, @Param("ahora") LocalDateTime ahora);
}
