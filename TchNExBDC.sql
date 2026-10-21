USE TechNexoDB;
GO

/* =========================================================
   1. INSERTAR MARCAS DE PRUEBA
   ========================================================= */
IF NOT EXISTS (SELECT 1 FROM dbo.Marca WHERE nombre = N'NetCore Demo')
    INSERT dbo.Marca(nombre, descripcion)
    VALUES (N'NetCore Demo', N'Marca ficticia para pruebas académicas');

IF NOT EXISTS (SELECT 1 FROM dbo.Marca WHERE nombre = N'FiberLink Demo')
    INSERT dbo.Marca(nombre, descripcion)
    VALUES (N'FiberLink Demo', N'Marca ficticia para pruebas académicas');

IF NOT EXISTS (SELECT 1 FROM dbo.Marca WHERE nombre = N'SecureView Demo')
    INSERT dbo.Marca(nombre, descripcion)
    VALUES (N'SecureView Demo', N'Marca ficticia para pruebas académicas');
GO

/* =========================================================
   2. INSERTAR PRODUCTOS DE PRUEBA
   ========================================================= */
DECLARE @MarcaNetCore INT = (SELECT id_marca FROM dbo.Marca WHERE nombre = N'NetCore Demo');
DECLARE @MarcaFiber INT = (SELECT id_marca FROM dbo.Marca WHERE nombre = N'FiberLink Demo');
DECLARE @MarcaSecure INT = (SELECT id_marca FROM dbo.Marca WHERE nombre = N'SecureView Demo');

DECLARE @CatRedes INT = (SELECT TOP (1) id_categoria FROM dbo.Categoria WHERE nombre = N'Redes y conectividad' AND id_categoria_padre IS NULL);
DECLARE @CatTelecom INT = (SELECT TOP (1) id_categoria FROM dbo.Categoria WHERE nombre = N'Telecomunicaciones' AND id_categoria_padre IS NULL);
DECLARE @CatSeguridad INT = (SELECT TOP (1) id_categoria FROM dbo.Categoria WHERE nombre = N'Seguridad y comunicación' AND id_categoria_padre IS NULL);

IF @MarcaNetCore IS NULL OR @MarcaFiber IS NULL OR @MarcaSecure IS NULL
    THROW 50001, 'Faltan marcas de prueba. Verifica la seccion de marcas.', 1;

IF @CatRedes IS NULL OR @CatTelecom IS NULL OR @CatSeguridad IS NULL
    THROW 50002, 'Faltan categorias iniciales. Ejecuta primero el script principal.', 1;

IF NOT EXISTS (SELECT 1 FROM dbo.Producto WHERE sku = N'TNX-SW-024')
    INSERT dbo.Producto(id_marca, id_categoria, sku, nombre, descripcion, precio, moneda)
    VALUES (@MarcaNetCore, @CatRedes, N'TNX-SW-024',
            N'Switch administrable de 24 puertos Gigabit',
            N'Switch ficticio para probar filtros de puertos, administracion y PoE+.',
            1250.00, 'GTQ');

IF NOT EXISTS (SELECT 1 FROM dbo.Producto WHERE sku = N'TNX-RT-006')
    INSERT dbo.Producto(id_marca, id_categoria, sku, nombre, descripcion, precio, moneda)
    VALUES (@MarcaNetCore, @CatRedes, N'TNX-RT-006',
            N'Router Gigabit de 6 puertos',
            N'Router ficticio para pruebas de consulta del catalogo.',
            675.00, 'GTQ');

IF NOT EXISTS (SELECT 1 FROM dbo.Producto WHERE sku = N'TNX-ONT-001')
    INSERT dbo.Producto(id_marca, id_categoria, sku, nombre, descripcion, precio, moneda)
    VALUES (@MarcaFiber, @CatTelecom, N'TNX-ONT-001',
            N'ONT GPON de 1 puerto Gigabit',
            N'Terminal de red optica ficticia para pruebas.',
            320.00, 'GTQ');

