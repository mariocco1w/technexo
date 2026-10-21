package com.technexo.web.controller;

import com.technexo.catalogo.service.CatalogoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductoWebController {
    private final CatalogoService catalogoService;

    @GetMapping("/producto/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", catalogoService.obtenerProducto(id));
        model.addAttribute("atributos", catalogoService.listarAtributos(id));
        return "producto-detalle";
    }

    public ProductoWebController(final CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }
}
