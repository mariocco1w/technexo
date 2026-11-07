package com.technexo.seguridad;

/**
 * Reglas de asignación de roles, independientes de la base de datos para poder probarlas.
 */
public final class PoliticaRoles {

    private PoliticaRoles() {
    }

    public static void validarCambio(boolean actorEsAdmin, boolean esSiMismo, boolean destinoSigueSiendoAdmin, long administradoresActivos) {
        if (!actorEsAdmin) {
            throw new AccesoDenegadoException("Solo un administrador puede asignar roles");
        }
        if (esSiMismo) {
            throw new IllegalStateException("Un administrador no puede cambiar su propio rol");
        }
        if (!destinoSigueSiendoAdmin && administradoresActivos <= 1) {
            throw new IllegalStateException("Debe permanecer al menos un administrador activo");
        }
    }

    public static void validarActivacion(boolean actorEsAdmin, boolean esSiMismo, boolean quedariaInactivo, boolean eraAdmin, long administradoresActivos) {
        if (!actorEsAdmin) {
            throw new AccesoDenegadoException("Solo un administrador puede activar o desactivar cuentas");
        }
        if (esSiMismo) {
            throw new IllegalStateException("No puede desactivar ni cambiar el estado de su propia cuenta");
        }
        if (quedariaInactivo && eraAdmin && administradoresActivos <= 1) {
            throw new IllegalStateException("No puede desactivar al único administrador activo");
        }
    }
}
