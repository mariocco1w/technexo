package com.technexo.pedidos.controller;

import com.technexo.pedidos.dto.PedidoCrearDto;
import com.technexo.pedidos.dto.PedidoDto;
import com.technexo.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoApiController {
    private final PedidoService pedidoService;

    @GetMapping
    public List<PedidoDto> listar() {
        return pedidoService.listar();
    }

    @PostMapping
    public ResponseEntity<PedidoDto> crear(@Valid @RequestBody PedidoCrearDto dto) {
        var p = pedidoService.crearPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.listar().stream().filter(x -> x.id().equals(p.getIdPedido())).findFirst().orElse(null));
    }

    public PedidoApiController(final PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }
}
