package com.technexo.chat.consulta;

import com.technexo.chat.dto.AtributoConocido;
import com.technexo.chat.dto.CatalogoReferencia;
import com.technexo.chat.dto.ConsultaFiltros;
import com.technexo.chat.dto.FiltroAtributo;
import com.technexo.chat.dto.Interpretacion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpreteConsultaTest {

    private final CatalogoReferencia catalogo = new CatalogoReferencia(
            List.of("Computación", "Redes y conectividad", "Seguridad y comunicación", "Telecomunicaciones"),
            List.of("FiberLink Demo", "NetCore Demo", "SecureView Demo"),
            List.of(
                    new AtributoConocido("Administrable", "BOOLEANO", null),
                    new AtributoConocido("Cantidad de puertos", "NUMERO", "puertos"),
                    new AtributoConocido("PoE+", "BOOLEANO", null),
                    new AtributoConocido("Tecnologia optica", "TEXTO", null),
                    new AtributoConocido("Velocidad de red", "NUMERO", "Mbps")
            )
    );

    @Test
    void interpretaSwitchConPuertosYPoe() {
        Interpretacion resultado = InterpreteConsulta.interpretar(
                "¿Qué switches administrables de 24 puertos tienen PoE+?", catalogo);
        assertFalse(resultado.ambigua());
        ConsultaFiltros filtros = resultado.filtros();
        assertEquals("switch", filtros.texto());
        assertTrue(booleano(filtros, "Administrable"));
        assertTrue(booleano(filtros, "PoE+"));
        assertEquals(0, new BigDecimal("24").compareTo(numero(filtros, "Cantidad de puertos")));
    }

    @Test
    void interpretaRouterPorPrecio() {
        Interpretacion resultado = InterpreteConsulta.interpretar(
                "¿Qué routers Gigabit tienen un precio inferior a Q800?", catalogo);
        assertFalse(resultado.ambigua());
        assertEquals("router", resultado.filtros().texto());
        assertEquals(0, new BigDecimal("800").compareTo(resultado.filtros().precioMax()));
        assertEquals(0, new BigDecimal("1000").compareTo(numero(resultado.filtros(), "Velocidad de red")));
    }

    @Test
    void interpretaCamarasDisponibles() {
        Interpretacion resultado = InterpreteConsulta.interpretar(
                "¿Hay cámaras IP disponibles actualmente?", catalogo);
        assertFalse(resultado.ambigua());
        assertEquals("camara", resultado.filtros().texto());
        assertTrue(resultado.filtros().soloDisponibles());
    }

    @Test
    void pidePrecisionSiFaltaElPresupuesto() {
        Interpretacion resultado = InterpreteConsulta.interpretar(
                "¿Qué alternativas existen dentro de mi presupuesto?", catalogo);
        assertTrue(resultado.ambigua());
        assertNull(resultado.filtros());
    }

    @Test
    void pideLaCaracteristicaCuandoNoHayDatos() {
        Interpretacion resultado = InterpreteConsulta.interpretar(
                "¿Qué equipo cumple con estas características técnicas?", catalogo);
        assertTrue(resultado.ambigua());
    }

    @Test
    void elFiltroDeSwitchSoloConservaElProductoQueCumple() {
        Interpretacion resultado = InterpreteConsulta.interpretar(
                "switches administrables de 24 puertos con PoE+", catalogo);
        FichaFiltro switchOk = new FichaFiltro("TNX-SW-024", "Switch administrable de 24 puertos Gigabit",
                "demo", "NetCore Demo", "Redes y conectividad", new BigDecimal("1250.00"), 8,
                List.of(
                        new AtributoValor("Administrable", "BOOLEANO", null, null, true),
                        new AtributoValor("Cantidad de puertos", "NUMERO", null, new BigDecimal("24"), null),
                        new AtributoValor("PoE+", "BOOLEANO", null, null, true)
                ));
        FichaFiltro router = new FichaFiltro("TNX-RT-006", "Router Gigabit de 6 puertos",
                "demo", "NetCore Demo", "Redes y conectividad", new BigDecimal("675.00"), 12,
                List.of(new AtributoValor("Cantidad de puertos", "NUMERO", null, new BigDecimal("6"), null)));
        assertTrue(FiltroProductos.cumple(switchOk, resultado.filtros()));
        assertFalse(FiltroProductos.cumple(router, resultado.filtros()));
    }

    private boolean booleano(ConsultaFiltros filtros, String nombre) {
        for (FiltroAtributo filtro : filtros.atributos()) {
            if (nombre.equals(filtro.nombre())) {
                return Boolean.TRUE.equals(filtro.booleano());
            }
        }
        return false;
    }

    private BigDecimal numero(ConsultaFiltros filtros, String nombre) {
        for (FiltroAtributo filtro : filtros.atributos()) {
            if (nombre.equals(filtro.nombre())) {
                return filtro.numero();
            }
        }
        return null;
    }
}
