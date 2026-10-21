/*
====================================================
TAREA9 - REPASO INTEGRADOR

NOMBRE:Mario Renato Monroy Cortez
CARNET:23000140

======================================================
*/
USE NorthWind;
GO;
-- =====================================================================
-- 1. SUBCONSULTAS
-- =====================================================================

--- Created by GitHub Copilot in SSMS - review carefully before executing
USE NorthWind;

-- =====================================================================
-- 1. SUBCONSULTAS
-- =====================================================================

-- 1.1 Productos más caros que el promedio general
SELECT IDProducto AS Producto, NombreProducto, PrecioUnitario
FROM Producto
WHERE PrecioUnitario > (SELECT AVG(PrecioUnitario) FROM Producto);

-- 1.2 Clientes que han realizado pedidos
SELECT c.IDCliente, c.NombreEmpresa
FROM Cliente c
WHERE EXISTS (SELECT 1 FROM Pedido p WHERE p.IDCliente = c.IDCliente);

-- 1.3 Productos más caros que el promedio de su categoría (correlacionada)
SELECT pr.IDProducto, pr.NombreProducto, pr.PrecioUnitario, pr.IDCategoria
FROM Producto pr
WHERE pr.PrecioUnitario > (
    SELECT AVG(p2.PrecioUnitario)
    FROM Producto p2
    WHERE p2.IDCategoria = pr.IDCategoria
);

-- 1.4 Clientes que gastaron más que el promedio de gastos de todos los clientes
SELECT c.IDCliente, c.NombreEmpresa,
       SUM(dp.Cantidad * dp.PrecioUnitario) AS GastoTotal
FROM Cliente c
INNER JOIN Pedido pd ON pd.IDCliente = c.IDCliente
INNER JOIN DetallePedido dp ON dp.IDPedido = pd.IDPedido
GROUP BY c.IDCliente, c.NombreEmpresa
HAVING SUM(dp.Cantidad * dp.PrecioUnitario) > (
    SELECT AVG(Gasto)
    FROM (
        SELECT p2.IDCliente, SUM(dp2.Cantidad * dp2.PrecioUnitario) AS Gasto
        FROM Pedido p2
        INNER JOIN DetallePedido dp2 ON dp2.IDPedido = p2.IDPedido
        GROUP BY p2.IDCliente
    ) AS GastosClientes
);

-- =====================================================================
-- 2. JOINS
-- =====================================================================

-- 2.1 Pedidos con cliente y empleado
SELECT pd.IDPedido, c.NombreEmpresa AS Cliente,
       e.Nombre + ' ' + e.Apellido AS Empleado
FROM Pedido pd
INNER JOIN Cliente c ON c.IDCliente = pd.IDCliente
INNER JOIN Empleado e ON e.IDEmpleado = pd.IDEmpleado;

-- 2.2 Clientes sin pedidos
SELECT c.IDCliente, c.NombreEmpresa
FROM Cliente c
LEFT JOIN Pedido pd ON pd.IDCliente = c.IDCliente
WHERE pd.IDPedido IS NULL;

-- 2.3 Pedidos completos con múltiples JOINS
SELECT pd.IDPedido, c.NombreEmpresa AS Cliente,
       e.Nombre + ' ' + e.Apellido AS Empleado,
       t.NombreEmpresa AS Transportista,
       pr.NombreProducto, dp.Cantidad
FROM Pedido pd
INNER JOIN Cliente c ON c.IDCliente = pd.IDCliente
INNER JOIN Empleado e ON e.IDEmpleado = pd.IDEmpleado
INNER JOIN Transportista t ON t.IDTransportista = pd.IDTransportista
INNER JOIN DetallePedido dp ON dp.IDPedido = pd.IDPedido
INNER JOIN Producto pr ON pr.IDProducto = dp.IDProducto;

-- 2.4 Productos con total de unidades vendidas
SELECT pr.IDProducto, pr.NombreProducto,
       ISNULL(SUM(dp.Cantidad), 0) AS TotalUnidadesVendidas
FROM Producto pr
LEFT JOIN DetallePedido dp ON dp.IDProducto = pr.IDProducto
GROUP BY pr.IDProducto, pr.NombreProducto
ORDER BY TotalUnidadesVendidas DESC;

-- =====================================================================
-- 3. JOINS AVANZADOS Y OPERADORES DE CONJUNTOS
-- =====================================================================

