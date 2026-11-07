# Asistente de catálogo

No hay un modelo externo. `InterpreteOperacion` elige una operación permitida y `ChatService` la ejecuta con los servicios de catálogo, reservas y pedidos. La respuesta se redacta solo con filas devueltas por la base.

## Qué puede hacer

| Pregunta | Operación | Efecto |
|---|---|---|
| Hola | `SALUDO` | Presenta el alcance |
| ¿Qué es TechNexo? | `INFO` | Explica la tienda sin inventar datos |
| Buscar un equipo, precio, ficha, existencias, categorías | consulta | Lee el catálogo activo |
| Compara dos SKU | `COMPARAR` | Muestra precio y existencia de esos dos códigos |
| ¿Cómo compro? | `COMPRA` | Indica catálogo, ficha y checkout. No crea el pedido |
| Quiero reservar | `PREPARAR_RESERVA` | Exige sesión y pide la frase `confirmo SKU` |
| Confirmo SKU | `CONFIRMAR_RESERVA` | Crea la reserva solo si esa confirmación sigue vigente en la sesión |
| Mis reservas / mis pedidos | consulta propia | Si no hay sesión, no muestra datos de otras cuentas |
| SQL, borrar, pedidos de otro, cambiar rol | `RECHAZAR` | No ejecuta nada |

Hay un límite de 30 mensajes por minuto por dirección. Un fallo de consulta no tumba el resto de la tienda: el chat guarda un mensaje de error y la página sigue.

## Lo que no hace

- No crea pedidos desde el chat. El checkout sigue siendo el formulario, con validación de stock y precio histórico.
- No recupera ni cambia contraseñas.
- No acepta un usuario escrito en la frase como identidad. Usa la sesión de Spring Security.
