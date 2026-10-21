package com.technexo.catalogo.repository;

import com.technexo.catalogo.entity.ProductoAtributo;
import com.technexo.catalogo.entity.ProductoAtributoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoAtributoRepository extends JpaRepository<ProductoAtributo, ProductoAtributoId> {
    @Query("SELECT pa FROM ProductoAtributo pa " +
            "JOIN FETCH pa.atributoTecnico a " +
            "WHERE pa.idProducto = :idProducto " +
            "ORDER BY a.nombre")
    List<ProductoAtributo> findByProductoIdConAtributo(@Param("idProducto") Integer idProducto);

    @Query("SELECT pa FROM ProductoAtributo pa JOIN FETCH pa.atributoTecnico")
    List<ProductoAtributo> findAllConAtributo();
}
