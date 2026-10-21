package com.technexo.catalogo.controller;

import com.technexo.catalogo.dto.AtributoDto;
import com.technexo.catalogo.dto.CategoriaDto;
import com.technexo.catalogo.dto.ProductoDto;
import com.technexo.catalogo.service.CatalogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductoApiController {
    private final CatalogoService catalogoService;

    @GetMapping("/productos")
    public List<ProductoDto> listar(@RequestParam(name = "q", required = false) String q) {
        if (q == null || q.isBlank()) {
            return catalogoService.listarProductos();
        }
        return catalogoService.buscar(q);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoDto> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(catalogoService.obtenerProducto(id));
    }

    @GetMapping("/productos/{id}/atributos")
    public List<AtributoDto> atributos(@PathVariable Integer id) {
        return catalogoService.listarAtributos(id);
    }

    @GetMapping("/categorias")
    public List<CategoriaDto> categorias() {
        return catalogoService.listarCategorias();
    }

    public ProductoApiController(final CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }
}
