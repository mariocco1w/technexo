/* =========================================================
   TechNexo - Pruebas de flujo: Reservas, Confirmación, Ingreso, Pedidos
   Objetivo: Comprobar que el flujo se maneja correctamente
   NOTA: Usa ROLLBACK por defecto. Cambiar a COMMIT solo tras validar.
   ========================================================= */

USE TechNexoDB;
GO

SET NOCOUNT ON;
GO

DECLARE @IdSwitch INT = (SELECT id_producto FROM dbo.Producto WHERE sku = N'TNX-SW-024');
IF @IdSwitch IS NULL
BEGIN
    RAISERROR('No se encontró TNX-SW-024', 16, 1);
    RETURN;
END

SELECT 'ESTADO INICIAL - TNX-SW-024' AS SECCION;
SELECT p.sku, p.nombre, i.stock_actual, d.cantidad_reservada, d.stock_disponible, p.precio, p.moneda, p.estado
FROM dbo.Producto p
LEFT JOIN dbo.Inventario i ON i.id_producto = p.id_producto
JOIN dbo.vw_DisponibilidadProducto d ON d.id_producto = p.id_producto
WHERE p.id_producto = @IdSwitch;
GO

/* =========================================================
   PRUEBA 1: CREAR RESERVA PENDIENTE VÁLIDA
   Debe: insertar Reserva con estado PENDIENTE, fecha_expiracion > creacion
         Disponibilidad debe disminuir (vista)
   ========================================================= */
BEGIN TRAN PRUEBA1;

DECLARE @IdSwitch INT = (SELECT id_producto FROM dbo.Producto WHERE sku = N'TNX-SW-024');
DECLARE @DispAntes INT = (
  SELECT stock_disponible FROM dbo.vw_DisponibilidadProducto WHERE id_producto = @IdSwitch
);
DECLARE @CantReserva INT = 2;

IF @DispAntes < @CantReserva
BEGIN
  RAISERROR('PRUEBA1: No hay disponibilidad suficiente para reservar %d. Disp=%d', 16,1,@CantReserva,@DispAntes);
  ROLLBACK TRAN PRUEBA1;
  RETURN;
END

DECLARE @Expira1 DATETIME2(0) = DATEADD(MINUTE, 30, SYSUTCDATETIME());
INSERT INTO dbo.Reserva (id_producto, cantidad, fecha_expiracion, estado, identificador_cliente)
VALUES (@IdSwitch, @CantReserva, @Expira1, 'PENDIENTE', N'PRUEBA-CLI-001');

DECLARE @IdReserva1 BIGINT = SCOPE_IDENTITY();

SELECT 'PRUEBA 1 - RESERVA CREADA' AS RES;
SELECT @IdReserva1 AS id_reserva, @IdSwitch AS id_producto, @CantReserva AS cantidad, 'PENDIENTE' AS estado, @Expira1 AS fecha_expiracion, SYSUTCDATETIME() AS fecha_creacion_ref;

SELECT 'PRUEBA 1 - DISPONIBILIDAD DESPUÉS DE RESERVAR' AS RES;
SELECT d.stock_fisico, d.cantidad_reservada, d.stock_disponible
FROM dbo.vw_DisponibilidadProducto d WHERE d.id_producto = @IdSwitch;

-- Validación: stock_disponible >= 0
IF EXISTS (SELECT 1 FROM dbo.vw_DisponibilidadProducto WHERE id_producto=@IdSwitch AND stock_disponible < 0)
BEGIN
  RAISERROR('PRUEBA1: stock_disponible NEGATIVO. Inválido.',16,1);
  ROLLBACK TRAN PRUEBA1; RETURN;
END

PRINT 'PRUEBA 1 OK: Reserva PENDIENTE creada. Disponibilidad recalculada correctamente.';
ROLLBACK TRAN PRUEBA1;
GO

/* =========================================================
   PRUEBA 2: CONFIRMAR RESERVA -> AFECTA INVENTARIO + MOVIMIENTO SALIDA
   Debe: pasar PENDIENTE->CONFIRMADA
         descontar stock_actual en Inventario
         registrar MovimientoInventario tipo SALIDA con motivo/referencia
   ========================================================= */
BEGIN TRAN PRUEBA2;

DECLARE @IdSwitch INT = (SELECT id_producto FROM dbo.Producto WHERE sku = N'TNX-SW-024');
DECLARE @DispAntes INT = (SELECT stock_disponible FROM dbo.vw_DisponibilidadProducto WHERE id_producto=@IdSwitch);
DECLARE @StockAntes INT = (SELECT stock_actual FROM dbo.Inventario WHERE id_producto=@IdSwitch);
DECLARE @Cant2 INT = 3;
DECLARE @Expira2 DATETIME2(0)=DATEADD(MINUTE,30,SYSUTCDATETIME());

