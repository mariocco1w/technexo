--1.una consulta con subconsulta correlacionada que muestre productos con precio superior al promedio de su categoria--
SELECT p1.NombreProducto, p1.PrecioUnitario, p1.IDCategoria,
    CASE
        WHEN p1.PrecioUnitario > (
            SELECT AVG(p2.PrecioUnitario)
            FROM Producto p2
            WHERE p2.IDCategoria = p1.IDCategoria
        ) THEN 'Superior'
        ELSE 'PorDebajo'
    END AS Producto
FROM Producto p1;
--2.una consulta con left joi que muestre todos los empleados y los pedidos que han atendido(Incluyendo los que no tiene)

--3.Una consulta con UNION que combine la lista de paises de clientes y proveedores
select pais from proveedor
union
select pais from cliente
--4.Una consulta con funcion de ventana que muestre el ranking de productos por precio dentro de cada categoria


--