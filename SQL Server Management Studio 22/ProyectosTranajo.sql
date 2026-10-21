USE Proyecto_TrabajoSemanal3;

CREATE TABLE Proyectos(
	IDProyecto int not null,
	Nombre nvarchar (20),
	Gerente  nvarchar (20),
	FechaInicio date not null,
CONSTRAINT PK_IDProyecto PRIMARY KEY (IDProyecto) 
);

CREATE TABLE Empleado( 
	IDEmpleado int not null,
	NombreEmpleado nvarchar (30),
	Cargo nvarchar (30),
CONSTRAINT PK_IDEmpleado PRIMARY KEY (IDEmpleado)
);

CREATE TABLE Asignacion(
IDProyecto int not null,
IDEmpleado int not null,
HorasAsignadas date not null,
FechaAsignacion date not null,

CONSTRAINT FK_IDProyecto FOREIGN KEY (IDProyecto) REFERENCES Proyectos(IDProyecto),
CONSTRAINT FK_IDEmpleado FOREIGN KEY (IDEmpleado) REFERENCES Empleado(IDEmpleado),

);


INSERT INTO Asignacion VALUES
('P001','E001',40,'2026-01-02'),
('P001','E002',20,'2026-01-02'),
('P001','E003',35,'2026-01-03'),
('P002','E001',30,'2026-01-16'),
('P002','E004',25,'2026-01-16'),
('P003','E002',45,'2026-02-02');

SELECT *
FROM Proyectos;

INSERT INTO Proyectos VALUES
('P001','Sistema Web','Carlos Ruiz','2026-01-01'),
('P002','App Móvil','María Gómez','2026-01-15'),
('P003','BD Empresarial','Carlos Ruiz','2026-02-01');