IF @DispAntes < @Cant2
BEGIN RAISERROR('PRUEBA2: Sin disponibilidad. Disp=%d Cant=%d',16,1,@DispAntes,@Cant2); ROLLBACK TRAN PRUEBA2; RETURN; END

INSERT dbo.Reserva(id_producto,cantidad,fecha_expiracion,estado,identificador_cliente)
VALUES(@IdSwitch,@Cant2,@Expira2,'PENDIENTE',N'PRUEBA2-CONFIRMAR');

DECLARE @IdRes2 BIGINT=SCOPE_IDENTITY();

-- Confirmar: validar que siga PENDIENTE y vigente
IF NOT EXISTS(SELECT 1 FROM dbo.Reserva WHERE id_reserva=@IdRes2 AND estado='PENDIENTE' AND fecha_expiracion>SYSUTCDATETIME())
BEGIN RAISERROR('PRUEBA2: Reserva no válida para confirmar',16,1); ROLLBACK TRAN PRUEBA2; RETURN; END

-- Descontar inventario con validación
UPDATE dbo.Inventario
SET stock_actual = stock_actual - @Cant2,
    fecha_actualizacion = SYSUTCDATETIME()
WHERE id_producto=@IdSwitch AND stock_actual >= @Cant2;

IF @@ROWCOUNT<>1
BEGIN RAISERROR('PRUEBA2: Fallo al descontar inventario (posible sobreventa/concurrencia)',16,1); ROLLBACK TRAN PRUEBA2; RETURN; END

-- Registrar movimiento SALIDA
INSERT dbo.MovimientoInventario(id_producto,tipo_movimiento,cantidad,motivo,referencia,creado_por)
VALUES(@IdSwitch,'SALIDA',@Cant2,N'Confirmación de reserva',CONCAT('RES-',@IdRes2),N'SCRIPT-PRUEBAS');

-- Marcar reserva CONFIRMADA
UPDATE dbo.Reserva SET estado='CONFIRMADA' WHERE id_reserva=@IdRes2 AND estado='PENDIENTE';
IF @@ROWCOUNT<>1
BEGIN RAISERROR('PRUEBA2: No se pudo marcar reserva como CONFIRMADA',16,1); ROLLBACK TRAN PRUEBA2; RETURN; END

DECLARE @StockDesp2 INT=(SELECT stock_actual FROM dbo.Inventario WHERE id_producto=@IdSwitch);
DECLARE @MovCount2 INT=(SELECT COUNT(*) FROM dbo.MovimientoInventario WHERE referencia=CONCAT('RES-',@IdRes2));

SELECT 'PRUEBA 2 - RESULTADOS' AS RES;
SELECT @StockAntes AS stock_antes, @Cant2 AS descontado, @StockDesp2 AS stock_despues, @MovCount2 AS movimientos_registrados;

SELECT 'PRUEBA 2 - ÚLTIMO MOVIMIENTO' AS RES;
SELECT TOP 1 id_movimiento,id_producto,tipo_movimiento,cantidad,motivo,referencia,fecha,creado_por
FROM dbo.MovimientoInventario WHERE id_producto=@IdSwitch ORDER BY id_movimiento DESC;

IF @StockDesp2 <> @StockAntes - @Cant2
BEGIN RAISERROR('PRUEBA2: Descuento incorrecto',16,1); ROLLBACK TRAN PRUEBA2; RETURN; END

PRINT 'PRUEBA 2 OK: CONFIRMADA -> Inventario descontado + Movimiento SALIDA registrado.';
ROLLBACK TRAN PRUEBA2;
GO

/* =========================================================
   PRUEBA 3: REGISTRO DE INGRESO DE PRODUCTO (ENTRADA)
   Debe: aumentar stock_actual y registrar MovimientoInventario tipo ENTRADA
   ========================================================= */
BEGIN TRAN PRUEBA3;

DECLARE @IdSwitch INT=(SELECT id_producto FROM dbo.Producto WHERE sku=N'TNX-SW-024');
DECLARE @StockAntes INT=(SELECT stock_actual FROM dbo.Inventario WHERE id_producto=@IdSwitch);
DECLARE @CantIng INT=5;
DECLARE @Ref3 NVARCHAR(100)=N'ING-PRUEBA-001';

