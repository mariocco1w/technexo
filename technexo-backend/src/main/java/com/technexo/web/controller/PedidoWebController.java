package com.technexo.web.controller;

import com.technexo.catalogo.service.CatalogoService;
import com.technexo.pedidos.dto.PedidoCrearDto;
import com.technexo.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PedidoWebController {
    private final PedidoService pedidoService;
    private final CatalogoService catalogoService;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("pedido", new PedidoCrearDto());
        model.addAttribute("productos", catalogoService.listarProductos());
        return "checkout";
    }

    @PostMapping("/pedidos/crear")
    public String crear(@Valid @ModelAttribute("pedido") PedidoCrearDto dto, BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors() || dto.getItems() == null || dto.getItems().isEmpty()) {
            model.addAttribute("error", "Debe agregar al menos un producto y completar datos válidos.");
            model.addAttribute("productos", catalogoService.listarProductos());
            return "checkout";
        }
        try {
            var p = pedidoService.crearPedido(dto);
            ra.addFlashAttribute("ok", "Pedido registrado y pagado correctamente (ID: " + p.getIdPedido() + ")");
            return "redirect:/pedidos";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("productos", catalogoService.listarProductos());
            return "checkout";
        }
    }

    @GetMapping("/pedidos")
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.listar());
        return "pedidos";
    }

    public PedidoWebController(final PedidoService pedidoService, final CatalogoService catalogoService) {
        this.pedidoService = pedidoService;
        this.catalogoService = catalogoService;
    }
}
