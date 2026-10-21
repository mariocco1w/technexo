# TechNexo — Contexto general y continuidad del proyecto

## 1. Descripción del proyecto

**Nombre:** TechNexo  
**Tipo de proyecto:** Tienda web de equipos de tecnología y telecomunicaciones con catálogo de productos, inventario y asistente conversacional inteligente.

TechNexo es un proyecto académico desarrollado para una competencia interna de Análisis de Sistemas. Su objetivo es construir una plataforma web que permita consultar equipos tecnológicos, conocer sus características técnicas, verificar su disponibilidad y encontrar productos mediante filtros convencionales o preguntas formuladas en lenguaje natural.

El sistema estará orientado principalmente a productos de redes, telecomunicaciones, computación, seguridad y comunicación.

Una característica importante será integrar un chatbot capaz de interpretar preguntas como:

- ¿Qué switches administrables de 24 puertos tienen PoE+?
- ¿Qué routers Gigabit tienen un precio inferior a Q800?
- ¿Hay cámaras IP disponibles actualmente?
- ¿Qué equipo cumple con estas características técnicas?
- ¿Qué alternativas existen dentro de mi presupuesto?

El chatbot debe utilizar información real de la base de datos. No debe inventar productos, precios, especificaciones ni existencias.

## 2. Tecnologías y arquitectura definidas

### Base de datos
- Microsoft SQL Server.
- Modelo relacional.
- Consultas parametrizadas.
- Restricciones de integridad, claves foráneas e índices.
- Control de inventario y registro histórico de operaciones.

### Backend
- Java.
- Spring Boot.
- API REST.
- Separación por capas y módulos.
- Validación de datos y reglas de negocio en el servidor.

### Frontend
- Aplicación web para consultar el catálogo e interactuar con el chatbot.
- La tecnología concreta del frontend debe verificarse en los archivos existentes antes de tomar decisiones.

### Arquitectura
Se ha decidido utilizar un **monolito modular**. Los componentes se organizarán por responsabilidades dentro de una aplicación backend, evitando introducir microservicios innecesarios para el alcance actual.

La arquitectura deberá separar, como mínimo, las responsabilidades de:
- Catálogo y productos.
- Marcas y categorías.
- Atributos técnicos.
- Inventario y movimientos.
- Reservas.
- Pedidos y detalles.
- Conversaciones y mensajes.
- Interpretación de preguntas del chatbot.

No se debe reemplazar la arquitectura existente ni cambiar tecnologías sin justificar la necesidad y revisar primero el estado actual del proyecto.

## 3. Modelo de datos definido

El script inicial de SQL Server contempla las siguientes entidades:

| Entidad | Responsabilidad |
|---|---|
| Marca | Identificar las marcas de los productos. |
| Categoria | Clasificar los productos, permitiendo jerarquías. |
| Producto | Almacenar SKU, nombre, descripción, precio, moneda y estado. |
| AtributoTecnico | Definir las características técnicas disponibles. |
| ProductoAtributo | Asociar características y valores tipados a cada producto. |
| Inventario | Registrar existencias y niveles mínimos. |
| MovimientoInventario | Mantener un historial de entradas, salidas y ajustes. |
| Compatibilidad | Representar relaciones de compatibilidad entre productos. |
| Conversacion | Identificar conversaciones del chatbot. |
| Mensaje | Registrar mensajes asociados a una conversación. |
| Reserva | Gestionar unidades reservadas temporalmente. |
| Pedido | Registrar las compras realizadas. |
| DetallePedido | Guardar productos, cantidades y precios históricos por pedido. |

También se definió la vista `vw_DisponibilidadProducto`, encargada de calcular la disponibilidad de cada producto.

### Modelo de atributos técnicos

Los valores técnicos pueden almacenarse en campos tipados:

- `valor_texto`
- `valor_numero`
- `valor_booleano`
- `valor_fecha`

La tabla `ProductoAtributo` incluye una restricción para asegurar que cada valor utilice exactamente uno de estos campos.

Esto permite representar características diferentes sin agregar una columna específica a la tabla Producto por cada característica técnica.

## 4. Reglas de negocio acordadas

Estas reglas deben respetarse durante todo el desarrollo.

### 4.1. Catálogo

- Cada producto tiene un SKU identificador.
- Los productos se relacionan con una marca y una categoría.
- Los productos tienen precio, moneda y estado.
- Los estados contemplados son `ACTIVO`, `INACTIVO` y `DESCATALOGADO`.
- Los productos comprados no se eliminan del catálogo por el hecho de venderse.
- La disponibilidad debe distinguirse del stock físico.
- Los productos descatalogados deben conservar su historial.

### 4.2. Inventario

