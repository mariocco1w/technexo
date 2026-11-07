package com.technexo.web.controller;

import com.technexo.pedidos.service.PedidoService;
import com.technexo.reservas.service.ReservaService;
import com.technexo.seguridad.SesionActual;
import com.technexo.seguridad.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CuentaController {

    private final ReservaService reservaService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public CuentaController(ReservaService reservaService, PedidoService pedidoService, UsuarioService usuarioService) {
        this.reservaService = reservaService;
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/cuenta")
    public String inicio(Model model) {
        var reservas = reservaService.listarPropias();
        var pedidos = pedidoService.listarPropios();
        model.addAttribute("nombre", SesionActual.nombre());
        model.addAttribute("reservas", reservas.stream().limit(5).toList());
        model.addAttribute("pedidos", pedidos.stream().limit(5).toList());
        model.addAttribute("totalReservas", reservas.size());
        model.addAttribute("reservasPendientes", reservas.stream().filter(r -> "PENDIENTE".equals(r.estado())).count());
        model.addAttribute("totalPedidos", pedidos.size());
        return "cuenta/inicio";
    }

    @GetMapping("/cuenta/perfil")
    public String perfil(Model model) {
        model.addAttribute("nombre", SesionActual.nombre());
        model.addAttribute("correo", SesionActual.correo());
        return "cuenta/perfil";
    }

    @PostMapping("/cuenta/perfil")
    public String actualizarNombre(@RequestParam String nombre, RedirectAttributes ra) {
        try {
            usuarioService.actualizarNombre(nombre);
            ra.addFlashAttribute("ok", "Nombre actualizado.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cuenta/perfil";
    }

    @PostMapping("/cuenta/password")
    public String password(@RequestParam String actual, @RequestParam String nueva, RedirectAttributes ra) {
        try {
            usuarioService.cambiarPassword(actual, nueva);
            ra.addFlashAttribute("ok", "Contraseña actualizada.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cuenta/perfil";
    }

    @GetMapping("/cuenta/reservas")
    public String reservas(Model model) {
        model.addAttribute("reservas", reservaService.listarPropias());
        return "cuenta/reservas";
    }

    @PostMapping("/cuenta/reservas/{id}/cancelar")
    public String cancelar(@org.springframework.web.bind.annotation.PathVariable Long id, RedirectAttributes ra) {
        try {
            reservaService.cancelar(id);
            ra.addFlashAttribute("ok", "Reserva cancelada.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cuenta/reservas";
    }

    @GetMapping("/cuenta/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.listarPropios());
        return "cuenta/pedidos";
    }
}