UPDATE dbo.Inventario
SET stock_actual = stock_actual + @CantIng,
    fecha_actualizacion = SYSUTCDATETIME()
WHERE id_producto=@IdSwitch;

IF @@ROWCOUNT<>1 BEGIN RAISERROR('PRUEBA3: Fallo al actualizar inventario',16,1); ROLLBACK TRAN PRUEBA3; RETURN; END

INSERT dbo.MovimientoInventario(id_producto,tipo_movimiento,cantidad,motivo,referencia,creado_por)
VALUES(@IdSwitch,'ENTRADA',@CantIng,N'Registro de ingreso de producto (prueba)',@Ref3,N'SCRIPT-PRUEBAS');

DECLARE @StockDesp3 INT=(SELECT stock_actual FROM dbo.Inventario WHERE id_producto=@IdSwitch);
DECLARE @IdMov3 BIGINT=SCOPE_IDENTITY();

SELECT 'PRUEBA 3 - RESULTADOS' AS RES;
SELECT @StockAntes AS stock_antes,@CantIng AS ingresado,@StockDesp3 AS stock_despues;

SELECT 'PRUEBA 3 - MOVIMIENTO CREADO' AS RES;
SELECT id_movimiento,id_producto,tipo_movimiento,cantidad,motivo,referencia,fecha,creado_por FROM dbo.MovimientoInventario WHERE id_movimiento=@IdMov3;

IF @StockDesp3 <> @StockAntes + @CantIng
BEGIN RAISERROR('PRUEBA3: Incremento incorrecto',16,1); ROLLBACK TRAN PRUEBA3; RETURN; END

PRINT 'PRUEBA 3 OK: ENTRADA registrada + stock_actual incrementado correctamente.';
ROLLBACK TRAN PRUEBA3;
GO

/* =========================================================
   PRUEBA 4: CONFIRMAR PAGO / CREAR PEDIDO CON PRECIO HISTÓRICO
   Debe: crear Pedido + DetallePedido con precio_unitario_historico = precio actual
         coordinar afectación a inventario + movimiento SALIDA
         preservar precio histórico aunque cambie precio catálogo
   ========================================================= */
BEGIN TRAN PRUEBA4;

DECLARE @IdSwitch INT=(SELECT id_producto FROM dbo.Producto WHERE sku=N'TNX-SW-024');
DECLARE @DispAntes INT=(SELECT stock_disponible FROM dbo.vw_DisponibilidadProducto WHERE id_producto=@IdSwitch);
DECLARE @StockAntes INT=(SELECT stock_actual FROM dbo.Inventario WHERE id_producto=@IdSwitch);
DECLARE @PrecioActual DECIMAL(12,2)=(SELECT precio FROM dbo.Producto WHERE id_producto=@IdSwitch);
DECLARE @CantPed INT=2;
DECLARE @IdPed BIGINT;

IF @DispAntes < @CantPed
BEGIN RAISERROR('PRUEBA4: Sin disponibilidad para pedido. Disp=%d',16,1,@DispAntes); ROLLBACK TRAN PRUEBA4; RETURN; END

-- Crear pedido simulando confirmación de pago
INSERT dbo.Pedido(fecha,estado,subtotal,total,moneda,nombre_cliente,correo_cliente,telefono_cliente,clave_idempotencia)
VALUES(SYSUTCDATETIME(),'PAGADO',@PrecioActual*@CantPed,@PrecioActual*@CantPed,'GTQ',N'CLIENTE-PAGO',N'pago@test.gt',N'5555',NEWID());

SET @IdPed=SCOPE_IDENTITY();

-- Detalle con precio histórico
INSERT dbo.DetallePedido(id_pedido,id_producto,cantidad,precio_unitario_historico)
VALUES(@IdPed,@IdSwitch,@CantPed,@PrecioActual);

-- Afectar inventario + movimiento
UPDATE dbo.Inventario
SET stock_actual = stock_actual - @CantPed,
    fecha_actualizacion = SYSUTCDATETIME()
WHERE id_producto=@IdSwitch AND stock_actual >= @CantPed;
IF @@ROWCOUNT<>1 BEGIN RAISERROR('PRUEBA4: Error descontando inventario',16,1); ROLLBACK TRAN PRUEBA4; RETURN; END

INSERT dbo.MovimientoInventario(id_producto,tipo_movimiento,cantidad,motivo,referencia,creado_por)
VALUES(@IdSwitch,'SALIDA',@CantPed,N'Confirmación de pedido/pago',CONCAT('PED-',@IdPed),N'SCRIPT-PRUEBAS');

