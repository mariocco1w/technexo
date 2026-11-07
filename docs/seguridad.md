# Seguridad

## Hecho en la aplicación

- Contraseñas con BCrypt.
- Rutas `/admin/**` e inventario exigen `ROLE_ADMIN` en el servidor.
- Reservas y pedidos del cliente se filtran por el id de la sesión.
- Un administrador no puede cambiar su propio rol ni desactivar su propia cuenta. Debe quedar al menos un administrador activo.
- CSRF activo. Los formularios usan el token de Thymeleaf y el chat envía el encabezado.
- Cookie de sesión `HttpOnly` y `SameSite=Lax`.
- La página de error no incluye la traza.
- Cabecera `Content-Security-Policy` restringe el origen de scripts y estilos.
- Consultas por JPA y parámetros. El asistente no concatena SQL.
- Como máximo 8 envíos de login por minuto y 30 mensajes de chat por minuto, por dirección.

## Credenciales

`application.yml` ya no contiene la contraseña. Se lee de `TECHNEXO_DB_PASSWORD` o de `technexo-backend/config/application-local.yml`, archivo ignorado por Git. Los scripts de SQL Server exigen la misma variable.

Esa contraseña sí estuvo en el repositorio y en el historial. Hay que cambiarla en SQL Server y actualizar solo el archivo local. No vuelva a pegarla en `README.md`.

## No cubierto

- Recuperación de contraseña por correo: no hay servicio de correo.
- La contraseña antigua sigue siendo válida hasta que se rote en el servidor.
- No se ejecutó un escaneo de dependencias de terceros en esta revisión.
