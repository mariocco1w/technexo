package com.technexo.seguridad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;

@Component
@Order(1)
public class ArranqueSeguridad implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ArranqueSeguridad.class);
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    private final EsquemaSeguridad esquemaSeguridad;
    private final UsuarioService usuarioService;
    private final Environment environment;

    public ArranqueSeguridad(EsquemaSeguridad esquemaSeguridad, UsuarioService usuarioService, Environment environment) {
        this.esquemaSeguridad = esquemaSeguridad;
        this.usuarioService = usuarioService;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        esquemaSeguridad.aplicar();
        String adminPassword = resolverPassword("TECHNEXO_ADMIN_PASSWORD");
        String clientePassword = resolverPassword("TECHNEXO_CLIENTE_PASSWORD");
        String adminCorreo = environment.getProperty("TECHNEXO_ADMIN_EMAIL", "admin@technexo.local");
        String clienteCorreo = environment.getProperty("TECHNEXO_CLIENTE_EMAIL", "cliente@technexo.local");
        boolean adminNuevo = usuarioService.asegurarCuenta("Administrador TechNexo", adminCorreo, adminPassword, "ADMIN");
        boolean clienteNuevo = usuarioService.asegurarCuenta("Cliente de demostración", clienteCorreo, clientePassword, "CLIENTE");
        if (adminNuevo || clienteNuevo) {
            Path destino = Path.of(System.getProperty("user.dir")).resolve("credenciales-locales.txt");
            StringBuilder texto = new StringBuilder();
            texto.append("Cuentas creadas en este arranque. Este archivo es local y no debe versionarse.\n");
            if (adminNuevo) {
                texto.append("ADMIN ").append(adminCorreo).append(' ').append(adminPassword).append('\n');
            }
            if (clienteNuevo) {
                texto.append("CLIENTE ").append(clienteCorreo).append(' ').append(clientePassword).append('\n');
            }
            Files.writeString(destino, texto, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            LOG.info("Se crearon cuentas locales. Las contraseñas quedaron en {}", destino.toAbsolutePath());
        }
    }

    private String resolverPassword(String variable) {
        String definida = environment.getProperty(variable);
        if (definida != null && !definida.isBlank()) {
            return definida;
        }
        SecureRandom aleatorio = new SecureRandom();
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            clave.append(ALFABETO.charAt(aleatorio.nextInt(ALFABETO.length())));
        }
        return clave.toString();
    }
}
