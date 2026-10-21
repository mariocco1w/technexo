package com.technexo.reservas.service;

import com.technexo.catalogo.entity.Producto;
import com.technexo.catalogo.repository.ProductoRepository;
import com.technexo.common.tiempo.FechaUtil;
import com.technexo.inventario.repository.InventarioRepository;
import com.technexo.inventario.service.InventarioService;
import com.technexo.reservas.dto.ReservaCrearDto;
import com.technexo.reservas.dto.ReservaDto;
import com.technexo.reservas.entity.Reserva;
import com.technexo.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReservaService {
    private static final int MINUTOS_EXPIRACION = 30;
    private final ReservaRepository reservaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final InventarioService inventarioService;

    public Reserva crearReserva(ReservaCrearDto dto) {
        Producto producto = productoRepository.findById(dto.getIdProducto()).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        LocalDateTime ahora = FechaUtil.ahoraUtc();
        int stockActual = inventarioRepository.findById(dto.getIdProducto()).map(i -> i.getStockActual()).orElse(0);
        int reservadas = reservaRepository.sumReservasPendientesVigentes(dto.getIdProducto(), ahora);
        int disponible = stockActual - reservadas;
        if (disponible < dto.getCantidad()) {
            throw new IllegalStateException("No hay disponibilidad suficiente. Disponible: " + Math.max(disponible, 0));
        }
        Reserva reserva = Reserva.builder().producto(producto).cantidad(dto.getCantidad()).fechaCreacion(ahora).fechaExpiracion(ahora.plusMinutes(MINUTOS_EXPIRACION)).estado("PENDIENTE").identificadorCliente(dto.getIdentificadorCliente()).build();
        return reservaRepository.save(reserva);
    }

    /**
     * Confirma una reserva PENDIENTE vigente: descuenta el stock fisico y registra
     * el movimiento de inventario (SALIDA). La reserva deja de estar PENDIENTE, por
     * lo que la disponibilidad se mantiene consistente.
     */
    public Reserva confirmar(Long idReserva) {
        Reserva r = reservaRepository.findById(idReserva).orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        if (!"PENDIENTE".equals(r.getEstado())) {
            throw new IllegalStateException("Solo se pueden confirmar reservas PENDIENTE. Estado actual: " + r.getEstado());
        }
        if (r.getFechaExpiracion().isBefore(FechaUtil.ahoraUtc())) {
            r.setEstado("EXPIRADA");
            reservaRepository.save(r);
            throw new IllegalStateException("La reserva estaba expirada y fue marcada como EXPIRADA");
        }
        inventarioService.registrarSalida(r.getProducto(), r.getCantidad(), "Confirmación de reserva", "RES-" + r.getIdReserva(), "WEB");
        r.setEstado("CONFIRMADA");
        return reservaRepository.save(r);
    }

    public Reserva cancelar(Long idReserva) {
        Reserva r = reservaRepository.findById(idReserva).orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        if ("CANCELADA".equals(r.getEstado()) || "CONFIRMADA".equals(r.getEstado())) {
            throw new IllegalStateException("La reserva ya no puede cancelarse. Estado actual: " + r.getEstado());
        }
        r.setEstado("CANCELADA");
        return reservaRepository.save(r);
    }

    @Transactional(readOnly = true)
    public List<ReservaDto> listar() {
        return reservaRepository.findAll().stream().sorted((a, b) -> b.getIdReserva().compareTo(a.getIdReserva())).map(this::aDto).toList();
    }

    private ReservaDto aDto(Reserva r) {
        return new ReservaDto(r.getIdReserva(), r.getProducto() != null ? r.getProducto().getIdProducto() : null, r.getProducto() != null ? r.getProducto().getSku() : null, r.getProducto() != null ? r.getProducto().getNombre() : null, r.getCantidad(), r.getFechaCreacion(), r.getFechaExpiracion(), r.getEstado(), r.getIdentificadorCliente());
    }

    public ReservaService(final ReservaRepository reservaRepository, final ProductoRepository productoRepository, final InventarioRepository inventarioRepository, final InventarioService inventarioService) {
        this.reservaRepository = reservaRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.inventarioService = inventarioService;
    }
}
