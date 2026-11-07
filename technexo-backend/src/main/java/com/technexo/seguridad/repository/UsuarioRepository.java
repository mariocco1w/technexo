package com.technexo.seguridad.repository;

import com.technexo.seguridad.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    List<Usuario> findAllByOrderByCorreoAsc();

    @Query("SELECT COUNT(u) FROM Usuario u JOIN u.roles r WHERE r.nombre = :nombre AND u.activo = true")
    long contarActivosConRol(@Param("nombre") String nombre);
}
