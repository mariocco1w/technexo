USE master;
GO

IF DB_ID(N'TechNexoDB') IS NULL
BEGIN
    CREATE DATABASE TechNexoDB;
END;
GO

USE TechNexoDB;
GO

/* =========================================================
   1. CATALOGO: MARCAS, CATEGORIAS Y PRODUCTOS
   ========================================================= */

CREATE TABLE dbo.Marca (
    id_marca       INT IDENTITY(1,1) NOT NULL,
    nombre         NVARCHAR(120) NOT NULL,
    descripcion    NVARCHAR(500) NULL,
    estado         VARCHAR(15) NOT NULL
        CONSTRAINT DF_Marca_estado DEFAULT ('ACTIVA'),
    fecha_creacion DATETIME2(0) NOT NULL
        CONSTRAINT DF_Marca_fecha DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_Marca PRIMARY KEY (id_marca),
    CONSTRAINT UQ_Marca_nombre UNIQUE (nombre),
    CONSTRAINT CK_Marca_estado CHECK (estado IN ('ACTIVA','INACTIVA'))
);
GO

CREATE TABLE dbo.Categoria (
    id_categoria        INT IDENTITY(1,1) NOT NULL,
    id_categoria_padre  INT NULL,
    nombre              NVARCHAR(120) NOT NULL,
    descripcion         NVARCHAR(500) NULL,
    estado              VARCHAR(15) NOT NULL
        CONSTRAINT DF_Categoria_estado DEFAULT ('ACTIVA'),
    CONSTRAINT PK_Categoria PRIMARY KEY (id_categoria),
    CONSTRAINT UQ_Categoria_nombre_padre UNIQUE (id_categoria_padre, nombre),
    CONSTRAINT FK_Categoria_Padre FOREIGN KEY (id_categoria_padre)
        REFERENCES dbo.Categoria(id_categoria),
    CONSTRAINT CK_Categoria_estado CHECK (estado IN ('ACTIVA','INACTIVA')),
    CONSTRAINT CK_Categoria_no_es_su_padre CHECK
        (id_categoria_padre IS NULL OR id_categoria_padre <> id_categoria)
);
GO

CREATE TABLE dbo.Producto (
    id_producto       INT IDENTITY(1,1) NOT NULL,
    id_marca          INT NOT NULL,
    id_categoria      INT NOT NULL,
    sku               NVARCHAR(60) NOT NULL,
    nombre            NVARCHAR(180) NOT NULL,
    descripcion       NVARCHAR(MAX) NULL,
    precio            DECIMAL(12,2) NOT NULL,
    moneda            CHAR(3) NOT NULL
        CONSTRAINT DF_Producto_moneda DEFAULT ('GTQ'),
    estado            VARCHAR(20) NOT NULL
        CONSTRAINT DF_Producto_estado DEFAULT ('ACTIVO'),
    fecha_creacion    DATETIME2(0) NOT NULL
        CONSTRAINT DF_Producto_fecha_creacion DEFAULT (SYSUTCDATETIME()),
    fecha_actualizacion DATETIME2(0) NOT NULL
        CONSTRAINT DF_Producto_fecha_actualizacion DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_Producto PRIMARY KEY (id_producto),
    CONSTRAINT UQ_Producto_sku UNIQUE (sku),
    CONSTRAINT FK_Producto_Marca FOREIGN KEY (id_marca)
        REFERENCES dbo.Marca(id_marca),
    CONSTRAINT FK_Producto_Categoria FOREIGN KEY (id_categoria)
        REFERENCES dbo.Categoria(id_categoria),
    CONSTRAINT CK_Producto_precio CHECK (precio >= 0),
    CONSTRAINT CK_Producto_moneda CHECK (moneda IN ('GTQ','USD')),
    CONSTRAINT CK_Producto_estado CHECK
        (estado IN ('ACTIVO','INACTIVO','DESCATALOGADO'))
);
GO

CREATE INDEX IX_Producto_nombre ON dbo.Producto(nombre);
CREATE INDEX IX_Producto_categoria_estado ON dbo.Producto(id_categoria, estado);
CREATE INDEX IX_Producto_marca_estado ON dbo.Producto(id_marca, estado);
GO

/* =========================================================
   2. ATRIBUTOS TECNICOS CONFIGURABLES
   ========================================================= */

