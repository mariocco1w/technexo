package com.technexo.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LimiteAccesoFilter extends OncePerRequestFilter {

    private static final int MAXIMO = 8;
    private final ConcurrentHashMap<String, Deque<Long>> ventanas = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!"/login".equals(request.getRequestURI()) || !"POST".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        String origen = request.getRemoteAddr() == null ? "local" : request.getRemoteAddr();
        long ahora = System.currentTimeMillis();
        Deque<Long> marcas = ventanas.computeIfAbsent(origen, clave -> new ArrayDeque<>());
        synchronized (marcas) {
            while (!marcas.isEmpty() && ahora - marcas.peekFirst() > 60_000) {
                marcas.removeFirst();
            }
            if (marcas.size() >= MAXIMO) {
                response.sendRedirect("/login?error");
                return;
            }
            marcas.addLast(ahora);
        }
        chain.doFilter(request, response);
    }
}
