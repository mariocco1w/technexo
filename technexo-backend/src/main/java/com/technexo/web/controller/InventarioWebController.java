package com.technexo.web.controller;

import com.technexo.catalogo.service.CatalogoService;
import com.technexo.inventario.dto.IngresoDto;
import com.technexo.inventario.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InventarioWebController {
    private final InventarioService inventarioService;
    private final CatalogoService catalogoService;

    @GetMapping("/inventario")
    public String ver(Model model) {
        model.addAttribute("inventario", inventarioService.listarInventario());
        model.addAttribute("movimientos", inventarioService.listarMovimientos());
        model.addAttribute("productos", catalogoService.listarProductos());
        return "inventario";
    }

    @PostMapping("/inventario/entrada")
    public String entrada(@ModelAttribute IngresoDto dto, RedirectAttributes ra) {
        try {
            inventarioService.registrarIngreso(dto, "WEB");
            ra.addFlashAttribute("ok", "Ingreso registrado: +" + dto.cantidad() + " unidades al producto " + dto.idProducto() + ".");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventario";
    }

    public InventarioWebController(final InventarioService inventarioService, final CatalogoService catalogoService) {
        this.inventarioService = inventarioService;
        this.catalogoService = catalogoService;
    }
}
