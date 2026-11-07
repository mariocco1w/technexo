package com.technexo.seguridad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoliticaRolesTest {

    @Test
    void impideQueUnAdministradorCambieSuPropioRol() {
        assertThrows(IllegalStateException.class, () -> PoliticaRoles.validarCambio(true, true, true, 2));
    }

    @Test
    void impideQuitarAlUnicoAdministrador() {
        assertThrows(IllegalStateException.class, () -> PoliticaRoles.validarCambio(true, false, false, 1));
    }

    @Test
    void permiteAsignarClienteCuandoHayOtroAdministrador() {
        assertDoesNotThrow(() -> PoliticaRoles.validarCambio(true, false, false, 2));
    }

    @Test
    void impideDesactivarLaPropiaCuenta() {
        assertThrows(IllegalStateException.class, () -> PoliticaRoles.validarActivacion(true, true, true, true, 2));
    }
}
