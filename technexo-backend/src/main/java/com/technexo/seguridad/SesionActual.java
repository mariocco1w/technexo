package com.technexo.seguridad;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SesionActual {

    private SesionActual() {
    }

    public static Optional<UsuarioPrincipal> principal() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioPrincipal principal)) {
            return Optional.empty();
        }
        return Optional.of(principal);
    }

    public static boolean autenticado() {
        return principal().isPresent();
    }

    public static boolean esAdmin() {
        return principal().map(UsuarioPrincipal::esAdmin).orElse(false);
    }

    public static Integer idUsuario() {
        return principal().map(UsuarioPrincipal::getIdUsuario).orElse(null);
    }

    public static String correo() {
        return principal().map(UsuarioPrincipal::getUsername).orElse(null);
    }

    public static String nombre() {
        return principal().map(UsuarioPrincipal::getNombre).orElse(null);
    }
}
