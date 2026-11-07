# Correcciones de esta cierre

- Compilación de `CatalogoAdminService`: el SKU duplicado se compara con el id actual, y el producto guardado es otra variable.
- Esquema `Rol`, `Usuario`, `UsuarioRol`, `AuditoriaOperacion` y columnas `id_usuario` en reserva, pedido y conversación, aplicado con `sql/002_usuarios_seguridad.sql`.
- Cookie CSRF disponible para el asistente y página de acceso denegado capaz de recibir el reenvío POST.
- Intenciones nuevas del asistente: saludo, información de la tienda, comparación por dos SKU y orientación de compra sin crear el pedido.
- Límite de intentos de login.
- Contraseña de SQL retirada de `README.md` y de los scripts versionados.
