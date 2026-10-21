package com.technexo.catalogo.repository;

import com.technexo.catalogo.entity.DisponibilidadProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibilidadProductoRepository extends JpaRepository<DisponibilidadProducto, Integer> {
}
