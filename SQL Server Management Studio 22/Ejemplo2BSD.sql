 USE Ejemplo2BSD

CREATE TABLE  Nacionalidades(
CodigoNac int not null,
Nacionalidad nvarchar not null,
CONSTRAINT PK_Nacionalidades PRIMARY KEY (Codigonac)
);

CREATE TABLE Editoriales(
CodigoEditorial int not null,
Editorial nvarchar not null,
CONSTRAINT PK_Editoriales PRIMARY KEY(CodigoEditorial)
);

CREATE TABLE Autor(
CodigoAutor int not null,
NombreAutor nvarchar not null,
CodigoNac int not null,
CONSTRAINT PK_Autor PRIMARY KEY(CodigoAutor),
CONSTRAINT FK_Autor_Nacionalidades FOREIGN KEY (CodigoNac) REFERENCES Nacionalidades (CodigoNac)
);
CREATE TABLE Estudiante(
Carnet int  not null,
Apellidos nvarchar(30) not null,
Nombre nvarchar(30) not null,
Direccion nvarchar(30) not null,
Telefono nvarchar(15) not null,
CONSTRAINT PK_Estudiante PRIMARY KEY (Carnet)
);
CREATE TABLE Libro(
CodigoLibro int not null,
TituloLibro nvarchar(30) not null,
CodigoEditorial int not null,
AnoEdicion int not null,
CodigoAutor int not null,

CONSTRAINT PK_Libro PRIMARY KEY (CodigoLibro),
CONSTRAINT FK_Libro_Editoriales FOREIGN KEY (CodigoEditorial) REFERENCES Editoriales (CodigoEditorial),
CONSTRAINT FK_Libro_Autor FOREIGN KEY (CodigoAutor) REFERENCES Autor(CodigoAutor)
);

CREATE TABLE Prestamo(
CodigoLibro int not null,
Carnet int not null,
Prestamo datetime not null,
Devolucion datetime not null,

CONSTRAINT PK_Prestamo PRIMARY KEY (CodigoLibro, Carnet, Prestamo),
CONSTRAINT FK_Prestamo_Libro FOREIGN KEY (CodigoLibro) REFERENCES Libro(CodigoLibro),
CONSTRAINT FK_Pestamo_Estudiante FOREIGN KEY(Carnet) REFERENCES Estudiante (Carnet)
);