IF NOT EXISTS (SELECT 1 FROM dbo.Producto WHERE sku = N'TNX-CAM-IP')
    INSERT dbo.Producto(id_marca, id_categoria, sku, nombre, descripcion, precio, moneda)
    VALUES (@MarcaSecure, @CatSeguridad, N'TNX-CAM-IP',
            N'Camara IP de red',
            N'Camara ficticia para pruebas de catalogo.',
            450.00, 'GTQ');
GO

/* =========================================================
   3. INICIALIZAR INVENTARIO DE LOS PRODUCTOS DE PRUEBA
   ========================================================= */
INSERT dbo.Inventario(id_producto, stock_actual, stock_minimo, ubicacion)
SELECT p.id_producto,
       CASE p.sku
           WHEN N'TNX-SW-024' THEN 8
           WHEN N'TNX-RT-006' THEN 12
           WHEN N'TNX-ONT-001' THEN 20
           WHEN N'TNX-CAM-IP' THEN 5
       END,
       CASE p.sku
           WHEN N'TNX-SW-024' THEN 2
           WHEN N'TNX-RT-006' THEN 3
           WHEN N'TNX-ONT-001' THEN 5
           WHEN N'TNX-CAM-IP' THEN 2
       END,
       N'Almacen principal - demo'
FROM dbo.Producto p
WHERE p.sku IN (N'TNX-SW-024', N'TNX-RT-006', N'TNX-ONT-001', N'TNX-CAM-IP')
  AND NOT EXISTS (
      SELECT 1 FROM dbo.Inventario i WHERE i.id_producto = p.id_producto
  );
GO

/* =========================================================
   4. ASEGURAR ATRIBUTOS TECNICOS DE PRUEBA
   ========================================================= */
IF NOT EXISTS (SELECT 1 FROM dbo.AtributoTecnico WHERE nombre = N'Cantidad de puertos')
    INSERT dbo.AtributoTecnico(nombre, tipo_dato, unidad)
    VALUES (N'Cantidad de puertos','NUMERO',N'puertos');

IF NOT EXISTS (SELECT 1 FROM dbo.AtributoTecnico WHERE nombre = N'Administrable')
    INSERT dbo.AtributoTecnico(nombre, tipo_dato, unidad)
    VALUES (N'Administrable','BOOLEANO',NULL);

IF NOT EXISTS (SELECT 1 FROM dbo.AtributoTecnico WHERE nombre = N'PoE+')
    INSERT dbo.AtributoTecnico(nombre, tipo_dato, unidad)
    VALUES (N'PoE+','BOOLEANO',NULL);

IF NOT EXISTS (SELECT 1 FROM dbo.AtributoTecnico WHERE nombre = N'Velocidad de red')
    INSERT dbo.AtributoTecnico(nombre, tipo_dato, unidad)
    VALUES (N'Velocidad de red','NUMERO',N'Mbps');

IF NOT EXISTS (SELECT 1 FROM dbo.AtributoTecnico WHERE nombre = N'Tecnologia optica')
    INSERT dbo.AtributoTecnico(nombre, tipo_dato, unidad)
    VALUES (N'Tecnologia optica','TEXTO',NULL);
GO

/* =========================================================
   5. ASOCIAR VALORES TECNICOS A PRODUCTOS
   Cada atributo debe coincidir con el tipo de dato definido.
   ========================================================= */
DECLARE @AtributoPuertos INT = (SELECT id_atributo FROM dbo.AtributoTecnico WHERE nombre = N'Cantidad de puertos');
DECLARE @AtributoAdmin INT = (SELECT id_atributo FROM dbo.AtributoTecnico WHERE nombre = N'Administrable');
DECLARE @AtributoPoe INT = (SELECT id_atributo FROM dbo.AtributoTecnico WHERE nombre = N'PoE+');
DECLARE @AtributoVelocidad INT = (SELECT id_atributo FROM dbo.AtributoTecnico WHERE nombre = N'Velocidad de red');
DECLARE @AtributoOptica INT = (SELECT id_atributo FROM dbo.AtributoTecnico WHERE nombre = N'Tecnologia optica');