La disponibilidad se calcula mediante:

`Stock disponible = Stock físico - Reservas pendientes vigentes`

Una reserva solo debe descontarse mientras esté pendiente y no haya expirado.

El inventario debe conservar un historial de movimientos para permitir auditoría y seguimiento.

### 4.3. Reservas

Los estados definidos son:

- `PENDIENTE`
- `CONFIRMADA`
- `EXPIRADA`
- `CANCELADA`

Las reservas deben tener un mecanismo de expiración.

Una reserva vencida o cancelada no debe seguir reduciendo la disponibilidad.

La transición de estados debe ser consistente y no permitir que una misma reserva se confirme o cancele repetidamente de manera que genere movimientos incorrectos.

### 4.4. Pedidos

- Un pedido puede tener varios detalles.
- Cada detalle registra el producto, la cantidad y el precio unitario aplicado al momento de la compra.
- El precio histórico no debe cambiar si posteriormente cambia el precio del catálogo.
- La confirmación de una compra debe coordinarse con la actualización del inventario y el registro del pedido.

### 4.5. Concurrencia

El sistema debe impedir la sobreventa.

Si dos clientes intentan reservar simultáneamente las últimas unidades, la operación debe validarse de forma transaccional para que las unidades no se asignen más de una vez.

Se debe evaluar el uso de transacciones SQL, bloqueo apropiado, restricciones y operaciones idempotentes según el diseño final.

### 4.6. Chatbot

- El chatbot interpreta la pregunta del usuario.
- La interpretación se transforma en filtros estructurados autorizados.
- El backend valida los filtros.
- El servidor ejecuta consultas parametrizadas.
- La respuesta se genera utilizando los resultados reales.

**No se debe permitir que el modelo de lenguaje ejecute SQL arbitrario ni modifique directamente la base de datos.**

El chatbot debe responder de forma controlada cuando no existan productos coincidentes, falten datos o la consulta sea ambigua.

## 5. Trabajo realizado hasta ahora

### 5.1. Requisitos y diseño

Se elaboró documentación inicial que incluye:

- Alcance del proyecto.
- Requisitos funcionales y no funcionales.
- Reglas de negocio.
- Modelo entidad-relación conceptual.
- Arquitectura propuesta.
- Endpoints REST propuestos.
- Consideraciones de seguridad.
- Criterios de aceptación.
- Etapas de desarrollo.

Archivos generados anteriormente:

- `TechNexo_Requerimientos_MER_Arquitectura.docx`
- `technexo_mer.png`
- `technexo_arquitectura.png`

OpenCode debe comprobar si estos archivos están disponibles en el directorio de trabajo o en los recursos del proyecto. No debe asumir que ya están incorporados al repositorio.

### 5.2. Script inicial de SQL Server

Se generó el archivo:

`TechNexo_SQLServer_Inicial.sql`

Este script contempla la creación de la base de datos `TechNexoDB`, el esquema relacional, las restricciones, los índices, las categorías iniciales, los atributos técnicos y la vista de disponibilidad.

El script debe revisarse como fuente de verdad del esquema realmente implementado. La documentación conceptual puede contener diferencias respecto al DDL y estas deben identificarse antes de modificar nada.

### 5.3. Datos de prueba y consultas

Se generó el archivo:

`TechNexo_Datos_Prueba_Consultas.sql`

El script inserta productos de demostración y ejecuta consultas para validar el catálogo y los atributos técnicos.

Los productos de prueba son:

| SKU | Producto | Precio | Stock físico |
|---|---|---:|---:|
| TNX-SW-024 | Switch administrable de 24 puertos Gigabit | Q1,250.00 | 8 |
| TNX-RT-006 | Router Gigabit de 6 puertos | Q675.00 | 12 |
| TNX-ONT-001 | ONT GPON de 1 puerto Gigabit | Q320.00 | 20 |
| TNX-CAM-IP | Cámara IP de red | Q450.00 | 5 |

Marcas de demostración:
- NetCore Demo.
- FiberLink Demo.
- SecureView Demo.

Las marcas y productos son datos ficticios para pruebas, no un catálogo comercial verificado.

## 6. Estado actual de las pruebas

El usuario ejecutó el script de datos de prueba en SQL Server y compartió una captura de los resultados.

### Pruebas observadas

**Prueba A: catálogo general**

Se muestran los cuatro productos, SKU, marca, categoría, precio, moneda, stock físico, cantidad reservada, stock disponible y estado.

Los resultados observados coinciden con los datos de demostración.

**Prueba B: atributos técnicos**

Se muestran los atributos de los productos con sus respectivos valores tipados.

