package com.technexo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejo de errores para los endpoints REST (/api/**). Devuelve JSON en lugar de vistas.
 */
@RestControllerAdvice(basePackages = {
        "com.technexo.catalogo.controller",
        "com.technexo.reservas.controller",
        "com.technexo.pedidos.controller",
        "com.technexo.inventario.controller",
        "com.technexo.chat.controller"
})
public class RestExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> reglaNegocio(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Datos inválidos");
        return ResponseEntity.badRequest().body(cuerpo(detalle));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> general(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cuerpo(ex.getMessage()));
    }

    private Map<String, Object> cuerpo(String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", mensaje);
        return body;
    }
}
