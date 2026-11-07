package com.technexo.chat.consulta;

import com.technexo.chat.dto.CatalogoReferencia;
import com.technexo.chat.dto.ConsultaFiltros;
import com.technexo.chat.dto.Interpretacion;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Elige una operación de la lista permitida. No construye SQL.
 */
public final class InterpreteOperacion {

    public enum Operacion {
        RECHAZAR,
        ACLARAR,
        BUSCAR,
        CATEGORIAS,
        PRECIO,
        CARACTERISTICAS,
        EXISTENCIAS,
        RECOMENDAR,
        RESERVAS,
        PEDIDOS,
        PREPARAR_RESERVA,
        CONFIRMAR_RESERVA,
        SALUDO,
        INFO,
        COMPARAR,
        COMPRA
    }

    public record Decision(Operacion operacion, String mensaje, Interpretacion busqueda) {
        public static Decision directa(Operacion operacion, String mensaje) {
            return new Decision(operacion, mensaje, null);
        }
    }

    private static final Pattern SKU = Pattern.compile("\\b[a-z]{2,}(?:-[a-z0-9]+)+\\b");
    private static final List<String> PROHIBIDO = List.of(
            "drop ", "delete ", "truncate", "insert ", "update ", "select ", "union ",
            " or 1", "eliminar", "elimina", "borrar", "borra ", "hazme admin", "cambiar rol",
            "cambiar mi rol", "otro usuario", "otra cuenta", "pedidos de todos", "todos los pedidos",
            "pedido de otro", "reserva de otro", "desactiva al", "otorga privilegio"
    );

    private InterpreteOperacion() {
    }

    public static Decision decidir(String pregunta, CatalogoReferencia catalogo) {
        String normalizada = TextoUtil.normalizar(pregunta);
        if (normalizada.isBlank()) {
            return Decision.directa(Operacion.ACLARAR, "Escribe qué equipo, precio o pedido quieres consultar.");
        }
        for (String bloqueo : PROHIBIDO) {
            if (normalizada.contains(bloqueo)) {
                return Decision.directa(Operacion.RECHAZAR,
                        "No puedo ejecutar esa operación. Solo consulto el catálogo y, si iniciaste sesión, tus propias reservas o pedidos.");
            }
        }
        if (esSaludo(normalizada)) {
            return Decision.directa(Operacion.SALUDO, null);
        }
        if (normalizada.contains("que es technexo") || normalizada.contains("como funciona la tienda")
                || normalizada.contains("quienes son") || normalizada.contains("que venden")) {
            return Decision.directa(Operacion.INFO, null);
        }
        if (normalizada.contains("compara") || normalizada.contains("diferencia entre")) {
            return Decision.directa(Operacion.COMPARAR, null);
        }
        if (normalizada.contains("como compro") || normalizada.contains("como comprar")
                || normalizada.contains("proceso de compra") || normalizada.contains("quiero comprar")
                || normalizada.equals("checkout")) {
            return Decision.directa(Operacion.COMPRA, null);
        }
        if (normalizada.contains("confirmo")) {
            return Decision.directa(Operacion.CONFIRMAR_RESERVA, null);
        }
        if (contiene(normalizada, "mis reservas") || normalizada.contains("estado de mi reserva")) {
            return Decision.directa(Operacion.RESERVAS, null);
        }
        if (contiene(normalizada, "mis pedidos") || normalizada.contains("estado de mi pedido")
                || normalizada.contains("estado del pedido") || normalizada.contains("consultar el estado")) {
            return Decision.directa(Operacion.PEDIDOS, null);
        }
        if (normalizada.contains("reserv")) {
            return new Decision(Operacion.PREPARAR_RESERVA, null, interpretarProducto(pregunta, catalogo, normalizada));
        }
        if (normalizada.contains("recomiend") || normalizada.contains("pequena empresa")
                || normalizada.contains("pequena oficina") || normalizada.contains("negocio pequeno")) {
            return Decision.directa(Operacion.RECOMENDAR, null);
        }
        if (contiene(normalizada, "categorias") || normalizada.contains("que categorias")) {
            return Decision.directa(Operacion.CATEGORIAS, null);
        }
        if (normalizada.contains("caracteristica") || normalizada.contains("ficha tecnica")
                || normalizada.contains("especificacion")) {
            Interpretacion busqueda = interpretarProducto(pregunta, catalogo, normalizada);
            if (busqueda.ambigua() && sku(normalizada) == null) {
                return Decision.directa(Operacion.ACLARAR, "Indica el nombre o el SKU del equipo para mostrar solo sus características registradas.");
            }
            return new Decision(Operacion.CARACTERISTICAS, null, busqueda);
        }
        if (normalizada.contains("cuanto cuesta") || normalizada.contains("precio de")
                || normalizada.contains("que precio") || normalizada.contains("cual es el precio")) {
            Interpretacion busqueda = interpretarProducto(pregunta, catalogo, normalizada);
            if (busqueda.ambigua()) {
                return Decision.directa(Operacion.ACLARAR, "Indica el nombre o el SKU. Por ejemplo: ¿cuánto cuesta el router?");
            }
            return new Decision(Operacion.PRECIO, null, busqueda);
        }
        if (normalizada.contains("existencia") || normalizada.contains("en stock")
                || normalizada.contains("hay stock") || normalizada.contains("tienen stock")
                || normalizada.contains("disponibilidad")) {
            return new Decision(Operacion.EXISTENCIAS, null, interpretarProducto(pregunta, catalogo, normalizada));
        }
        Interpretacion busqueda = InterpreteConsulta.interpretar(pregunta, catalogo);
        if (busqueda.ambigua()) {
            return Decision.directa(Operacion.ACLARAR, busqueda.aclaracion());
        }
        return new Decision(Operacion.BUSCAR, null, busqueda);
    }

    private static Interpretacion interpretarProducto(String pregunta, CatalogoReferencia catalogo, String normalizada) {
        String codigo = sku(normalizada);
        if (codigo != null) {
            return Interpretacion.concreta(new ConsultaFiltros(codigo, null, null, null, null, false, List.of()));
        }
        return InterpreteConsulta.interpretar(pregunta, catalogo);
    }

    private static String sku(String normalizada) {
        Matcher coincidencia = SKU.matcher(normalizada);
        if (!coincidencia.find()) {
            return null;
        }
        return coincidencia.group().toUpperCase(Locale.ROOT);
    }

    private static boolean contiene(String texto, String frase) {
        return texto.contains(frase);
    }

    private static boolean esSaludo(String normalizada) {
        return normalizada.matches("hola|buenos dias|buenas tardes|buenas noches|buenas|hey|saludos");
    }
}
