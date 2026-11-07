# Auditoría de TechNexo

Fecha de revisión: 6 de noviembre de 2026. El diagnóstico distingue lo visto en el código y lo ejecutado.

## Qué está implementado y conectado a SQL Server

- Catálogo, búsqueda, ficha técnica y vista `vw_DisponibilidadProducto`.
- Reservas con estados, confirmación, cancelación y salida de inventario.
- Pedidos con precio histórico y descuento de stock.
- Inventario: entradas, salidas y movimientos.
- Cuentas, roles `ADMIN` y `CLIENTE`, BCrypt, sesión y CSRF.
- Panel `/admin` con indicadores leídos de la base, productos, categorías, inventario, reservas, pedidos, usuarios y auditoría.
- Panel `/cuenta` limitado a las reservas y pedidos del usuario autenticado.
- Asistente en `/api/chat`: la frase se convierte en una operación de una lista cerrada. No genera SQL.

## Prioridad alta

| Problema | Corrección |
|---|---|
| La contraseña de SQL estaba en `README.md`, en scripts y en el historial de Git | Se retiró de los archivos actuales. Hay que rotarla en SQL Server. |
| El arranque creaba tablas con el usuario de aplicación, que no tiene permiso `CREATE TABLE` | El esquema de cuentas se aplicó con el script `sql/002_usuarios_seguridad.sql`. |
| `CatalogoAdminService` no compilaba | La variable del producto ya no se reasigna dentro de la lambda. |
| Un POST sin permiso se reenviaba a una página que solo aceptaba GET y devolvía 405 | `/acceso-denegado` acepta el reenvío y la API responde 403. |
| El asistente no recibía la cookie CSRF | Cada respuesta deja la cookie y el chat envía `X-XSRF-TOKEN`. |

## Prioridad media

- No hay prueba de dos compras simultáneas sobre el mismo stock.
- No hay recuperación de contraseña por correo.
- El asistente no usa un proveedor de IA. Si más adelante se conecta uno, solo puede proponer una operación de la lista; no puede escribir SQL.

## Lo que no se reconstruyó

Spring Boot 3.3.6, Thymeleaf, SQL Server y el esquema original del catálogo se mantienen. Las tablas de usuarios se agregaron sin borrar datos.
