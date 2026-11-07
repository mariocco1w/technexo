package com.technexo.seguridad.repository;

import com.technexo.seguridad.entity.AuditoriaOperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<AuditoriaOperacion, Long> {
    List<AuditoriaOperacion> findTop12ByOrderByIdAuditoriaDesc();
}
