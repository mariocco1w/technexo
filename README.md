# TechNexo — Tienda web de tecnología (Backend + Frontend)

Aplicación web desarrollada con **Spring Boot 3.3.6 + Java 17 + Thymeleaf** que
implementa el catálogo, las reservas, los pedidos y el inventario de TechNexo,
trabajando **sobre el esquema existente `TechNexoDB`** sin alterar su diseño.

> El **chatbot está fuera de alcance por ahora** (fases posteriores).

---

## 1. Arquitectura

Monolito: el backend Spring Boot sirve tanto la API REST (`/api/**`) como las
vistas HTML (Thymeleaf). La base de datos es el mismo esquema relacional
`TechNexoDB` definido en `TchNExBD.sql`.

```
technexo-backend/
  src/main/java/com/technexo/
    catalogo/      entidades, repositorios, DTOs, servicio y API de catálogo
    reservas/      creación/confirmación/cancelación de reservas
    pedidos/       registro y pago de pedidos (precio histórico)
    inventario/    entradas, salidas y movimientos de inventario
    web/           controladores de las vistas Thymeleaf
    common/        manejo de errores (vistas y JSON) y utilidades de fecha
  src/main/resources/
    application.yml
    templates/     index, catalogo, producto-detalle, checkout,
                   reservas, pedidos, inventario, error, fragments
```

---

## 2. Requisitos

| Componente | Detalle |
|---|---|
| JDK | **Red Hat OpenJDK 21** en `C:\Program Files\RedHat\java-21-openjdk-21.0.6.0.7-1` |
| Maven | No hace falta instalarlo: se usa el **Maven Wrapper** (`mvnw.cmd`) |
| SQL Server | Instancia `.\SQLEXPRESS` con **TCP/IP en el puerto 1433** y **autenticación mixta** |
| Base de datos | `TechNexoDB` (ya creada a partir de `TchNExBD.sql` / `TchNExBDC.sql`) |
| Navegador | Cualquiera moderno |

> **Importante (JDK):** con el JDK Android/Microsoft 21.0.8 la aplicación falla con
> `java.lang.ClassFormatError`. Use el JDK Red Hat indicado (o cualquier JDK 21
> estándar). El script `run-backend.cmd` ya apunta al JDK Red Hat.

Configuración de SQL Server (si aún no está hecha): ejecutar como administrador
`configurar_sqlserver_technexo.ps1` (habilita TCP 1433 y modo mixto y reinicia el
servicio). Es un cambio de configuración, **no destructivo**.

---

## 3. Credenciales y conexión

| Parámetro | Valor |
|---|---|
| Usuario SQL | `technexo_app` |
| Contraseña | `TechNexo#2026` |
| Servidor | `localhost,1433` |
| Base de datos | `TechNexoDB` |

Cadena usada por el backend (`src/main/resources/application.yml`):

```
jdbc:sqlserver://localhost:1433;databaseName=TechNexoDB;encrypt=false;trustServerCertificate=true
```

> El login `sa` está deshabilitado; se usa el login dedicado `technexo_app`
> (`db_datareader`, `db_datawriter`).

Detalle relevante: el esquema usa nombres **PascalCase**, por lo que
`application.yml` fija `hibernate.naming.physical-strategy =
PhysicalNamingStrategyStandardImpl`.

---

## 4. Cómo ejecutar

1. Asegúrese de que SQL Server esté en ejecución y acepte la conexión anterior.
2. Doble clic en **`run-backend.cmd`** (o ejecútelo desde una consola).
3. Espere el mensaje `Started TechNexoApplication` y abra
   <http://localhost:8080>.
4. Para detener: `Ctrl+C` en la consola o ejecutar **`stop-backend.cmd`**.

Arranque manual equivalente:

```bat
set "JAVA_HOME=C:\Program Files\RedHat\java-21-openjdk-21.0.6.0.7-1"
cd technexo-backend
mvnw.cmd spring-boot:run
```

---

## 5. Vistas web