-- 3.1 Empleados con su gerente (SELF JOIN)
SELECT e.Nombre + ' ' + e.Apellido AS Empleado,
       g.Nombre + ' ' + g.Apellido AS Gerente
FROM Empleado e
LEFT JOIN Empleado g ON g.IDEmpleado = e.ReporteA;

-- 3.2 Países únicos de clientes y proveedores (UNION)
SELECT Pais FROM Cliente
UNION
SELECT Pais FROM Proveedor;

-- 3.3 Países con clientes y proveedores (INTERSECT)
SELECT Pais FROM Cliente
INTERSECT
SELECT Pais FROM Proveedor;

-- 3.4 Empleados sin pedidos (EXCEPT)
SELECT IDEmpleado, Nombre, Apellido FROM Empleado
EXCEPT
SELECT DISTINCT e2.IDEmpleado, e2.Nombre, e2.Apellido
FROM Pedido p2
INNER JOIN Empleado e2 ON e2.IDEmpleado = p2.IDEmpleado;

-- 3.5 Combinación de productos y categorías (CROSS JOIN)
SELECT p.NombreProducto, c.NombreCategoria
FROM Producto p
CROSS JOIN Categoria c;

-- =====================================================================
-- 4. FUNCIONES NATIVAS
-- =====================================================================

-- 4.1 Funciones de cadena
SELECT UPPER(NombreEmpresa) AS NombreMayusculas,
       LEN(NombreEmpresa)   AS Longitud,
       LEFT(NombreEmpresa, 10) AS Primeros10Caracteres
FROM Cliente;

-- 4.2 Funciones de fecha
SELECT IDPedido,
       YEAR(FechaPedido)  AS Año,
       MONTH(FechaPedido) AS Mes,
       DATENAME(MONTH, FechaPedido) AS NombreMes,
       DATEDIFF(DAY, FechaPedido, FechaEnvio) AS DiasEnvio
FROM Pedido;

-- 4.3 Funciones lógicas (clasificación por precio)
SELECT IDProducto, NombreProducto, PrecioUnitario,
       CASE
           WHEN PrecioUnitario >= 50 THEN 'Premium'
           WHEN PrecioUnitario >= 20 THEN 'Estándar'
           ELSE 'Económico'
       END AS Clasificación
FROM Producto;

-- 4.4 Funciones de ventana - Ranking
SELECT IDProducto, NombreProducto, PrecioUnitario,
       RANK() OVER (ORDER BY PrecioUnitario DESC) AS PosicionRANK,
       DENSE_RANK() OVER (ORDER BY PrecioUnitario DESC) AS PosicionDENSE_RANK
FROM Producto;

-- 4.5 Funciones de ventana - Acumulados
SELECT pd.IDPedido, pd.FechaPedido,
       SUM(dp.Cantidad * dp.PrecioUnitario) AS TotalDia,
       SUM(SUM(dp.Cantidad * dp.PrecioUnitario))
           OVER (ORDER BY pd.FechaPedido) AS TotalAcumulado
FROM Pedido pd
INNER JOIN DetallePedido dp ON dp.IDPedido = pd.IDPedido
GROUP BY pd.IDPedido, pd.FechaPedido;

-- =====================================================================
-- 5. CONSULTA LIBRE AVANZADA
-- =====================================================================

SELECT c.IDCliente, c.NombreEmpresa, YEAR(pd.FechaPedido) AS Anio,
       SUM(dp.Cantidad * dp.PrecioUnitario) AS GastoTotal,
       CASE WHEN SUM(dp.Cantidad * dp.PrecioUnitario) > (
            SELECT AVG(Gasto) FROM (
                SELECT p2.IDCliente, SUM(d2.Cantidad * d2.PrecioUnitario) AS Gasto
                FROM Pedido p2
                INNER JOIN DetallePedido d2 ON d2.IDPedido = p2.IDPedido
                GROUP BY p2.IDCliente
            ) AS GastosClientes
       ) THEN 'EncimaPromedio' ELSE 'PorDebajoPromedio' END AS Comparacion
FROM Cliente c
INNER JOIN Pedido pd ON pd.IDCliente = c.IDCliente
INNER JOIN DetallePedido dp ON dp.IDPedido = pd.IDPedido
GROUP BY c.IDCliente, c.NombreEmpresa, YEAR(pd.FechaPedido);
