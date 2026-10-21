USE Sistema_Matriculas_Cursos;

/*==========================================================
TABLA: ESTUDIANTE

Descripción:
Almacena la información de los estudiantes.

Clave Primaria:
PK -> IdEstudiante

Relación:
Estudiante (1) -------- (M) Matrículas
==========================================================*/
CREATE TABLE Estudiante(
IdEstudiante int not null,
Estudiantes nvarchar(20),
Semestre int not null

CONSTRAINT PK_IdEstudiante PRIMARY KEY (IdEstudiante)
);
/*==========================================================
TABLA: PROFESORES

Descripción:
Información de los profesores.

PK -> IdProfesores

Relación:
Profesores (1) -------- (M) Matrículas
==========================================================*/
CREATE TABLE Profesores(
IdProfesores int not null,
Profesor nvarchar(20),
Departamento nvarchar(20)

CONSTRAINT PK_IdProfesores PRIMARY KEY (IdProfesores)
);
/*==========================================================
TABLA: CURSOS

Descripción:
Información de los cursos.

PK -> IdCurso

Relación:
Cursos (1) -------- (M) Matrículas
==========================================================*/
CREATE TABLE Cursos(
IdCurso int not null,
Curso nvarchar(20),
Creditos int not null,


CONSTRAINT PK_IdCurso PRIMARY KEY (IdCurso)
);
/*==========================================================
TABLA: MATRICULAS

Descripción:
Registro de las matrículas.

PK -> IdMatricula

FK -> IdEstudiante
FK -> IdCurso
FK -> IdProfesor

Relaciones

Matrículas (M) ---- (1) Estudiante

Matrículas (M) ---- (1) Curso

Matrículas (M) ---- (1) Profesor
==========================================================*/
CREATE TABLE Matriculas(
IdMatricula int not null,
IdEstudiante int not null,
IdCurso int not null,
IdProfesor int not null,
Estudiante nvarchar(20),
FechaMatriculacion date not null,
Carrera nvarchar(11)

CONSTRAINT PK_IdMatricula PRIMARY KEY (IdMatricula)
CONSTRAINT FK_IdEstudiante FOREIGN KEY (IdEstudiante) REFERENCES Estudiante(IdEstudiante),
CONSTRAINT FK_IdCurso FOREIGN KEY (IdCurso) REFERENCES Cursos(IdCurso),
CONSTRAINT FK_IdPrfesor FOREIGN KEY (IdProfesor) REFERENCES Profesores(IdProfesores)
);