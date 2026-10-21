USE Sistema_Pedidos;

/*===========================================================
 TABLA: PROVEEDORES
 Descripción:
 Almacena la información de los proveedores.

 Clave Primaria:
    PK -> IdProveedor

 Relaciones:
    Proveedores (1) ------ (M) Pedidos
===========================================================*/

CREATE TABLE Proveedores(
IdProveedor int not null,
Proveedor nvarchar(20)

CONSTRAINT PK_IdProveedor PRIMARY KEY (IdProveedor)
);
/*===========================================================
 TABLA: PRODUCTOS
 Descripción:
 Almacena la información de los productos.

 Clave Primaria:
    PK -> IdProducto

 Relaciones:
    Productos (1) ------ (M) Pedidos
===========================================================*/
CREATE TABLE Productos(
IdProducto int not null,
Producto nvarchar(20),
Categoria nvarchar(20)

CONSTRAINT PK_IdProducto PRIMARY KEY (IdProducto)
);
/*===========================================================
 TABLA: CLIENTES
 Descripción:
 Almacena la información de los clientes.

 Clave Primaria:
    PK -> IdCliente

 Relaciones:
    Clientes (1) ------ (M) Pedidos
===========================================================*/

CREATE TABLE Clientes(
IdCliente int not null,
Cliente nvarchar(20),
Telefono nvarchar(11),
Direccion nvarchar(20)

CONSTRAINT PK_IdCLiente PRIMARY KEY (IdCliente)
);
/*===========================================================
 TABLA: PEDIDOS
 Descripción:
 Almacena la información de los pedidos realizados.

 Atributos:
    IdPedido
    IdProveedor
    IdProducto
    IdCliente
    PrecioCompra
    PrecioVenta
    Cantidad
    Descuento
    Fecha

 Clave Primaria:
    PK -> IdPedido

 Claves Foráneas:
    FK -> IdProveedor referencia Proveedores(IdProveedor)
    FK -> IdProducto referencia Productos(IdProducto)
    FK -> IdCliente referencia Clientes(IdCliente)

 Relaciones:
    Pedidos (M) ------ (1) Proveedores
    Pedidos (M) ------ (1) Productos
    Pedidos (M) ------ (1) Clientes
===========================================================*/
CREATE TABLE Pedidos(
IdPedido int not null,
IdProveedor int not null,
IdProducto int not null,
IdCliente int not null,
PrecioCompra int not null,
PrecioVenta int not null,
Cantidad int not null,
Descuento int not null, 
Fecha date not null

CONSTRAINT PK_IdPedido PRIMARY KEY (IdPedido)
CONSTRAINT FK_IdProveedor FOREIGN KEY (IdProveedor) REFERENCES Proveedores(IdProveedor),
CONSTRAINT FK_IdProducto FOREIGN KEY (IdProducto) REFERENCES Productos(IdProducto),
CONSTRAINT FK_IdCliente FOREIGN KEY (IdCliente) REFERENCES Clientes(IdCliente)
);




