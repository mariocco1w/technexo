package com.technexo.inventario.controller;

import com.technexo.inventario.dto.IngresoDto;
import com.technexo.inventario.dto.InventarioDto;
import com.technexo.inventario.dto.MovimientoDto;
import com.technexo.inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioApiController {
    private final InventarioService inventarioService;

    @GetMapping
    public List<InventarioDto> inventario() {
        return inventarioService.listarInventario();
    }

    @GetMapping("/movimientos")
    public List<MovimientoDto> movimientos() {
        return inventarioService.listarMovimientos();
    }

    @PostMapping("/entrada")
    public ResponseEntity<MovimientoDto> entrada(@Valid @RequestBody IngresoDto dto) {
        inventarioService.registrarIngreso(dto, "API");
        MovimientoDto ultimo = inventarioService.listarMovimientos().stream().findFirst().orElse(null);
        return ResponseEntity.ok(ultimo);
    }

    public InventarioApiController(final InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }
}
