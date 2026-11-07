package com.technexo.web.controller;

import com.technexo.seguridad.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccesoController {

    private final UsuarioService usuarioService;
    private final com.technexo.seguridad.UsuarioDetallesService detallesService;

    public AccesoController(UsuarioService usuarioService, com.technexo.seguridad.UsuarioDetallesService detallesService) {
        this.usuarioService = usuarioService;
        this.detallesService = detallesService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String crear(@RequestParam String nombre,
                        @RequestParam String correo,
                        @RequestParam String password,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {
        try {
            usuarioService.registrarCliente(nombre, correo, password);
            var principal = detallesService.loadUserByUsername(correo);
            var autenticacion = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            autenticacion.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(autenticacion);
            request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
            return "redirect:/cuenta";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("nombre", nombre);
            model.addAttribute("correo", correo);
            return "registro";
        }
    }

    @RequestMapping(value = "/acceso-denegado", method = {RequestMethod.GET, RequestMethod.POST})
    public String denegado(Model model) {
        model.addAttribute("error", "No tienes permiso para entrar a esa sección.");
        return "error";
    }
}
