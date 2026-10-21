package com.technexo.catalogo.repository;

import com.technexo.catalogo.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Integer> {
    List<Marca> findAllByOrderByNombreAsc();
}
