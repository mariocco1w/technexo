package com.technexo.seguridad;

import com.technexo.common.tiempo.FechaUtil;
import com.technexo.seguridad.entity.Rol;
import com.technexo.seguridad.entity.Usuario;
import com.technexo.seguridad.repository.RolRepository;
import com.technexo.seguridad.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder,
                          AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Usuario registrarCliente(String nombre, String correo, String password) {
        String correoNormal = normalizarCorreo(correo);
        if (usuarioRepository.findByCorreoIgnoreCase(correoNormal).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese correo");
        }
        validarPassword(password);
        Usuario usuario = base(nombre, correoNormal, password, "CLIENTE");
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public boolean asegurarCuenta(String nombre, String correo, String password, String rol) {
        String correoNormal = normalizarCorreo(correo);
        if (usuarioRepository.findByCorreoIgnoreCase(correoNormal).isPresent()) {
            return false;
        }
        validarPassword(password);
        usuarioRepository.save(base(nombre, correoNormal, password, rol));
        return true;
    }

    @Transactional
    public void actualizarNombre(String nombre) {
        Usuario usuario = actual();
        if (nombre == null || nombre.isBlank() || nombre.length() > 150) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener hasta 150 caracteres");
        }
        usuario.setNombre(nombre.trim());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarPassword(String actualClave, String nuevaClave) {
        Usuario usuario = actual();
        if (!passwordEncoder.matches(actualClave == null ? "" : actualClave, usuario.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual no coincide");
        }
        validarPassword(nuevaClave);
        if (passwordEncoder.matches(nuevaClave, usuario.getPasswordHash())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser distinta");
        }
        usuario.setPasswordHash(passwordEncoder.encode(nuevaClave));
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByCorreoAsc();
    }

    @Transactional
    public void cambiarRol(Integer idUsuario, String rolNombre) {
        if (!"ADMIN".equals(rolNombre) && !"CLIENTE".equals(rolNombre)) {
            throw new IllegalArgumentException("Rol no permitido");
        }
        Usuario destino = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        boolean esSiMismo = destino.getIdUsuario().equals(SesionActual.idUsuario());
        boolean seguiraAdmin = "ADMIN".equals(rolNombre);
        PoliticaRoles.validarCambio(SesionActual.esAdmin(), esSiMismo, seguiraAdmin || !destino.tieneRol("ADMIN"),
                usuarioRepository.contarActivosConRol("ADMIN"));
        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new IllegalStateException("El rol no está configurado"));
        destino.getRoles().clear();
        destino.getRoles().add(rol);
        usuarioRepository.save(destino);
        auditoriaService.registrar("CAMBIAR_ROL", "Usuario", String.valueOf(idUsuario), "Rol asignado: " + rolNombre);
    }

    @Transactional
    public void cambiarActivo(Integer idUsuario, boolean activo) {
        Usuario destino = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        boolean esSiMismo = destino.getIdUsuario().equals(SesionActual.idUsuario());
        PoliticaRoles.validarActivacion(SesionActual.esAdmin(), esSiMismo, !activo, destino.tieneRol("ADMIN"),
                usuarioRepository.contarActivosConRol("ADMIN"));
        destino.setActivo(activo);
        usuarioRepository.save(destino);
        auditoriaService.registrar(activo ? "ACTIVAR_USUARIO" : "DESACTIVAR_USUARIO", "Usuario",
                String.valueOf(idUsuario), destino.getCorreo());
    }

    private Usuario actual() {
        Integer id = SesionActual.idUsuario();
        if (id == null) {
            throw new AccesoDenegadoException("Debe iniciar sesión");
        }
        return usuarioRepository.findById(id).orElseThrow(() -> new IllegalStateException("La cuenta ya no existe"));
    }

    private Usuario base(String nombre, String correo, String password, String rolNombre) {
        if (nombre == null || nombre.isBlank() || nombre.length() > 150) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener hasta 150 caracteres");
        }
        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new IllegalStateException("El rol " + rolNombre + " no está configurado"));
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setCorreo(correo);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setActivo(true);
        usuario.setFechaCreacion(FechaUtil.ahoraUtc());
        usuario.setRoles(Set.of(rol));
        return usuario;
    }

    static void validarPassword(String password) {
        if (password == null || password.length() < 10 || password.length() > 80) {
            throw new IllegalArgumentException("La contraseña debe tener entre 10 y 80 caracteres");
        }
        boolean letra = password.chars().anyMatch(Character::isLetter);
        boolean digito = password.chars().anyMatch(Character::isDigit);
        if (!letra || !digito) {
            throw new IllegalArgumentException("La contraseña debe incluir letras y números");
        }
    }

    static String normalizarCorreo(String correo) {
        if (correo == null || !correo.contains("@") || correo.length() > 254) {
            throw new IllegalArgumentException("Correo no válido");
        }
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