| Ruta | Descripción |
|---|---|
| `/` | Inicio con productos destacados y disponibilidad |
| `/catalogo` | Catálogo completo |
| `/buscar?q=...` | Búsqueda por nombre, SKU, marca o categoría |
| `/producto/{id}` | Ficha técnica (atributos) + stock + formulario de reserva |
| `/reservas` | Listado de reservas; confirmar / cancelar |
| `/checkout` | Alta de pedido (datos del cliente + productos) |
| `/pedidos` | Pedidos registrados con su detalle e importe |
| `/inventario` | Existencias, registro de ingreso (ENTRADA) y movimientos |

---

## 6. API REST (`/api/**`, respuestas JSON)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/productos` | Lista de productos activos con disponibilidad |
| GET | `/api/productos/{id}` | Detalle de un producto |
| GET | `/api/productos/{id}/atributos` | Atributos técnicos del producto |
| GET | `/api/categorias` | Categorías |
| GET | `/api/reservas` | Lista de reservas |
| POST | `/api/reservas` | Crea una reserva |
| POST | `/api/reservas/{id}/confirmar` | Confirma la reserva (descuenta stock + SALIDA) |
| POST | `/api/reservas/{id}/cancelar` | Cancela la reserva |
| GET | `/api/pedidos` | Lista de pedidos |
| POST | `/api/pedidos` | Crea un pedido (PAGADO) |
| GET | `/api/inventario` | Existencias |
| GET | `/api/inventario/movimientos` | Movimientos de inventario |
| POST | `/api/inventario/entrada` | Registra un ingreso (ENTRADA) |

Los errores de negocio se devuelven como JSON con código `409 Conflict` y la
clave `error`; las validaciones como `400 Bad Request`. Las vistas, en cambio,
muestran el mensaje en pantalla.

---

## 7. Reglas de negocio implementadas

- **Disponibilidad** = `stock_físico − reservas PENDIENTES vigentes`
  (según `vw_DisponibilidadProducto`).
- **Reserva:** se crea en estado `PENDIENTE` con expiración a 30 minutos; valida
  disponibilidad antes de crearla.
- **Confirmar reserva:** valida vigencia, descuenta stock físico y registra un
  movimiento `SALIDA`; pasa a `CONFIRMADA`.
- **Cancelar reserva:** pasa a `CANCELADA` y libera la disponibilidad.
- **Pedido (pago):** valida disponibilidad, guarda el **precio histórico** por
  línea (`precio_unitario_historico`), descuenta stock y registra un `SALIDA`;
  queda en estado `PAGADO`.
- **Ingreso de inventario:** suma stock y registra un movimiento `ENTRADA`.
- **Clave de idempotencia** del pedido: `UUID` generado por el backend.
- **Fechas en UTC:** el esquema usa `SYSUTCDATETIME()` y la vista compara contra
  UTC, por lo que el backend escribe en UTC y convierte a hora local solo para
  mostrarla.

---

## 8. Pruebas realizadas (evidencia)

Ejecutadas contra la aplicación en `http://localhost:8080`:

- **Flujo completo** (`test_flujo_web.ps1`):
  - reserva de 2 unidades → `reservadas=2, disponible=6`;
  - confirmación → `stock 8 → 6` + movimiento `SALIDA`;
  - pedido de 1 unidad → `PAGADO`, precio histórico `1250.00`, `stock 6 → 5`;
  - ingreso de 5 unidades → movimiento `ENTRADA`, `stock 5 → 10`.
- **Validaciones** (`test_validaciones.ps1`):
  - reserva de 999 unidades → rechazada (no altera disponibilidad);
  - reserva de 3 + cancelación → disponibilidad vuelve a su valor;
  - pedido de 999 unidades → rechazado.
- **Endpoints** (`test_endpoints.ps1`): todas las vistas y endpoints REST
  responden `200`.

Los datos creados por las pruebas se eliminaron al finalizar, dejando la base en
su estado original (stock 8, sin reservas/pedidos/movimientos).

---

## 9. Pendientes (fases posteriores)

- Prueba de **concurrencia real** con dos sesiones simultáneas.
- **Idempotencia** de pedidos extremo a extremo (reintentos).
- **Chatbot** (conversación/mensajes) — explícitamente fuera de alcance por ahora.
- Ampliaciones de frontend (carrito persistente, autenticación, panel admin).
