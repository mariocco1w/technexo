/*
  Ampliación no destructiva de TechNexoDB.
  Agrega cuentas, roles y auditoría. No borra tablas ni datos existentes.
  El arranque de la aplicación ejecuta el mismo script de forma idempotente.
*/
IF OBJECT_ID('dbo.Rol', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Rol (
        id_rol INT IDENTITY(1,1) NOT NULL,
        nombre VARCHAR(30) NOT NULL,
        CONSTRAINT PK_Rol PRIMARY KEY (id_rol),
        CONSTRAINT UQ_Rol_nombre UNIQUE (nombre),
        CONSTRAINT CK_Rol_nombre CHECK (nombre IN ('ADMIN','CLIENTE'))
    );
END
GO

IF OBJECT_ID('dbo.Usuario', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Usuario (
        id_usuario INT IDENTITY(1,1) NOT NULL,
        correo NVARCHAR(254) NOT NULL,
        nombre NVARCHAR(150) NOT NULL,
        password_hash NVARCHAR(100) NOT NULL,
        activo BIT NOT NULL CONSTRAINT DF_Usuario_activo DEFAULT (1),
        fecha_creacion DATETIME2(0) NOT NULL CONSTRAINT DF_Usuario_fecha DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_Usuario PRIMARY KEY (id_usuario),
        CONSTRAINT UQ_Usuario_correo UNIQUE (correo)
    );
END
GO

IF OBJECT_ID('dbo.UsuarioRol', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.UsuarioRol (
        id_usuario INT NOT NULL,
        id_rol INT NOT NULL,
        CONSTRAINT PK_UsuarioRol PRIMARY KEY (id_usuario, id_rol),
        CONSTRAINT FK_UsuarioRol_Usuario FOREIGN KEY (id_usuario) REFERENCES dbo.Usuario(id_usuario),
        CONSTRAINT FK_UsuarioRol_Rol FOREIGN KEY (id_rol) REFERENCES dbo.Rol(id_rol)
    );
END
GO

IF OBJECT_ID('dbo.AuditoriaOperacion', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.AuditoriaOperacion (
        id_auditoria BIGINT IDENTITY(1,1) NOT NULL,
        id_usuario INT NULL,
        accion VARCHAR(80) NOT NULL,
        entidad VARCHAR(40) NOT NULL,
        id_entidad NVARCHAR(40) NULL,
        detalle NVARCHAR(500) NULL,
        fecha DATETIME2(0) NOT NULL CONSTRAINT DF_Auditoria_fecha DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_AuditoriaOperacion PRIMARY KEY (id_auditoria)
    );
END
GO

IF COL_LENGTH('dbo.Reserva', 'id_usuario') IS NULL
    ALTER TABLE dbo.Reserva ADD id_usuario INT NULL;
GO
IF COL_LENGTH('dbo.Pedido', 'id_usuario') IS NULL
    ALTER TABLE dbo.Pedido ADD id_usuario INT NULL;
GO
IF COL_LENGTH('dbo.Conversacion', 'id_usuario') IS NULL
    ALTER TABLE dbo.Conversacion ADD id_usuario INT NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Reserva_Usuario')
    ALTER TABLE dbo.Reserva ADD CONSTRAINT FK_Reserva_Usuario FOREIGN KEY (id_usuario) REFERENCES dbo.Usuario(id_usuario);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Pedido_Usuario')
    ALTER TABLE dbo.Pedido ADD CONSTRAINT FK_Pedido_Usuario FOREIGN KEY (id_usuario) REFERENCES dbo.Usuario(id_usuario);
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Conversacion_Usuario')
    ALTER TABLE dbo.Conversacion ADD CONSTRAINT FK_Conversacion_Usuario FOREIGN KEY (id_usuario) REFERENCES dbo.Usuario(id_usuario);
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Rol WHERE nombre = 'ADMIN')
    INSERT dbo.Rol(nombre) VALUES ('ADMIN');
IF NOT EXISTS (SELECT 1 FROM dbo.Rol WHERE nombre = 'CLIENTE')
    INSERT dbo.Rol(nombre) VALUES ('CLIENTE');
GO
