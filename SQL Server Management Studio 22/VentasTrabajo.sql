USE Venta_TrabajoSemanal3;

CREATE TABLE Cliente(
    Cliente NVARCHAR(30) NOT NULL,
    Direccion NVARCHAR(50) NOT NULL,
    Telefono NVARCHAR(15) NOT NULL,

    CONSTRAINT PK_Cliente
        PRIMARY KEY (Cliente)
);

CREATE TABLE Producto(
    Producto NVARCHAR(30) NOT NULL,
    Categoria NVARCHAR(30) NOT NULL,
    Precio_Unitario DECIMAL(10,2) NOT NULL,

    CONSTRAINT PK_Producto
        PRIMARY KEY (Producto)
);

CREATE TABLE Venta_Producto(
    IDVenta INT NOT NULL,
    Fecha DATE NOT NULL,
    Cliente NVARCHAR(30) NOT NULL,

    CONSTRAINT PK_Venta
        PRIMARY KEY (IDVenta),

    CONSTRAINT FK_Venta_Cliente
        FOREIGN KEY (Cliente)
        REFERENCES Cliente(Cliente)
);

CREATE TABLE Detalle_Venta(
    IDVenta INT NOT NULL,
    Producto NVARCHAR(30) NOT NULL,
    Cantidad INT NOT NULL,
    Descuento DECIMAL(5,2) NOT NULL,

    CONSTRAINT PK_Detalle_Venta
        PRIMARY KEY (IDVenta, Producto),

    CONSTRAINT FK_Detalle_Venta
        FOREIGN KEY (IDVenta)
        REFERENCES Venta_Producto(IDVenta),

    CONSTRAINT FK_Detalle_Producto
        FOREIGN KEY (Producto)
        REFERENCES Producto(Producto)
);