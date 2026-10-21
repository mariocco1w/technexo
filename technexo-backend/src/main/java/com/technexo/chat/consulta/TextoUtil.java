package com.technexo.chat.consulta;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class TextoUtil {

    private TextoUtil() {
    }

    public static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT)
                .replace('+', ' ')
                .replaceAll("[^a-z0-9\\s.]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static boolean contienePalabra(String texto, String palabra) {
        if (texto == null || palabra == null || palabra.isBlank()) {
            return false;
        }
        return Pattern.compile("(?<![a-z0-9])" + Pattern.quote(palabra) + "(?![a-z0-9])")
                .matcher(texto)
                .find();
    }
}
