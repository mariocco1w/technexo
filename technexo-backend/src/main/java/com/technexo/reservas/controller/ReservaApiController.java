package com.technexo.reservas.controller;

import com.technexo.reservas.dto.ReservaCrearDto;
import com.technexo.reservas.dto.ReservaDto;
import com.technexo.reservas.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaApiController {
    private final ReservaService reservaService;

    @GetMapping
    public List<ReservaDto> listar() {
        return reservaService.listar();
    }

    @PostMapping
    public ResponseEntity<ReservaDto> crear(@Valid @RequestBody ReservaCrearDto dto) {
        var r = reservaService.crearReserva(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.listar().stream().filter(x -> x.id().equals(r.getIdReserva())).findFirst().orElse(null));
    }

    @PostMapping("/{id}/confirmar")
    public ReservaDto confirmar(@PathVariable Long id) {
        var r = reservaService.confirmar(id);
        return reservaService.listar().stream().filter(x -> x.id().equals(r.getIdReserva())).findFirst().orElse(null);
    }

    @PostMapping("/{id}/cancelar")
    public ReservaDto cancelar(@PathVariable Long id) {
        var r = reservaService.cancelar(id);
        return reservaService.listar().stream().filter(x -> x.id().equals(r.getIdReserva())).findFirst().orElse(null);
    }

    public ReservaApiController(final ReservaService reservaService) {
        this.reservaService = reservaService;
    }
}
