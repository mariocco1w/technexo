package com.technexo.seguridad;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SeguridadConfig {

    @Bean
    SecurityFilterChain filtro(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler destino = new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                 org.springframework.security.core.Authentication authentication)
                    throws IOException, ServletException {
                setDefaultTargetUrl(authentication.getAuthorities().stream()
                        .anyMatch(rol -> "ROLE_ADMIN".equals(rol.getAuthority())) ? "/admin" : "/cuenta");
                super.onAuthenticationSuccess(request, response, authentication);
            }
        };
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/error", "/acceso-denegado", "/login", "/registro").permitAll()
                        .requestMatchers("/", "/catalogo", "/buscar", "/producto/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos", "/api/productos/**", "/api/categorias").permitAll()
                        .requestMatchers("/api/chat", "/api/chat/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/inventario", "/inventario/**", "/api/inventario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/reservas/*/confirmar", "/api/reservas/*/confirmar").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login").successHandler(destino).permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
                .exceptionHandling(ex -> ex.accessDeniedHandler((request, response, denied) -> {
                    if (request.getRequestURI() != null && request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write("{\"error\":\"Acceso denegado\"}");
                        return;
                    }
                    request.getRequestDispatcher("/acceso-denegado").forward(request, response);
                }))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfSpaHandler()))
                .sessionManagement(sesion -> sesion.sessionFixation(fijacion -> fijacion.migrateSession()))
                .headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'self'; img-src 'self' data:; style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; connect-src 'self'")));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
