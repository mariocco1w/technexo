package com.technexo.chat.consulta;

import com.technexo.chat.dto.AtributoConocido;
import com.technexo.chat.dto.CatalogoReferencia;
import com.technexo.chat.dto.ConsultaFiltros;
import com.technexo.chat.dto.FiltroAtributo;
import com.technexo.chat.dto.Interpretacion;
import com.technexo.chat.dto.Operador;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convierte una pregunta en filtros cerrados. No genera SQL.
 */
public final class InterpreteConsulta {

    private static final Pattern PRECIO_MAX = Pattern.compile(
            "(inferior|menor|menos|hasta|maximo|bajo|presupuesto)\\s+(?:a |de |que |del )?q?\\s*(\\d+(?:\\.\\d+)?)");
    private static final Pattern PRECIO_MIN = Pattern.compile(
            "(?:mas de|superior a|mayor a|mayor que|desde)\\s*q?\\s*(\\d+(?:\\.\\d+)?)");
    private static final Pattern PUERTOS = Pattern.compile("(\\d+)\\s*puertos?");
    private static final Pattern MBPS = Pattern.compile("(\\d+)\\s*mbps");

    private static final Map<String, String> TIPOS = new LinkedHashMap<>();

    static {
        TIPOS.put("switches", "switch");
        TIPOS.put("switch", "switch");
        TIPOS.put("routers", "router");
        TIPOS.put("router", "router");
        TIPOS.put("camaras", "camara");
        TIPOS.put("camara", "camara");
        TIPOS.put("ont", "ont");
    }

    private InterpreteConsulta() {
    }

    public static Interpretacion interpretar(String pregunta, CatalogoReferencia catalogo) {
        String normalizada = TextoUtil.normalizar(pregunta);
        if (normalizada.isBlank()) {
            return Interpretacion.ambigua("Escribe qué equipo buscas. Por ejemplo: switches de 24 puertos con PoE+.");
        }

        BigDecimal precioMax = capturar(PRECIO_MAX, normalizada);
        BigDecimal precioMin = capturar(PRECIO_MIN, normalizada);
        if (precioMin != null && precioMax != null && precioMin.compareTo(precioMax) > 0) {
            return Interpretacion.ambigua("El rango de precio no es válido. Indica un mínimo menor que el máximo.");
        }

        boolean soloDisponibles = TextoUtil.contienePalabra(normalizada, "disponible")
                || TextoUtil.contienePalabra(normalizada, "disponibles")
                || normalizada.contains("en stock");

        String texto = tipoProducto(normalizada);
        String categoria = coincidencia(normalizada, catalogo.categorias());
        String marca = coincidenciaMarca(normalizada, catalogo.marcas());
        List<FiltroAtributo> atributos = atributos(normalizada, catalogo.atributos());

        ConsultaFiltros filtros = new ConsultaFiltros(texto, categoria, marca, precioMin, precioMax, soloDisponibles, atributos);
        if (!filtros.tieneCriterio()) {
            if (normalizada.contains("presupuesto")) {
                return Interpretacion.ambigua("Indica el monto. Por ejemplo: routers con precio inferior a Q800.");
            }
            if (normalizada.contains("caracteristica")) {
                return Interpretacion.ambigua("Indica la característica. Por ejemplo: 24 puertos, PoE+ o Gigabit.");
            }
            return Interpretacion.ambigua("No identifiqué un equipo, una característica o un precio. Prueba: switches administrables de 24 puertos con PoE+.");
        }
        return Interpretacion.concreta(filtros);
    }

    private static List<FiltroAtributo> atributos(String normalizada, List<AtributoConocido> conocidos) {
        List<FiltroAtributo> filtros = new ArrayList<>();
        if (conocidos == null) {
            return filtros;
        }
        for (AtributoConocido atributo : conocidos) {
            FiltroAtributo filtro = filtroDe(normalizada, atributo);
            if (filtro != null) {
                filtros.add(filtro);
            }
        }
        return filtros;
    }

    private static FiltroAtributo filtroDe(String normalizada, AtributoConocido atributo) {
        String nombre = TextoUtil.normalizar(atributo.nombre());
        String tipo = atributo.tipoDato() == null ? "" : atributo.tipoDato();
        String unidad = TextoUtil.normalizar(atributo.unidad());
        boolean mencionado = !nombre.isBlank() && normalizada.contains(nombre);

        if ("BOOLEANO".equals(tipo) && (mencionado || ("poe".equals(nombre) && TextoUtil.contienePalabra(normalizada, "poe")))) {
            boolean afirmativo = !normalizada.contains("sin " + nombre) && !normalizada.contains("no " + nombre);
            return new FiltroAtributo(atributo.nombre(), tipo, Operador.EQ, null, null, afirmativo);
        }
        if ("NUMERO".equals(tipo)) {
            BigDecimal numero = numeroDe(normalizada, unidad);
            if (numero != null) {
                return new FiltroAtributo(atributo.nombre(), tipo, Operador.EQ, null, numero, null);
            }
        }
        if ("TEXTO".equals(tipo) && "tecnologia optica".equals(nombre) && TextoUtil.contienePalabra(normalizada, "gpon")) {
            return new FiltroAtributo(atributo.nombre(), tipo, Operador.EQ, "GPON", null, null);
        }
        return null;
    }

    private static BigDecimal numeroDe(String normalizada, String unidad) {
        if ("puertos".equals(unidad)) {
            return primerNumero(PUERTOS, normalizada);
        }
        if ("mbps".equals(unidad)) {
            BigDecimal mbps = primerNumero(MBPS, normalizada);
            if (mbps != null) {
                return mbps;
            }
            if (TextoUtil.contienePalabra(normalizada, "gigabit")) {
                return new BigDecimal("1000");
            }
        }
        return null;
    }

    private static String tipoProducto(String normalizada) {
        for (Map.Entry<String, String> tipo : TIPOS.entrySet()) {
            if (TextoUtil.contienePalabra(normalizada, tipo.getKey())) {
                return tipo.getValue();
            }
        }
        return null;
    }

    private static String coincidencia(String normalizada, List<String> nombres) {
        if (nombres == null) {
            return null;
        }
        for (String nombre : nombres) {
            String normal = TextoUtil.normalizar(nombre);
            if (normal.length() > 3 && normalizada.contains(normal)) {
                return nombre;
            }
        }
        return null;
    }

    private static String coincidenciaMarca(String normalizada, List<String> marcas) {
        String completa = coincidencia(normalizada, marcas);
        if (completa != null) {
            return completa;
        }
        if (marcas == null) {
            return null;
        }
        for (String marca : marcas) {
            for (String parte : TextoUtil.normalizar(marca).split(" ")) {
                if (parte.length() >= 5 && TextoUtil.contienePalabra(normalizada, parte)) {
                    return marca;
                }
            }
        }
        return null;
    }

    private static BigDecimal capturar(Pattern patron, String texto) {
        return primerNumero(patron, texto);
    }

    private static BigDecimal primerNumero(Pattern patron, String texto) {
        Matcher coincidencia = patron.matcher(texto);
        if (!coincidencia.find()) {
            return null;
        }
        String crudo = coincidencia.group(coincidencia.groupCount());
        return new BigDecimal(crudo);
    }
}
