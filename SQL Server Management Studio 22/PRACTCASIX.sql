USE NorthWind;
--1.1 PRODUCTO MAS CAROS QUE EL PROMEDIO GENERAL--
Select NombreProducto, PrecioUnitario
From Producto
Where PrecioUnitario > (Select AVG(PrecioUnitario)From
						Producto) ORDER BY PrecioUnitario DESC;

--1.2 CLEINTES QUE HAN REALIZADO PEDIDOS--	
SELECT NombreEmpresa
FROM Cliente 
WHERE IDCliente IN (
					SELECT Distinct IDCliente FROM Pedido);

--1.3 PRODUCTOS MAS CAROS QUE EL PROMEDIO DE SU CATEGORIA --

SELECT p1.NombreProducto, p1.PrecioUnitario, IDCategoria 
FROM Producto p1
WHERE p1.PrecioUnitario > (SELECT AVG(p2.PrecioUnitario)
					FROM Producto p2
					WHERE p2.IDCategoria = p1.IDCategoria)

--2.1 PEDIDOS CON CLIENTE Y EMPLEADO (INNER JOIN)--

SELECT p.IDPedido, p.FechaPedido, c.NombreEmpresa
		AS Cliente,
		e.Nombre + ' ' + e.Apellido AS Empleado
FROM Pedido p
INNER JOIN Cliente c ON p.IDCliente = c.IDCLIENTE
INNER JOIN Empleado e ON p.IDEmpleado = e.IDEmpleado
ORDER BY p.FechaPedido DESC;

--2.2 Cliente sin Pedios (LEFT JOIN)

SELECT c.NombreEmpresa
FROM Cliente c
LEFT JOIN Pedido p ON c.IDCliente = p.IDCliente
WHERE p.IDPEdido IS NULL

--3.1 EMPLEADOS CON SU GERENTE (LEFT JOIN)--

SELECT e.Nombre + ' ' + e.Apellido AS Empleado,
		g.Nombre + ' ' + g.Apellido AS Gerente

FROM Empleado e
LEFT JOIN Empleado g ON e.ReporteA = g.IDEmpleado;

--3.2 PAISESE CON CLIENTE SO PREOVEEDORES

SELECT Pais FROM Cliente
UNION 
SELECT Pais FROM Proveedor
ORDER BY Pais;

--3.3 EMPLEADOS SIN PEDIDOS (EXCEPT)
SELECT IDEmpleado,Nombre + ' ' + Apellido AS Empleado 
FROM Empleado
WHERE  IDEmpleado NOT IN (SELECT Distinct IDEmpleado 
							FROM Pedido);
--4.1 REPORTE CON FUNCIONES DE CADENA-- 
SELECT 
UPPER(NombreEmpresa) AS NombreMayusculas,
LEN(NombreEmpresa) AS Longitud,
SUBSTRING(NombreEmpresa,1,10) AS PrimerosDiez
FROM CLiente;

--4.2 REPORTE CON FUNCIONES DE FECHA

SELECT IDPedido,
YEAR(FechaPedido) AS AÑO,
DATENAME(MONTH,FechaPedido) AS MES,
DATEDIFF(DAY,FechaPedido,FechaEnvio) AS DiasEnvio
FROM Pedido 
WHERE FechaEnvio IS NOT NULL;

--4.3 CLASIFICACION CON CASE--
SELECT NombreProducto, PrecioUnitario,
CASE 
	WHEN PrecioUnitario >= 85 THEN 'Premium'
	WHEN PrecioUnitario >= 50 THEN 'Estandar'
	ELSE 'Economico'
END AS Categoria
FROM Producto;

--4.4 RANKING  CON FUNCIONES D EVENTANA
SELECT NombreProducto, PrecioUnitario,
ROW_NUMBER() OVER (ORDER BY PrecioUnitario DESC) AS Fila,
RANK() OVER (ORDER BY PrecioUnitario DESC) AS Ranking,
DENSE_RANK() OVER (ORDER BY PrecioUnitario DESC) AS RankingDenso
FROM Producto;