CREATE TABLE dbo.AtributoTecnico (
    id_atributo       INT IDENTITY(1,1) NOT NULL,
    nombre            NVARCHAR(120) NOT NULL,
    tipo_dato         VARCHAR(20) NOT NULL,
    unidad            NVARCHAR(40) NULL,
    descripcion       NVARCHAR(400) NULL,
    CONSTRAINT PK_AtributoTecnico PRIMARY KEY (id_atributo),
    CONSTRAINT UQ_AtributoTecnico_nombre UNIQUE (nombre),
    CONSTRAINT CK_AtributoTecnico_tipo CHECK
        (tipo_dato IN ('TEXTO','NUMERO','BOOLEANO','FECHA'))
);
GO

CREATE TABLE dbo.ProductoAtributo (
    id_producto       INT NOT NULL,
    id_atributo       INT NOT NULL,
    valor_texto       NVARCHAR(1000) NULL,
    valor_numero      DECIMAL(18,4) NULL,
    valor_booleano    BIT NULL,
    valor_fecha       DATE NULL,
    CONSTRAINT PK_ProductoAtributo PRIMARY KEY (id_producto, id_atributo),
    CONSTRAINT FK_ProductoAtributo_Producto FOREIGN KEY (id_producto)
        REFERENCES dbo.Producto(id_producto),
    CONSTRAINT FK_ProductoAtributo_Atributo FOREIGN KEY (id_atributo)
        REFERENCES dbo.AtributoTecnico(id_atributo),
    CONSTRAINT CK_ProductoAtributo_un_solo_valor CHECK (
        (CASE WHEN valor_texto    IS NULL THEN 0 ELSE 1 END) +
        (CASE WHEN valor_numero   IS NULL THEN 0 ELSE 1 END) +
        (CASE WHEN valor_booleano IS NULL THEN 0 ELSE 1 END) +
        (CASE WHEN valor_fecha    IS NULL THEN 0 ELSE 1 END) = 1
    )
);
GO

CREATE INDEX IX_ProductoAtributo_atributo_numero
    ON dbo.ProductoAtributo(id_atributo, valor_numero)
    INCLUDE (id_producto);
GO

/* =========================================================
   3. INVENTARIO Y MOVIMIENTOS
   ========================================================= */

CREATE TABLE dbo.Inventario (
    id_producto   INT NOT NULL,
    stock_actual  INT NOT NULL
        CONSTRAINT DF_Inventario_stock DEFAULT (0),
    stock_minimo  INT NOT NULL
        CONSTRAINT DF_Inventario_minimo DEFAULT (0),
    ubicacion     NVARCHAR(120) NULL,
    fecha_actualizacion DATETIME2(0) NOT NULL
        CONSTRAINT DF_Inventario_fecha DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_Inventario PRIMARY KEY (id_producto),
    CONSTRAINT FK_Inventario_Producto FOREIGN KEY (id_producto)
        REFERENCES dbo.Producto(id_producto),
    CONSTRAINT CK_Inventario_stock CHECK (stock_actual >= 0),
    CONSTRAINT CK_Inventario_minimo CHECK (stock_minimo >= 0)
);
GO

CREATE TABLE dbo.MovimientoInventario (
    id_movimiento  BIGINT IDENTITY(1,1) NOT NULL,
    id_producto    INT NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL,
    cantidad       INT NOT NULL,
    fecha          DATETIME2(0) NOT NULL
        CONSTRAINT DF_Movimiento_fecha DEFAULT (SYSUTCDATETIME()),
    motivo         NVARCHAR(300) NOT NULL,
    referencia     NVARCHAR(100) NULL,
    creado_por     NVARCHAR(120) NULL,
    CONSTRAINT PK_MovimientoInventario PRIMARY KEY (id_movimiento),
    CONSTRAINT FK_MovimientoInventario_Producto FOREIGN KEY (id_producto)
        REFERENCES dbo.Producto(id_producto),
    CONSTRAINT CK_Movimiento_tipo CHECK
        (tipo_movimiento IN ('ENTRADA','SALIDA','AJUSTE_POSITIVO','AJUSTE_NEGATIVO')),
    CONSTRAINT CK_Movimiento_cantidad CHECK (cantidad > 0)
);
GO

CREATE INDEX IX_MovimientoInventario_producto_fecha
    ON dbo.MovimientoInventario(id_producto, fecha DESC);
GO

/* =========================================================
   4. COMPATIBILIDAD ENTRE PRODUCTOS
   ========================================================= */

