USE NorthWind;

/*CREATE TABLE Cliente(
IDCliente nvarchar(5) not null,
NombreEmpresa nvarchar(40) not null,
NombreContacto nvarchar(30),
CargoContacto nvarchar (30),
Direccion nvarchar(60),
Ciudad nvarchar(15),
Region nvarchar(15),
CodigoPostal nvarchar(10),
Pais nvarchar(15),
Telefono nvarchar(24),
Fax nvarchar (24),

CONSTRAINT PK_IDCliente PRIMARY KEY(IDCliente)

);

CREATE TABLE Empleado(
IDEmpleado int not null,
Apellido nvarchar(20) not null,
Nombre nvarchar(10) not null,
Cargo nvarchar(30),
Tratamiento nvarchar(25),
FechaNacimiento datetime,
FechaContratacion datetime,
Direccion nvarchar(60),
Ciudad nvarchar(15),
Region nvarchar(15),
CodigoPostal nvarchar(10),
Pais nvarchar(15),
TelefonoDomicilio nvarchar(24),
Extension nvarchar(4),
Notas ntext,
ReporteA int,


CONSTRAINT PK_Empleado PRIMARY KEY (IDEmpleado),
CONSTRAINT FK_Empleado_ReporteA FOREIGN KEY (ReporteA) REFERENCES Empleado(IDEmpleado)

);


CREATE TABLE Transportista(
IDTransportista int not null,
NombreEmpresa Nvarchar(40) not null,
Telefono nvarchar(24),

CONSTRAINT PK_IDTransportista PRIMARY KEY (IDTransportista)

);

CREATE TABLE Categoria(
IDCategoria int not null,
NombreCategoria nvarchar(15) not null,
Descripcion ntext,
Imagen image,

CONSTRAINT PK_IDCategoria PRIMARY KEY (IDCategoria)

);

CREATE TABLE Proveedor(
IDProveedor int not null,
NombreEmpresa nvarchar(40) not null,
NombreContacto nvarchar(30),
CargoContacto nvarchar (30),
Direccion nvarchar(60),
Ciudad nvarchar(15),
Region nvarchar(15),
CodigoPostal nvarchar(10),
Pais nvarchar(15),
Telefono nvarchar(24),
Fax nvarchar (24),

CONSTRAINT PK_IDProveedor PRIMARY KEY (IDProveedor)
);

CREATE TABLE Producto(
IDProducto int not null,
NombreProducto nvarchar(40),
IDProveedor int,
IDCategoria int,
CantidadPorUnidad nvarchar(20),
PrecioUnitario money,
UnidadeStock smallint,
UnidadesPedido smallint,
NivelReordenar smallint,
Descontinuado bit,

CONSTRAINT PK_IDProducto PRIMARY KEY (IDProducto),
CONSTRAINT FK_Producto_proveedor FOREIGN KEY (IDProveedor) REFERENCES Proveedor(IDProveedor),
CONSTRAINT FK_Producto_Categoria FOREIGN KEY (IDCategoria) REFERENCES Categoria(IDCategoria)
);

CREATE TABLE Pedido(
IDPedido int not null,
IDCliente nvarchar(5),
IDEmpleado int not null,
FechaPedido datetime,
FechaRequerida datetime,
FechaEnvio datetime,
IDTransportista int not null,
FLete money,
NombreEnvio nvarchar(40),
DireccionEnvio nvarchar(60),
CiudadEnvio nvarchar(15),
RegionEnvio nvarchar(15),
CodigoPostalEnvio nvarchar(10),
PaisEnvio nvarchar(15),
Telefono nvarchar(24),
Fax nvarchar (24),

CONSTRAINT PK_Pedido PRIMARY KEY (IDPedido),
CONSTRAINT FK_Pedido_cliente FOREIGN KEY (IDCliente) REFERENCES  Cliente(IDCliente),
CONSTRAINT FK_Empleado FOREIGN KEY (IDEmpleado) REFERENCES Empleado(IDEmpleado),
CONSTRAINT FK_Pedido_Transportista FOREIGN KEY (IDTransportista) REFERENCES Transportista(IDTransportista)


);

CREATE TABLE DetallePedido(
IDPedido int not null,
IDProducto int not null,
PrecioUnitario money not null,
Cantidad smallint not null,
Descuento real not null,

CONSTRAINT PK_DetallePedido PRIMARY KEY (IDPedido, IDProducto),
CONSTRAINT FK_DetallePedido_Pedido FOREIGN KEY (IDPedido) REFERENCES Pedido(IDPedido),
CONSTRAINT FK_DetallePedido_Producto FOREIGN KEY (IDProducto) REFERENCES Producto(IDProducto)


);

CREATE INDEX IX_DetallePedido_IDProducto ON DetallePedido(IDProducto);
CREATE INDEx IX_Pedido_IDCliente ON Pedido(IDCliente);
CREATE INDEX IX_Pedido_IDEmpleado ON Pedido(IDEmpleado);
CREATE INDEX IX_Pedido_IDTransportista ON Pedido(IDTransportista);
CREATE INDEX IX_Producto_IDCategoria ON Producto(IDCategoria);
CREATE INDEX IX_Pedido_IDProveedor ON Producto(IDProveedor);*/




