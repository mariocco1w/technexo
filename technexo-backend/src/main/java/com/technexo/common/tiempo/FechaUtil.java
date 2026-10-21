package com.technexo.common.tiempo;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * El esquema de TechNexo almacena las fechas en UTC (SYSUTCDATETIME()) y la vista
 * vw_DisponibilidadProducto compara contra SYSUTCDATETIME(). Por eso el backend
 * escribe siempre en UTC y solo convierte a hora local para mostrarla.
 */
@Component("fecha")
public class FechaUtil {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDateTime ahoraUtc() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public String local(LocalDateTime utc) {
        if (utc == null) {
            return "";
        }
        return FORMATO.format(utc.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime());
    }
}
