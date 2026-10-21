package com.technexo.catalogo.repository;

import com.technexo.catalogo.entity.AtributoTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtributoTecnicoRepository extends JpaRepository<AtributoTecnico, Integer> {
    List<AtributoTecnico> findAllByOrderByNombreAsc();
}
