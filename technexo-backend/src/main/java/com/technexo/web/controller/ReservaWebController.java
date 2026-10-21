package com.technexo.web.controller;

import com.technexo.reservas.dto.ReservaCrearDto;
import com.technexo.reservas.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservaWebController {
    private final ReservaService reservaService;

    @PostMapping("/reservas/crear")
    public String crear(@Valid @ModelAttribute ReservaCrearDto dto, BindingResult br, @RequestParam Integer idProducto, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", "Datos inválidos para crear la reserva");
            return "redirect:/producto/" + idProducto;
        }
        try {
            var r = reservaService.crearReserva(dto);
            ra.addFlashAttribute("ok", "Reserva creada correctamente (ID: " + r.getIdReserva() + "). Expira en 30 minutos.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/producto/" + idProducto;
    }

    @GetMapping("/reservas")
    public String listar(Model model) {
        model.addAttribute("reservas", reservaService.listar());
        return "reservas";
    }

    @PostMapping("/reservas/{id}/confirmar")
    public String confirmar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            reservaService.confirmar(id);
            ra.addFlashAttribute("ok", "Reserva " + id + " confirmada. Se descontó stock y se registró la SALIDA.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas";
    }

    @PostMapping("/reservas/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            reservaService.cancelar(id);
            ra.addFlashAttribute("ok", "Reserva " + id + " cancelada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas";
    }

    public ReservaWebController(final ReservaService reservaService) {
        this.reservaService = reservaService;
    }
}
