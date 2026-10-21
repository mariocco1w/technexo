CREATE DATABASE ejemplo0

USE ejemplo0

CREATE TABLE nacionalidad(
	codigonac int,
	descripcion nvarchar(50) not null,
	CONSTRAINT PK_nacionalidad PRIMARY KEY (codigonac),
);

CREATE TABLE curso(
	codigocurso int,
	nombre nvarchar(30) not null,
	cas numeric(2,0)not null,
	CONSTRAINT PK_curso PRIMARY KEY (codigocurso)
)

CREATE TABLE  Estudiante (
	carnet int not null,
	nombre nvarchar(20)not null,
	apellido nvarchar(30)not null,
	direccion nvarchar(50),
	telefono nvarchar(20)not null,
	codigonac int, /*<--Fk*/
	fechanacimiento date not null,
	sexo nvarchar(1),
	
	CONSTRAINT PK_estudiante PRIMARY KEY(carnet),
	CONSTRAINT FK_estudiante_nacionalidad FOREIGN KEY(codigonac) REFERENCES nacionalidad(codigonac));

CREATE TABLE Asignacion_Curso(
	carnet int not null,--<--FK
	codigocurso int not null,--<--FK
	fechaasignacion date not null,
	nota numeric (3,0),
	CONSTRAINT PK_asignacion_curso PRIMARY KEY (carnet,codigocurso,fechaasignacion),
	CONSTRAINT FK_asignacion_curso FOREIGN KEY (carnet) REFERENCES estudiante(carnet),
	CONSTRAINT FK_asignacion_curso_curso FOREIGN KEY (codigocurso) REFERENCES curso(codigocurso)

);