Ejemplos observados:
- Switch: 24 puertos.
- Switch: administrable = verdadero.
- Router: 6 puertos.
- Router: velocidad de red = 1000 Mbps.
- ONT: tecnología óptica = GPON.
- Cámara IP: 1 puerto.

**Prueba C: búsqueda por características técnicas**

La consulta devuelve:

- SKU: `TNX-SW-024`.
- Precio: Q1,250.00.
- Stock disponible: 8 unidades.

Los filtros de demostración corresponden a un switch con al menos 24 puertos, administrable, con PoE+, velocidad de red de al menos 1000 Mbps, precio máximo de Q1,500 y disponibilidad positiva.

**Prueba D: búsqueda de texto**

La consulta encuentra el switch por su información textual.

**Prueba E: productos con existencias bajas**

El resultado completo de esta consulta todavía no está confirmado a partir de la captura compartida.

### Evaluación general

El esquema y las consultas básicas muestran un funcionamiento correcto con los datos de prueba.

Sin embargo, esto no significa que el sistema completo esté terminado. Todavía falta probar explícitamente las transacciones de reservas y compras, la concurrencia, los errores y la integración con el backend.

No se ha confirmado todavía la implementación completa de Spring Boot, el frontend ni el chatbot conectado a la base de datos. OpenCode debe inspeccionar el proyecto para determinar si existen implementaciones parciales.

## 7. Archivos que se deben localizar primero

Buscar y revisar, si están disponibles:

1. `TechNexo_SQLServer_Inicial.sql`
2. `TechNexo_Datos_Prueba_Consultas.sql`
3. `TechNexo_Requerimientos_MER_Arquitectura.docx`
4. Diagramas MER y arquitectura.
5. Archivos Java y configuración de Spring Boot.
6. Archivos del frontend.
7. Configuración de conexión con SQL Server.
8. Consultas, repositorios, controladores y servicios existentes.
9. Pruebas automatizadas y documentación adicional.

Si el proyecto está en un repositorio Git, revisar la rama actual, los cambios sin confirmar y el historial relevante antes de editar.

Si falta alguno de estos archivos, registrar su ausencia. No inventar su contenido ni recrear el proyecto entero automáticamente.

## 8. Trabajo pendiente

### Fase 1. Auditoría del estado actual

Antes de programar:

- Inspeccionar la estructura del proyecto.
- Identificar qué componentes existen.
- Verificar la configuración de SQL Server.
- Revisar el DDL y sus restricciones.
- Identificar posibles discrepancias entre requisitos, documentación y código.
- Verificar qué pruebas se han ejecutado realmente.
- Elaborar un diagnóstico breve con lo terminado, lo parcial, los problemas encontrados y las tareas pendientes.

### Fase 2. Validación de inventario y reservas

Esta es la siguiente prioridad funcional.

Diseñar y probar operaciones para:

1. Crear una reserva pendiente.
2. Validar que haya unidades disponibles.
3. Evitar reservas por cantidades inválidas o superiores a las disponibles.
4. Verificar que una reserva vigente reduzca la disponibilidad.
5. Cancelar una reserva y liberar su disponibilidad.
6. Expirar una reserva y liberar su disponibilidad.
7. Confirmar una reserva.
8. Impedir que una reserva terminal se procese nuevamente de forma incorrecta.
9. Registrar movimientos de inventario cuando corresponda.
10. Probar solicitudes concurrentes para evitar sobreventa.

Usar una base de datos de desarrollo y datos de prueba. No modificar datos reales de producción.

No dar por implementadas estas operaciones únicamente porque existan las tablas correspondientes.

### Fase 3. Validación de pedidos y compras

Una vez que las reservas funcionen:

- Crear pedidos y detalles.
- Validar cantidades y precios.
- Conservar el precio unitario histórico.
- Coordinar la confirmación de la compra y la actualización del inventario.
- Evitar pedidos duplicados por reintentos.
- Registrar movimientos de inventario.
- Verificar el comportamiento ante errores intermedios y rollback.

### Fase 4. Backend con Java y Spring Boot

Cuando la lógica de datos esté definida:

- Configurar el proyecto y la conexión a SQL Server.
- Implementar entidades o modelos apropiados.
- Crear repositorios.
- Crear servicios con las reglas de negocio.
- Crear controladores REST.
- Implementar validación de entrada.
- Centralizar el manejo de errores.
- Configurar variables de entorno y credenciales seguras.
- Añadir pruebas unitarias y de integración.

Endpoints orientativos, que deben verificarse y ajustarse al diseño final:

- `GET /api/productos`
- `GET /api/productos/{id}`
- `GET /api/productos/{id}/atributos`
- `GET /api/categorias`
- `POST /api/reservas`
- `POST /api/reservas/{id}/cancelar`
- `POST /api/reservas/{id}/confirmar`
- `POST /api/pedidos`