INSERT INTO Categoria(IDCategoria, NombreCategoria, Descripcion)
VALUES
	(1,'Bebidas','REFRESCOS,CAFE,TES,CERVEZA Y BEBIDAS DE MALTA'),
	(2,'CONDIMENTACION','SALSAS DULCES Y SALADAS, CONDIMENTOS, ADEREZOS Y ESPECIES'),
	(3,'LACTEOS','QUESOS,LECHE, YOUGURT'),
	(4,'REPOSTERIA','DULCES,PANES,GALLETAS, PASTELES'),
	(5,'CARNES','CARNES PREPARADAS Y EN CONSERVA'),
	(6,'PESCADOS','PESCADOS Y MARISCOS'),
	(7,'FURTAS','FRUTAS Y VERDURAS FRESCAS Y EN CONSERVA'),
	(8,'GRANOS','PASTAS,CEREALES Y GRANOS')
;

INSERT INTO Proveedor(IDProveedor, NombreEmpresa,NombreContacto, Pais)
VALUES 
	(1,'PROVEEDOR DEL NORTE','ANA TRUJILLO','ESPAÑA'),
	(2,'DISTRIBUIDORA DEL SUR','LUIS PEREZ', 'MEXICO'),
	(3,'IMPORTADORA CENTRAL','CARLOS PEREZ','ARGENTINA')

;

INSERT INTO Transportista(IDTransportista, NombreEmpresa, Telefono)
VALUES 
	(1,'TRANSPORTE RAPIDOS','123-456'),
	(2,'LOGISTICA GLOBAL','555-5678'),
	(3,'ENVIOS EXPRES','333-9012');

INSERT INTO Empleado(IDEmpleado, Apellido, Nombre, Cargo, FechaContratacion)
VALUES
	(1,'GARCIA','JUAN','GERENTE GENERAL','2020-01-15'),
	(2,'MARTINEZ','MARIA','SUPERVISORA','2021-03-10'),
	(3,'LOPEZ','CARLOS','VENDEDOR','2022-06-01')


;


INSERT INTO Cliente(IDCliente, NombreEmpresa, NombreContacto, Ciudad, Pais)
VALUES
	('1','ASDFR','ALFRED FUTTERKISTE','ALFRED SCHMIT','BERLIN'),
	('2','ASSDF','ANA TRUJILLO EMPAREDADOS','ANA TRUJILLO','MEXICO DF'),
	('3','TRASA','','ALFRED SCHMIT','BERLIN')
;

INSERT INTO Producto(IDProducto, NombreProducto, IDProveedor, IDCategoria, PrecioUnitario, UnidadeStock, UnidadesPedido, NivelReordenar, Descontinuado)
VALUES
	(1,'CAFE DE COLOMBIA',1,1,18.00,39,0,10,0),
	(2,'QUESO MANCHEGO',2,3,19.00,17,40,25,0),
	(3,'SALSA DE CHILE',3,2,10.00,120,0,0,0)
;

INSERT INTO Pedido(IDPedido, IDCliente, IDEmpleado, FechaPedido, FechaRequerida, FechaEnvio, IDTransportista, NombreEnvio)
VALUES
	(1,'1',1,'2024-01-15','2024-01-20','2024-01-18',1,'ALFRED FUTTERKISTE'),
	(2,'2',2,'2024-02-03','2024-02-10','2024-02-08',2,'ANA TRUJILLO EMPAREDADOS'),
	(3,'3',3,'2024-02-10','2024-02-17',NULL,3,'TRASA'),
	(4,'1',2,'2024-03-01','2024-03-08','2024-03-05',2,'ALFRED FUTTERKISTE')
;

INSERT INTO DetallePedido(IDPedido, IDProducto, PrecioUnitario, Cantidad, Descuento)
VALUES
	(1,1,18.00,10,0),
	(1,2,19.00,20,0.05),
	(2,1,18.00,15,0),
	(2,3,10.00,5,0.10),
	(3,2,19.00,12,0),
	(4,1,18.00,8,0),
	(4,3,10.00,25,0.15)
;

	