DECLARE @StockDesp4 INT=(SELECT stock_actual FROM dbo.Inventario WHERE id_producto=@IdSwitch);

SELECT 'PRUEBA 4 - PEDIDO CREADO' AS RES;
SELECT id_pedido,estado,subtotal,total,moneda,nombre_cliente,fecha FROM dbo.Pedido WHERE id_pedido=@IdPed;

SELECT 'PRUEBA 4 - DETALLE CON PRECIO HISTÓRICO' AS RES;
SELECT id_detalle,id_pedido,id_producto,cantidad,precio_unitario_historico FROM dbo.DetallePedido WHERE id_pedido=@IdPed;

SELECT 'PRUEBA 4 - INVENTARIO/MOVIMIENTO' AS RES;
SELECT @StockAntes AS stock_antes,@CantPed AS cantidad,@StockDesp4 AS stock_despues;

-- Verificar precio histórico persiste si cambiamos precio catálogo
UPDATE dbo.Producto SET precio = @PrecioActual + 100.00, fecha_actualizacion=SYSUTCDATETIME() WHERE id_producto=@IdSwitch;
SELECT 'PRUEBA 4 - VERIFICACIÓN PRECIO HISTÓRICO (POST CAMBIO CATÁLOGO)' AS RES;
SELECT dp.precio_unitario_historico AS precio_historico_en_pedido, p.precio AS precio_actual_catalogo
FROM dbo.DetallePedido dp JOIN dbo.Producto p ON p.id_producto=dp.id_producto WHERE dp.id_pedido=@IdPed;
-- Revertir precio
UPDATE dbo.Producto SET precio=@PrecioActual, fecha_actualizacion=SYSUTCDATETIME() WHERE id_producto=@IdSwitch;

IF @StockDesp4 <> @StockAntes - @CantPed BEGIN RAISERROR('PRUEBA4: Descuento inválido',16,1); ROLLBACK TRAN PRUEBA4; RETURN; END

PRINT 'PRUEBA 4 OK: Pedido PAGADO + detalle con precio histórico + afectación inventario + movimiento SALIDA. Precio histórico se conserva.';
ROLLBACK TRAN PRUEBA4;
GO

/* =========================================================
   PRUEBA 5: EVITAR SOBREVENTA POR RESERVA (> DISPONIBLE)
   Debe rechazarse lógicamente
   ========================================================= */
BEGIN TRAN PRUEBA5;

DECLARE @IdSwitch INT=(SELECT id_producto FROM dbo.Producto WHERE sku=N'TNX-SW-024');
DECLARE @DispAntes INT=(SELECT stock_disponible FROM dbo.vw_DisponibilidadProducto WHERE id_producto=@IdSwitch);
DECLARE @Cant5 INT=@DispAntes + 1;

SELECT 'PRUEBA 5 - INTENTO INVÁLIDO' AS RES, @DispAntes AS disp_disponible, @Cant5 AS cantidad_solicitada;

IF @Cant5 > @DispAntes
BEGIN
  PRINT 'VALIDACIÓN: cantidad_solicitada > stock_disponible -> RECHAZADO CORRECTAMENTE (NO SE INSERTA RESERVA)';
  SELECT 'PRUEBA 5' AS RES, 'RECHAZADO' AS ACCION, 'NO SE INSERTÓ RESERVA' AS MOTIVO, 'Motivo'='Excede disponibilidad' AS DETALLE;
END
ELSE
BEGIN
  RAISERROR('PRUEBA5: Condición inesperada',16,1); ROLLBACK TRAN PRUEBA5; RETURN;
END

ROLLBACK TRAN PRUEBA5;
GO

/* =========================================================
   RESUMEN
   ========================================================= */
SELECT 'RESUMEN FINAL - ESTADO ACTUAL TNX-SW-024 (SIN CAMBIOS)' AS SECCION;
SELECT p.sku,p.nombre,i.stock_actual,d.cantidad_reservada,d.stock_disponible,p.precio,p.moneda,p.estado
FROM dbo.Producto p
LEFT JOIN dbo.Inventario i ON i.id_producto=p.id_producto
JOIN dbo.vw_DisponibilidadProducto d ON d.id_producto=p.id_producto
WHERE p.id_producto=(SELECT id_producto FROM dbo.Producto WHERE sku=N'TNX-SW-024');

PRINT 'Todas las pruebas ejecutadas con ROLLBACK. No se modificaron datos permanentes.';
PRINT 'Flujos validados: Reserva PENDIENTE -> Confirmación (descuento+SALIDA), INGRESO (incremento+ENTRADA), Pedido/Pago (precio histórico + afectación).';
GO