No crear endpoints duplicados si ya existen equivalentes.

### Fase 5. Chatbot conectado al catálogo

- Diseñar el contrato de entrada y salida del chatbot.
- Interpretar preguntas en filtros estructurados.
- Validar filtros permitidos.
- Ejecutar búsquedas mediante servicios del backend.
- Incorporar atributos técnicos, precio, marca, categoría y disponibilidad.
- Devolver resultados verificables.
- Controlar preguntas ambiguas y ausencia de resultados.
- Evitar SQL arbitrario generado por el modelo.
- Registrar conversaciones y mensajes conforme a las necesidades del proyecto.

### Fase 6. Frontend e integración

- Mostrar el catálogo.
- Incorporar búsqueda y filtros.
- Mostrar fichas técnicas.
- Presentar la disponibilidad de los productos.
- Integrar el chatbot con la API.
- Mostrar errores de forma clara.
- Verificar que la información mostrada coincida con la base de datos.

### Fase 7. Pruebas finales y documentación

- Pruebas de catálogo.
- Pruebas de atributos técnicos.
- Pruebas de disponibilidad.
- Pruebas de reservas y expiración.
- Pruebas de concurrencia.
- Pruebas de pedidos.
- Pruebas del chatbot.
- Pruebas de seguridad y validación.
- Actualización del MER y la arquitectura según la implementación real.
- Documentación de instalación, configuración, ejecución y uso.
- Registro de resultados y decisiones técnicas.

## 9. Criterios generales de calidad

Toda implementación debe cumplir los siguientes principios:

- Mantener consistencia entre la documentación, el esquema y el código.
- Evitar duplicar lógica de negocio en diferentes capas.
- Usar consultas parametrizadas.
- Evitar credenciales escritas directamente en el código.
- Mantener separación de responsabilidades.
- Usar transacciones en operaciones que modifican varios registros relacionados.
- Controlar la concurrencia del inventario.
- Preservar el historial de pedidos y movimientos.
- No eliminar productos que deban conservarse para trazabilidad.
- Añadir pruebas reproducibles para las reglas de negocio.
- Evitar cambios destructivos en la base de datos.
- No introducir dependencias o tecnologías adicionales sin una justificación concreta.

## 10. Instrucciones de trabajo para OpenCode

Actúa como asistente de ingeniería de software para continuar el desarrollo de TechNexo.

Sigue este orden:

**Paso 1. Inspección**

Examina el directorio de trabajo y localiza los archivos disponibles. Determina el estado real del proyecto antes de escribir código.

**Paso 2. Diagnóstico**

Presenta un resumen con estas categorías:

- Completado y comprobado.
- Implementado, pero sin pruebas suficientes.
- Parcialmente implementado.
- Pendiente.
- Problemas o riesgos detectados.

Distingue las observaciones verificadas de las suposiciones.

**Paso 3. Plan de trabajo**

Propón una lista ordenada de cambios pequeños y verificables. Explica las dependencias entre tareas y los archivos que sería necesario modificar.

**Paso 4. Primera tarea**

Prioriza la validación de reservas e inventario en SQL Server. Reutiliza el esquema existente siempre que sea correcto.

No reconstruyas la base de datos desde cero, no sustituyas tecnologías y no elimines datos sin autorización.

**Paso 5. Implementación controlada**

Antes de modificar scripts o archivos existentes, identifica los efectos del cambio. Genera scripts incrementales y seguros cuando sea apropiado.

Ejecuta las pruebas que estén disponibles. Si no puedes conectarte a SQL Server o ejecutar una prueba, indícalo claramente y proporciona los pasos para realizarla.

**Paso 6. Informe de cada etapa**

Al terminar cada tarea, informa:

1. Qué se modificó.
2. Qué archivos se crearon o editaron.
3. Qué reglas de negocio quedaron implementadas.
4. Qué pruebas se ejecutaron y sus resultados.
5. Qué problemas permanecen.
6. Cuál es el siguiente paso recomendado.

No marques una funcionalidad como terminada sin evidencia de implementación y pruebas.

## 11. Primera solicitud concreta

Comienza inspeccionando el proyecto existente de TechNexo.

No escribas código todavía.

Entrega primero un diagnóstico de la estructura actual, los archivos encontrados, el estado del esquema de SQL Server, las consultas existentes, las implementaciones de backend y frontend, y las pruebas disponibles.

Después, identifica las tareas pendientes en orden de prioridad y prepara un plan específico para validar las reservas y el inventario sin comprometer el trabajo ya realizado.

La meta es continuar desde el estado actual del proyecto, no reiniciarlo.