DECLARE @IdSwitch INT = (SELECT id_producto FROM dbo.Producto WHERE sku = N'TNX-SW-024');
DECLARE @IdRouter INT = (SELECT id_producto FROM dbo.Producto WHERE sku = N'TNX-RT-006');
DECLARE @IdOnt INT = (SELECT id_producto FROM dbo.Producto WHERE sku = N'TNX-ONT-001');
DECLARE @IdCamara INT = (SELECT id_producto FROM dbo.Producto WHERE sku = N'TNX-CAM-IP');

-- Switch: 24 puertos, administrable, PoE+, 1000 Mbps
IF @IdSwitch IS NOT NULL AND @AtributoPuertos IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM dbo.ProductoAtributo WHERE id_producto = @IdSwitch AND id_atributo = @AtributoPuertos)
    INSERT dbo.ProductoAtributo(id_producto, id_atributo, valor_numero)
    VALUES (@IdSwitch, @AtributoPuertos, 24);

IF @IdSwitch IS NOT NULL AND @AtributoAdmin IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM dbo.ProductoAtributo WHERE id_producto = @IdSwitch AND id_atributo = @AtributoAdmin)
    INSERT dbo.ProductoAtributo(id_producto, id_atributo, valor_booleano)
    VALUES (@IdSwitch, @AtributoAdmin, 1);

IF @IdSwitch IS NOT NULL AND @AtributoPoe IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM dbo.ProductoAtributo WHERE id_producto = @IdSwitch AND id_atributo = @AtributoPoe)
    INSERT dbo.ProductoAtributo(id_producto, id_atributo, valor_booleano)
    VALUES (@IdSwitch, @AtributoPoe, 1);

IF @IdSwitch IS NOT NULL AND @AtributoVelocidad IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM dbo.ProductoAtributo WHERE id_producto = @IdSwitch AND id_atributo = @AtributoVelocidad)
    INSERT dbo.ProductoAtributo(id_producto, id_atributo, valor_numero)
    VALUES (@IdSwitch, @AtributoVelocidad, 1000);

-- Router: 6 puertos, no administrable en esta demo, 1000 Mbps
IF @IdRouter IS NOT NULL AND @AtributoPuertos IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM dbo.ProductoAtributo WHERE id_producto = @IdRouter AND id_atributo = @AtributoPuertos)
    INSERT dbo.ProductoAtributo(id_producto, id_atributo, valor_numero)
    VALUES (@IdRouter, @AtributoPuertos, 6);

IF @IdRouter IS NOT NULL AND @AtributoAdmin IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM dbo.ProductoAtributo WHERE id_producto = @IdRouter AND id_atributo = @AtributoAdmin)
    INSERT dbo.ProductoAtributo(id_producto, id_atributo, valor_booleano)
    VALUES (@IdRouter, @AtributoAdmin, 0);

IF @IdRouter IS NOT NULL AND @AtributoVelocidad IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM dbo.ProductoAtributo WHERE id_producto = @IdRouter AND id_atributo = @AtributoVelocidad)
    INSERT dbo.ProductoAtributo(id_producto, id_atributo, valor_numero)
    VALUES (@IdRouter, @AtributoVelocidad, 1000);

-- ONT: 1 puerto, tecnología GPON
IF @IdOnt IS NOT NULL AND @AtributoPuertos IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM dbo.ProductoAtributo WHERE id_producto = @IdOnt AND id_atributo = @AtributoPuertos)
    INSERT dbo.ProductoAtributo(id_producto, id_atributo, valor_numero)
    VALUES (@IdOnt, @AtributoPuertos, 1);

IF @IdOnt IS NOT NULL AND @AtributoOptica IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM dbo.ProductoAtributo WHERE id_producto = @IdOnt AND id_atributo = @AtributoOptica)
    INSERT dbo.ProductoAtributo(id_producto, id_atributo, valor_texto)
    VALUES (@IdOnt, @AtributoOptica, N'GPON');

-- Cámara: 1 interfaz de red
IF @IdCamara IS NOT NULL AND @AtributoPuertos IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM dbo.ProductoAtributo WHERE id_producto = @IdCamara AND id_atributo = @AtributoPuertos)
    INSERT dbo.ProductoAtributo(id_producto, id_atributo, valor_numero)
    VALUES (@IdCamara, @AtributoPuertos, 1);
