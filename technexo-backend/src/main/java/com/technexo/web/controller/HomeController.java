package com.technexo.web.controller;

import com.technexo.catalogo.service.CatalogoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    private final CatalogoService catalogoService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("productos", catalogoService.listarProductos());
        return "index";
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(value = "categoria", required = false) String categoria, Model model) {
        model.addAttribute("categorias", catalogoService.listarCategorias());
        model.addAttribute("categoria", categoria);
        model.addAttribute("productos", catalogoService.listarPorCategoria(categoria));
        return "catalogo";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("categorias", catalogoService.listarCategorias());
        if (q == null || q.isBlank()) {
            model.addAttribute("productos", catalogoService.listarProductos());
        } else {
            model.addAttribute("productos", catalogoService.buscar(q));
            model.addAttribute("q", q);
        }
        return "catalogo";
    }

    public HomeController(final CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }
}
