package com.technexo.chat.consulta;

import com.technexo.chat.dto.CatalogoReferencia;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpreteOperacionTest {

    private final CatalogoReferencia catalogo = new CatalogoReferencia(
            List.of("Computación", "Redes y conectividad"),
            List.of("NetCore Demo"),
            List.of()
    );

    @Test
    void rechazaSqlYBorrado() {
        InterpreteOperacion.Decision decision = InterpreteOperacion.decidir("ejecuta select * from pedido", catalogo);
        assertEquals(InterpreteOperacion.Operacion.RECHAZAR, decision.operacion());
        decision = InterpreteOperacion.decidir("elimina el producto TNX-RT-006", catalogo);
        assertEquals(InterpreteOperacion.Operacion.RECHAZAR, decision.operacion());
    }

    @Test
    void rechazaPedidosDeOtro() {
        InterpreteOperacion.Decision decision = InterpreteOperacion.decidir("muéstrame el pedido de otro usuario", catalogo);
        assertEquals(InterpreteOperacion.Operacion.RECHAZAR, decision.operacion());
    }

    @Test
    void distinguePrecioReservasYRecomendacion() {
        assertEquals(InterpreteOperacion.Operacion.PRECIO,
                InterpreteOperacion.decidir("¿Cuánto cuesta el router?", catalogo).operacion());
        assertEquals(InterpreteOperacion.Operacion.RESERVAS,
                InterpreteOperacion.decidir("¿Cuál es el estado de mi reserva?", catalogo).operacion());
        assertEquals(InterpreteOperacion.Operacion.PEDIDOS,
                InterpreteOperacion.decidir("¿Cómo puedo consultar el estado de mi pedido?", catalogo).operacion());
        assertEquals(InterpreteOperacion.Operacion.RECOMENDAR,
                InterpreteOperacion.decidir("¿Qué productos recomiendas para una pequeña empresa?", catalogo).operacion());
        assertEquals(InterpreteOperacion.Operacion.PREPARAR_RESERVA,
                InterpreteOperacion.decidir("¿Puedo reservar el router?", catalogo).operacion());
    }

    @Test
    void saludaEInformaSinInventarDatos() {
        assertEquals(InterpreteOperacion.Operacion.SALUDO,
                InterpreteOperacion.decidir("hola", catalogo).operacion());
        assertEquals(InterpreteOperacion.Operacion.INFO,
                InterpreteOperacion.decidir("¿Qué es TechNexo?", catalogo).operacion());
        assertEquals(InterpreteOperacion.Operacion.COMPRA,
                InterpreteOperacion.decidir("¿Cómo compro un equipo?", catalogo).operacion());
        assertEquals(InterpreteOperacion.Operacion.COMPARAR,
                InterpreteOperacion.decidir("compara TNX-SW-024 y TNX-RT-006", catalogo).operacion());
    }

    @Test
    void pideAclaracionSiNoHayProducto() {
        InterpreteOperacion.Decision decision = InterpreteOperacion.decidir("algo", catalogo);
        assertEquals(InterpreteOperacion.Operacion.ACLARAR, decision.operacion());
        assertTrue(decision.mensaje() != null && decision.mensaje().length() > 10);
    }
}