GO

/* =========================================================
   6. CONSULTAS DE VERIFICACION
   Ejecuta cada bloque por separado para observar resultados.
   ========================================================= */

-- A. Catalogo general con marca, categoria y disponibilidad
SELECT
    p.id_producto,
    p.sku,
    p.nombre,
    m.nombre AS marca,
    c.nombre AS categoria,
    p.precio,
    p.moneda,
    d.stock_fisico,
    d.cantidad_reservada,
    d.stock_disponible,
    p.estado
FROM dbo.Producto p
JOIN dbo.Marca m ON m.id_marca = p.id_marca
JOIN dbo.Categoria c ON c.id_categoria = p.id_categoria
JOIN dbo.vw_DisponibilidadProducto d ON d.id_producto = p.id_producto
ORDER BY p.nombre;
GO

-- B. Ficha técnica de cada producto (filas por atributo)
SELECT
    p.sku,
    p.nombre AS producto,
    a.nombre AS atributo,
    pa.valor_texto,
    pa.valor_numero,
    pa.valor_booleano,
    pa.valor_fecha,
    a.unidad
FROM dbo.Producto p
JOIN dbo.ProductoAtributo pa ON pa.id_producto = p.id_producto
JOIN dbo.AtributoTecnico a ON a.id_atributo = pa.id_atributo
ORDER BY p.sku, a.nombre;
GO

-- C. Consulta tipo chatbot: switch administrable, PoE+, 24 puertos,
--    Gigabit o superior, presupuesto maximo Q1,500.
SELECT DISTINCT
    p.id_producto, p.sku, p.nombre, p.precio, p.moneda,
    d.stock_disponible
FROM dbo.Producto p
JOIN dbo.vw_DisponibilidadProducto d ON d.id_producto = p.id_producto
JOIN dbo.ProductoAtributo pu ON pu.id_producto = p.id_producto
JOIN dbo.AtributoTecnico au ON au.id_atributo = pu.id_atributo
JOIN dbo.ProductoAtributo ad ON ad.id_producto = p.id_producto
JOIN dbo.AtributoTecnico aa ON aa.id_atributo = ad.id_atributo
JOIN dbo.ProductoAtributo pp ON pp.id_producto = p.id_producto
JOIN dbo.AtributoTecnico ap ON ap.id_atributo = pp.id_atributo
JOIN dbo.ProductoAtributo ve ON ve.id_producto = p.id_producto
JOIN dbo.AtributoTecnico av ON av.id_atributo = ve.id_atributo
WHERE p.estado = 'ACTIVO'
  AND p.precio <= 1500
  AND d.stock_disponible > 0
  AND au.nombre = N'Cantidad de puertos' AND pu.valor_numero >= 24
  AND aa.nombre = N'Administrable' AND ad.valor_booleano = 1
  AND ap.nombre = N'PoE+' AND pp.valor_booleano = 1
  AND av.nombre = N'Velocidad de red' AND ve.valor_numero >= 1000;
GO

-- D. Búsqueda por nombre, SKU o marca (ejemplo)
DECLARE @Busqueda NVARCHAR(100) = N'switch';
SELECT p.sku, p.nombre, m.nombre AS marca, p.precio, p.moneda
FROM dbo.Producto p
JOIN dbo.Marca m ON m.id_marca = p.id_marca
WHERE p.estado = 'ACTIVO'
  AND (
      p.nombre LIKE N'%' + @Busqueda + N'%'
      OR p.sku LIKE N'%' + @Busqueda + N'%'
      OR m.nombre LIKE N'%' + @Busqueda + N'%'
  )
ORDER BY p.nombre;
GO

-- E. Productos que requieren reposicion (stock fisico menor o igual al minimo)
SELECT p.sku, p.nombre, i.stock_actual, i.stock_minimo
FROM dbo.Producto p
JOIN dbo.Inventario i ON i.id_producto = p.id_producto
WHERE i.stock_actual <= i.stock_minimo
ORDER BY p.nombre;
GO