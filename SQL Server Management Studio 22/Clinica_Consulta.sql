USE Sistema_Clinica_Medica;
GO

CREATE TABLE Medico (
    NumeroColegiado int not null,
    NombreCompleto nvarchar(30),
    Especialidad nvarchar(15),
    Telefono nvarchar(15) not null,
    CorreoElectronico nvarchar(30),

    CONSTRAINT PK_NumeroColegiado PRIMARY KEY (NumeroColegiado)
);
GO

CREATE TABLE Paciente (
    NumeroExpediente int not null,
    NombreCompleto nvarchar(30),
    FechaNacimiento date not null,
    Direccion nvarchar(50),
    Telefono nvarchar(15) not null,
    CorreoElectronico nvarchar(30),

    CONSTRAINT PK_NumeroExpediente PRIMARY KEY (NumeroExpediente)
);
GO

CREATE TABLE Medicamento (
    Codigo int not null,
    Nombre nvarchar(30),
    PrincipioActivo nvarchar(30),
    Presentaciones nvarchar(20),
    Precio int not null,

    CONSTRAINT PK_Codigo PRIMARY KEY (Codigo)
);
GO

CREATE TABLE Consulta (
    ID int not null,
    Paciente int not null,
    Medico int not null,
    Fecha date not null,
    Hora time not null,
    Diagnostico nvarchar(100),
    Tratamiento nvarchar(100),

    CONSTRAINT PK_IdConsulta PRIMARY KEY (ID),
    CONSTRAINT FK_Consulta_Paciente FOREIGN KEY (Paciente) REFERENCES Paciente(NumeroExpediente),
    CONSTRAINT FK_Consulta_Medico FOREIGN KEY (Medico) REFERENCES Medico(NumeroColegiado)
);
GO

CREATE TABLE Receta (
    ID int not null,
    IDConsulta int not null,
    CodigoMedicamento int not null,
    Dosis nvarchar(15),
    Duracion int not null,
    Indicaciones nvarchar(100),

    CONSTRAINT PK_IdReceta PRIMARY KEY (ID),
    CONSTRAINT FK_Receta_Consulta FOREIGN KEY (IDConsulta) REFERENCES Consulta(ID),
    CONSTRAINT FK_Receta_Medicamento FOREIGN KEY (CodigoMedicamento) REFERENCES Medicamento(Codigo)
);
GO

CREATE TABLE Cita (
    ID int not null,
    Paciente int not null,
    Medico int not null,
    Fecha date not null,
    Hora time not null,
    Estado nvarchar(15) not null,

    CONSTRAINT PK_IdCita PRIMARY KEY (ID),
    CONSTRAINT FK_Cita_Paciente FOREIGN KEY (Paciente) REFERENCES Paciente(NumeroExpediente),
    CONSTRAINT FK_Cita_Medico FOREIGN KEY (Medico) REFERENCES Medico(NumeroColegiado)
);
GO

CREATE INDEX IX_Consulta_Paciente
ON Consulta(Paciente);
GO

CREATE INDEX IX_Consulta_Medico
ON Consulta(Medico);
GO

CREATE INDEX IX_Receta_Consulta
ON Receta(IDConsulta);
GO

CREATE INDEX IX_Receta_Medicamento
ON Receta(CodigoMedicamento);
GO

CREATE INDEX IX_Cita_Paciente
ON Cita(Paciente);
GO

CREATE INDEX IX_Cita_Medico
ON Cita(Medico);
GO

INSERT INTO Medico VALUES
(1001, 'Dr. Carlos Lopez', 'Cardiologia', '555-1001', 'carlos@clinica.com'),
(1002, 'Dra. Ana Gomez', 'Pediatria', '555-1002', 'ana@clinica.com'),
(1003, 'Dr. Luis Martinez', 'Dermatologia', '555-1003', 'luis@clinica.com'),
(1004, 'Dra. Maria Perez', 'Neurologia', '555-1004', 'maria@clinica.com'),
(1005, 'Dr. Jose Ramirez', 'Medicina General', '555-1005', 'jose@clinica.com');
GO