CREATE TABLE dbo.Compatibilidad (
    id_compatibilidad       INT IDENTITY(1,1) NOT NULL,
    id_producto_origen      INT NOT NULL,
    id_producto_compatible  INT NOT NULL,
    tipo_compatibilidad     NVARCHAR(100) NOT NULL,
    notas                   NVARCHAR(500) NULL,
    CONSTRAINT PK_Compatibilidad PRIMARY KEY (id_compatibilidad),
    CONSTRAINT FK_Compatibilidad_Origen FOREIGN KEY (id_producto_origen)
        REFERENCES dbo.Producto(id_producto),
    CONSTRAINT FK_Compatibilidad_Compatible FOREIGN KEY (id_producto_compatible)
        REFERENCES dbo.Producto(id_producto),
    CONSTRAINT UQ_Compatibilidad UNIQUE
        (id_producto_origen, id_producto_compatible, tipo_compatibilidad),
    CONSTRAINT CK_Compatibilidad_no_mismo_producto CHECK
        (id_producto_origen <> id_producto_compatible)
);
GO

/* =========================================================
   5. CONVERSACIONES Y MENSAJES DEL CHATBOT
   ========================================================= */

CREATE TABLE dbo.Conversacion (
    id_conversacion  BIGINT IDENTITY(1,1) NOT NULL,
    fecha_inicio     DATETIME2(0) NOT NULL
        CONSTRAINT DF_Conversacion_fecha DEFAULT (SYSUTCDATETIME()),
    fecha_fin        DATETIME2(0) NULL,
    estado           VARCHAR(20) NOT NULL
        CONSTRAINT DF_Conversacion_estado DEFAULT ('ABIERTA'),
    canal            VARCHAR(30) NOT NULL
        CONSTRAINT DF_Conversacion_canal DEFAULT ('WEB'),
    identificador_sesion NVARCHAR(150) NULL,
    CONSTRAINT PK_Conversacion PRIMARY KEY (id_conversacion),
    CONSTRAINT CK_Conversacion_estado CHECK
        (estado IN ('ABIERTA','CERRADA','TRANSFERIDA')),
    CONSTRAINT CK_Conversacion_fechas CHECK
        (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
);
GO

CREATE TABLE dbo.Mensaje (
    id_mensaje       BIGINT IDENTITY(1,1) NOT NULL,
    id_conversacion  BIGINT NOT NULL,
    rol              VARCHAR(20) NOT NULL,
    contenido        NVARCHAR(MAX) NOT NULL,
    fecha            DATETIME2(0) NOT NULL
        CONSTRAINT DF_Mensaje_fecha DEFAULT (SYSUTCDATETIME()),
    referencias_datos NVARCHAR(MAX) NULL,
    CONSTRAINT PK_Mensaje PRIMARY KEY (id_mensaje),
    CONSTRAINT FK_Mensaje_Conversacion FOREIGN KEY (id_conversacion)
        REFERENCES dbo.Conversacion(id_conversacion),
    CONSTRAINT CK_Mensaje_rol CHECK
        (rol IN ('USUARIO','ASISTENTE','SISTEMA','AGENTE'))
);
GO

CREATE INDEX IX_Mensaje_conversacion_fecha
    ON dbo.Mensaje(id_conversacion, fecha);
GO

/* =========================================================
   6. RESERVAS
   La disponibilidad se calcula como stock_actual menos
   reservas PENDIENTES no expiradas.
   ========================================================= */

CREATE TABLE dbo.Reserva (
    id_reserva       BIGINT IDENTITY(1,1) NOT NULL,
    id_producto      INT NOT NULL,
    cantidad         INT NOT NULL,
    fecha_creacion   DATETIME2(0) NOT NULL
        CONSTRAINT DF_Reserva_creacion DEFAULT (SYSUTCDATETIME()),
    fecha_expiracion DATETIME2(0) NOT NULL,
    estado           VARCHAR(20) NOT NULL
        CONSTRAINT DF_Reserva_estado DEFAULT ('PENDIENTE'),
    identificador_cliente NVARCHAR(150) NULL,
    id_conversacion  BIGINT NULL,
    CONSTRAINT PK_Reserva PRIMARY KEY (id_reserva),
    CONSTRAINT FK_Reserva_Producto FOREIGN KEY (id_producto)
        REFERENCES dbo.Producto(id_producto),
    CONSTRAINT FK_Reserva_Conversacion FOREIGN KEY (id_conversacion)
        REFERENCES dbo.Conversacion(id_conversacion),
    CONSTRAINT CK_Reserva_cantidad CHECK (cantidad > 0),
    CONSTRAINT CK_Reserva_fechas CHECK (fecha_expiracion > fecha_creacion),
    CONSTRAINT CK_Reserva_estado CHECK
        (estado IN ('PENDIENTE','CONFIRMADA','EXPIRADA','CANCELADA'))
);
GO

CREATE INDEX IX_Reserva_producto_estado_expiracion
    ON dbo.Reserva(id_producto, estado, fecha_expiracion)
    INCLUDE (cantidad);
GO

/* =========================================================
   7. PEDIDOS Y DETALLES
   ========================================================= */

CREATE TABLE dbo.Pedido (
    id_pedido       BIGINT IDENTITY(1,1) NOT NULL,
    fecha           DATETIME2(0) NOT NULL
        CONSTRAINT DF_Pedido_fecha DEFAULT (SYSUTCDATETIME()),
    estado          VARCHAR(25) NOT NULL
        CONSTRAINT DF_Pedido_estado DEFAULT ('PENDIENTE'),
    subtotal        DECIMAL(12,2) NOT NULL
        CONSTRAINT DF_Pedido_subtotal DEFAULT (0),
    total           DECIMAL(12,2) NOT NULL
        CONSTRAINT DF_Pedido_total DEFAULT (0),
    moneda          CHAR(3) NOT NULL
        CONSTRAINT DF_Pedido_moneda DEFAULT ('GTQ'),
    nombre_cliente  NVARCHAR(150) NULL,
    correo_cliente  NVARCHAR(254) NULL,
    telefono_cliente NVARCHAR(40) NULL,
    clave_idempotencia UNIQUEIDENTIFIER NOT NULL
        CONSTRAINT DF_Pedido_idempotencia DEFAULT (NEWID()),
    CONSTRAINT PK_Pedido PRIMARY KEY (id_pedido),
    CONSTRAINT UQ_Pedido_idempotencia UNIQUE (clave_idempotencia),
    CONSTRAINT CK_Pedido_estado CHECK
        (estado IN ('PENDIENTE','CONFIRMADO','PAGADO','CANCELADO','RECHAZADO')),
    CONSTRAINT CK_Pedido_importes CHECK
        (subtotal >= 0 AND total >= 0 AND total >= subtotal),
    CONSTRAINT CK_Pedido_moneda CHECK (moneda IN ('GTQ','USD'))
);
GO

CREATE TABLE dbo.DetallePedido (
    id_detalle       BIGINT IDENTITY(1,1) NOT NULL,
    id_pedido        BIGINT NOT NULL,
    id_producto      INT NOT NULL,
    cantidad         INT NOT NULL,
    precio_unitario_historico DECIMAL(12,2) NOT NULL,
    CONSTRAINT PK_DetallePedido PRIMARY KEY (id_detalle),
    CONSTRAINT FK_DetallePedido_Pedido FOREIGN KEY (id_pedido)
        REFERENCES dbo.Pedido(id_pedido),
    CONSTRAINT FK_DetallePedido_Producto FOREIGN KEY (id_producto)
        REFERENCES dbo.Producto(id_producto),
    CONSTRAINT CK_DetallePedido_cantidad CHECK (cantidad > 0),
    CONSTRAINT CK_DetallePedido_precio CHECK (precio_unitario_historico >= 0)
);
GO

CREATE INDEX IX_DetallePedido_pedido ON dbo.DetallePedido(id_pedido);
CREATE INDEX IX_DetallePedido_producto ON dbo.DetallePedido(id_producto);
GO

/* =========================================================
   8. VISTA DE DISPONIBILIDAD
   Disponible = stock físico - reservas pendientes vigentes.
   Esta vista calcula disponibilidad al consultarla. Las
   operaciones de escritura deben volver a validarla bajo
   bloqueo/transacción para evitar sobreventa.
   ========================================================= */

CREATE VIEW dbo.vw_DisponibilidadProducto
AS
SELECT
    p.id_producto,
    p.sku,
    p.nombre,
    p.precio,
    p.moneda,
    p.estado AS estado_producto,
    ISNULL(i.stock_actual, 0) AS stock_fisico,
    ISNULL(r.cantidad_reservada, 0) AS cantidad_reservada,
    CASE
        WHEN ISNULL(i.stock_actual, 0) - ISNULL(r.cantidad_reservada, 0) < 0
            THEN 0
        ELSE ISNULL(i.stock_actual, 0) - ISNULL(r.cantidad_reservada, 0)
    END AS stock_disponible
FROM dbo.Producto AS p
LEFT JOIN dbo.Inventario AS i
    ON i.id_producto = p.id_producto
OUTER APPLY (
    SELECT SUM(rv.cantidad) AS cantidad_reservada
    FROM dbo.Reserva AS rv
    WHERE rv.id_producto = p.id_producto
      AND rv.estado = 'PENDIENTE'
      AND rv.fecha_expiracion > SYSUTCDATETIME()
) AS r;
GO

/* =========================================================
   9. DATOS INICIALES OPCIONALES: CATEGORIAS Y ATRIBUTOS
   Ejecutar una sola vez; se omiten duplicados.
   ========================================================= */

IF NOT EXISTS (SELECT 1 FROM dbo.Categoria WHERE nombre = N'Redes y conectividad' AND id_categoria_padre IS NULL)
    INSERT dbo.Categoria(nombre, descripcion) VALUES
    (N'Redes y conectividad', N'Routers, switches, puntos de acceso, cableado y fibra óptica');

IF NOT EXISTS (SELECT 1 FROM dbo.Categoria WHERE nombre = N'Telecomunicaciones' AND id_categoria_padre IS NULL)
    INSERT dbo.Categoria(nombre, descripcion) VALUES
    (N'Telecomunicaciones', N'ONT, OLT, transceptores y equipos de telecomunicación');

IF NOT EXISTS (SELECT 1 FROM dbo.Categoria WHERE nombre = N'Computación' AND id_categoria_padre IS NULL)
    INSERT dbo.Categoria(nombre, descripcion) VALUES
    (N'Computación', N'Procesadores, memoria RAM, almacenamiento y componentes');

IF NOT EXISTS (SELECT 1 FROM dbo.Categoria WHERE nombre = N'Seguridad y comunicación' AND id_categoria_padre IS NULL)
    INSERT dbo.Categoria(nombre, descripcion) VALUES
    (N'Seguridad y comunicación', N'Cámaras IP, NVR, teléfonos IP y equipos PoE');

IF NOT EXISTS (SELECT 1 FROM dbo.AtributoTecnico WHERE nombre = N'Cantidad de puertos')
    INSERT dbo.AtributoTecnico(nombre, tipo_dato, unidad, descripcion)
    VALUES (N'Cantidad de puertos','NUMERO',N'puertos',N'Número de puertos físicos');

IF NOT EXISTS (SELECT 1 FROM dbo.AtributoTecnico WHERE nombre = N'Administrable')
    INSERT dbo.AtributoTecnico(nombre, tipo_dato, unidad, descripcion)
    VALUES (N'Administrable','BOOLEANO',NULL,N'Indica si el equipo permite administración');

IF NOT EXISTS (SELECT 1 FROM dbo.AtributoTecnico WHERE nombre = N'PoE+')
    INSERT dbo.AtributoTecnico(nombre, tipo_dato, unidad, descripcion)
    VALUES (N'PoE+','BOOLEANO',NULL,N'Indica soporte para PoE+');

IF NOT EXISTS (SELECT 1 FROM dbo.AtributoTecnico WHERE nombre = N'Velocidad de red')
    INSERT dbo.AtributoTecnico(nombre, tipo_dato, unidad, descripcion)
    VALUES (N'Velocidad de red','NUMERO',N'Mbps',N'Velocidad de interfaz de red');

GO

/* =========================================================
   10. CONSULTAS DE VERIFICACION
   ========================================================= */

-- Productos activos y su disponibilidad:
-- SELECT * FROM dbo.vw_DisponibilidadProducto
-- WHERE estado_producto = 'ACTIVO'
-- ORDER BY nombre;

-- Consultar especificaciones técnicas de un producto:
-- SELECT p.nombre, a.nombre AS atributo, a.unidad,
--        pa.valor_texto, pa.valor_numero, pa.valor_booleano, pa.valor_fecha
-- FROM dbo.Producto AS p
-- JOIN dbo.ProductoAtributo AS pa ON pa.id_producto = p.id_producto
-- JOIN dbo.AtributoTecnico AS a ON a.id_atributo = pa.id_atributo
-- WHERE p.id_producto = 1;

-- Importante: este esquema no implementa por sí solo una compra segura.
-- Las operaciones de reserva y compra deben ejecutar validación de stock,
-- escritura de pedido/detalles y actualización del inventario de forma
-- atómica en una transacción con bloqueo apropiado