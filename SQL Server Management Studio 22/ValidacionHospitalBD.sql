USE HospitalW2;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- Se elimina el trigger si ya existe para poder recrearlo sin error.
IF OBJECT_ID(N'dbo.TR_Empleado_ConCitas_Medico', N'TR') IS NOT NULL
    DROP TRIGGER dbo.TR_Empleado_ConCitas_Medico;
GO

CREATE TRIGGER dbo.TR_Empleado_ConCitas_Medico
ON dbo.Empleado
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1
        FROM inserted AS i
        INNER JOIN dbo.Cita AS c
            ON c.id_empleado = i.id_empleado
        WHERE i.rol <> 'Medico'
    )
    BEGIN
        THROW 51002,
            'Operacion rechazada: un empleado con citas asignadas debe conservar el rol Medico.',
            1;
    END;
END;
GO