INSERT INTO Paciente VALUES
(2001, 'Juan Perez', '1995-05-10', 'Calle 1', '555-2001', 'juan@gmail.com'),
(2002, 'Ana Lopez', '1998-08-15', 'Calle 2', '555-2002', 'ana@gmail.com'),
(2003, 'Carlos Gomez', '1990-03-20', 'Calle 3', '555-2003', 'carlos@gmail.com'),
(2004, 'Maria Rodriguez', '2001-11-25', 'Calle 4', '555-2004', 'maria@gmail.com'),
(2005, 'Luis Perez', '1988-07-12', 'Calle 5', '555-2005', 'luis@gmail.com');
GO

INSERT INTO Paciente VALUES
(2001, 'Juan Perez', '1995-05-10', 'Calle 1', '555-2001', 'juan@gmail.com'),
(2002, 'Ana Lopez', '1998-08-15', 'Calle 2', '555-2002', 'ana@gmail.com'),
(2003, 'Carlos Gomez', '1990-03-20', 'Calle 3', '555-2003', 'carlos@gmail.com'),
(2004, 'Maria Rodriguez', '2001-11-25', 'Calle 4', '555-2004', 'maria@gmail.com'),
(2005, 'Luis Perez', '1988-07-12', 'Calle 5', '555-2005', 'luis@gmail.com');
GO

INSERT INTO Consulta VALUES
(4001, 2001, 1001, '2024-01-15', '08:00', 'Presion arterial alta', 'Control de presion'),
(4002, 2002, 1002, '2024-01-16', '09:00', 'Gripe comun', 'Reposo y tratamiento'),
(4003, 2003, 1003, '2024-01-17', '10:00', 'Dermatitis', 'Tratamiento topico'),
(4004, 2004, 1004, '2024-01-18', '11:00', 'Dolor de cabeza', 'Tratamiento analgesico'),
(4005, 2005, 1005, '2024-01-19', '12:00', 'Dolor estomacal', 'Tratamiento gastrico');
GO

INSERT INTO Receta VALUES
(5001, 4001, 3001, '500 mg', 5, 'Tomar cada 8 horas'),
(5002, 4002, 3003, '500 mg', 7, 'Tomar cada 12 horas'),
(5003, 4003, 3004, '10 mg', 5, 'Tomar una vez al dia'),
(5004, 4004, 3002, '400 mg', 3, 'Tomar cada 8 horas'),
(5005, 4005, 3005, '20 mg', 7, 'Tomar antes del desayuno');
GO

INSERT INTO Cita VALUES
(6001, 2001, 1001, '2024-02-01', '08:00', 'Programada'),
(6002, 2002, 1002, '2024-02-02', '09:00', 'Programada'),
(6003, 2003, 1003, '2024-02-03', '10:00', 'Realizada'),
(6004, 2004, 1004, '2024-02-04', '11:00', 'Cancelada'),
(6005, 2005, 1005, '2024-02-05', '12:00', 'Programada');
GO

CREATE PROCEDURE sp_AgendarCita
    @ID int,
    @Paciente int,
    @Medico int,
    @Fecha date,
    @Hora time
AS
BEGIN

    INSERT INTO Cita
    VALUES
    (@ID, @Paciente, @Medico, @Fecha, @Hora, 'Programada');

END;
GO

CREATE PROCEDURE sp_PacientesPorMedico
    @NumeroColegiado int
AS
BEGIN

    SELECT
        M.NombreCompleto AS Medico,
        P.NumeroExpediente,
        P.NombreCompleto AS Paciente
    FROM Medico M
    INNER JOIN Consulta C
        ON M.NumeroColegiado = C.Medico
    INNER JOIN Paciente P
        ON C.Paciente = P.NumeroExpediente
    WHERE M.NumeroColegiado = @NumeroColegiado;

END;
GO

CREATE PROCEDURE sp_HistorialPaciente
    @NumeroExpediente int
AS
BEGIN

    SELECT
        P.NombreCompleto AS Paciente,
        C.ID AS IdConsulta,
        C.Fecha,
        C.Hora,
        M.NombreCompleto AS Medico,
        C.Diagnostico,
        C.Tratamiento,
        MD.Nombre AS Medicamento,
        R.Dosis,
        R.Duracion,
        R.Indicaciones
    FROM Paciente P
    INNER JOIN Consulta C
        ON P.NumeroExpediente = C.Paciente
    INNER JOIN Medico M
        ON C.Medico = M.NumeroColegiado
    LEFT JOIN Receta R
        ON C.ID = R.IDConsulta
    LEFT JOIN Medicamento MD
        ON R.CodigoMedicamento = MD.Codigo
    WHERE P.NumeroExpediente = @NumeroExpediente;

END;
GO