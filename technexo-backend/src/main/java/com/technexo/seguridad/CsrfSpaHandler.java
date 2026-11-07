package com.technexo.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import java.util.function.Supplier;

/**
 * Acepta el token de los formularios y el encabezado que envía el asistente.
 * Al resolver la petición deja la cookie lista para el navegador.
 */
public final class CsrfSpaHandler implements CsrfTokenRequestHandler {

    private final CsrfTokenRequestHandler formulario = new XorCsrfTokenRequestAttributeHandler();
    private final CsrfTokenRequestHandler encabezado = new CsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        formulario.handle(request, response, csrfToken);
        csrfToken.get();
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        if (request.getHeader(csrfToken.getHeaderName()) != null) {
            return encabezado.resolveCsrfTokenValue(request, csrfToken);
        }
        return formulario.resolveCsrfTokenValue(request, csrfToken);
    }
}
