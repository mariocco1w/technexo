# Smoke QA — 6 de noviembre de 2026

Las pruebas unitarias de `technexo-backend` terminaron con código 0 (`mvnw test`). La aplicación quedó respondiendo en `http://localhost:8080`.

| ID | Módulo | Escenario | Esperado | Obtenido | Estado |
|---|---|---|---|---|---|
| S01 | Tienda | Abrir inicio | 200 y marca TechNexo | 200 | PASS |
| S02 | Tienda | Abrir catálogo | 200 y SKU reales | 200, aparece TNX- | PASS |
| S03 | Tienda | Abrir ficha `/producto/1` | 200 | 200 | PASS |
| S04 | Admin | Entrar a `/admin` sin sesión | Redirección a login | 302 hacia `/login` | PASS |
| S05 | Chat | Saludar | Texto de alcance, sin inventar productos | Respondió con el alcance del catálogo | PASS |
| S06 | Chat | Preguntar qué es TechNexo | Menciona la base real | Mencionó TechNexoDB | PASS |
| S07 | Chat | Comparar TNX-SW-024 y TNX-RT-006 | Los dos SKU | Ambos aparecieron | PASS |
| S08 | Chat | Pedir `select` sobre pedidos | Rechazo | «No puedo» | PASS |
| S09 | Chat | Pedir el pedido de otro usuario | Rechazo | «No puedo» | PASS |
| S10 | Chat | Estado de mi pedido sin sesión | Pide iniciar sesión | Pidió iniciar sesión | PASS |
| S11 | Admin | Entrar como administrador | Llega a `/admin` | Llegó a `/admin` | PASS |
| S12 | Admin | Listar usuarios | 200 y el correo admin | 200 | PASS |
| S13 | Cliente | Abrir `/admin` con rol cliente | No muestra el panel | Página de permiso denegado | PASS |
| S14 | Cliente | Abrir `/cuenta` | 200 y su nombre | 200 | PASS |
| U01 | Pruebas | `mvnw test` | Compila y pasa | Código de salida 0 | PASS |

## No ejecutadas

| ID | Motivo | Estado |
|---|---|---|
| C01 | Dos compras simultáneas del mismo stock | BLOCKED: no se armó un segundo cliente concurrente en esta corrida |
| M01 | Recuperación de contraseña por correo | BLOCKED: no hay servidor de correo |
| I01 | Respuesta de un modelo de IA externo | BLOCKED: no hay proveedor ni clave; el asistente es local |
