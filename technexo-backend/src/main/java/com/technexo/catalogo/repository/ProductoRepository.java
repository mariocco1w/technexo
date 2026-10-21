package com.technexo.catalogo.repository;

import com.technexo.catalogo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Optional<Producto> findBySku(String sku);

    @Query("SELECT p FROM Producto p " +
            "JOIN FETCH p.marca m " +
            "JOIN FETCH p.categoria c " +
            "WHERE p.estado = 'ACTIVO' " +
            "ORDER BY p.nombre")
    List<Producto> findActivosConRelaciones();

    @Query("SELECT p FROM Producto p " +
            "JOIN FETCH p.marca m " +
            "JOIN FETCH p.categoria c " +
            "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "   OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "   OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "ORDER BY p.nombre")
    List<Producto> buscarPorTexto(@Param("q") String q);
}
