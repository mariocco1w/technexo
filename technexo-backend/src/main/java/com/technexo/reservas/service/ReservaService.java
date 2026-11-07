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
import com.technexo.seguridad.AccesoDenegadoException;
import com.technexo.seguridad.AuditoriaService;
import com.technexo.seguridad.SesionActual;
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
    private final AuditoriaService auditoriaService;

    public Reserva crearReserva(ReservaCrearDto dto) {
        Producto producto = productoRepository.findById(dto.getIdProducto()).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        if (SesionActual.idUsuario() == null) {
            throw new AccesoDenegadoException("Debe iniciar sesión para reservar");
        }
        if (!"ACTIVO".equals(producto.getEstado())) {
            throw new IllegalStateException("El producto no está disponible para reserva");
        }
        LocalDateTime ahora = FechaUtil.ahoraUtc();
        int stockActual = inventarioRepository.findById(dto.getIdProducto()).map(i -> i.getStockActual()).orElse(0);
        int reservadas = reservaRepository.sumReservasPendientesVigentes(dto.getIdProducto(), ahora);
        int disponible = stockActual - reservadas;
        if (disponible < dto.getCantidad()) {
            throw new IllegalStateException("No hay disponibilidad suficiente. Disponible: " + Math.max(disponible, 0));
        }
        Reserva reserva = Reserva.builder().producto(producto).cantidad(dto.getCantidad()).fechaCreacion(ahora).fechaExpiracion(ahora.plusMinutes(MINUTOS_EXPIRACION)).estado("PENDIENTE").identificadorCliente(SesionActual.correo()).build();
        reserva.setIdUsuario(SesionActual.idUsuario());
        return reservaRepository.save(reserva);
    }

    /**
     * Confirma una reserva PENDIENTE vigente: descuenta el stock fisico y registra
     * el movimiento de inventario (SALIDA). La reserva deja de estar PENDIENTE, por
     * lo que la disponibilidad se mantiene consistente.
     */
    public Reserva confirmar(Long idReserva) {
        if (!SesionActual.esAdmin()) {
            throw new AccesoDenegadoException("Solo un administrador puede confirmar reservas");
        }
        Reserva r = reservaRepository.findById(idReserva).orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        if (!"PENDIENTE".equals(r.getEstado())) {
            throw new IllegalStateException("Solo se pueden confirmar reservas PENDIENTE. Estado actual: " + r.getEstado());
        }
        if (r.getFechaExpiracion().isBefore(FechaUtil.ahoraUtc())) {
            r.setEstado("EXPIRADA");
            reservaRepository.save(r);
            throw new IllegalStateException("La reserva estaba expirada y fue marcada como EXPIRADA");
        }
        inventarioService.registrarSalida(r.getProducto(), r.getCantidad(), "Confirmación de reserva", "RES-" + r.getIdReserva(), SesionActual.correo());
        r.setEstado("CONFIRMADA");
        Reserva guardada = reservaRepository.save(r);
        auditoriaService.registrar("CONFIRMAR_RESERVA", "Reserva", String.valueOf(guardada.getIdReserva()), guardada.getProducto().getSku());
        return guardada;
    }

    public Reserva cancelar(Long idReserva) {
        Reserva r = reservaRepository.findById(idReserva).orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        if (!SesionActual.esAdmin()) {
            Integer idUsuario = SesionActual.idUsuario();
            if (idUsuario == null || r.getIdUsuario() == null || !idUsuario.equals(r.getIdUsuario())) {
                throw new AccesoDenegadoException("No puede cancelar reservas de otra cuenta");
            }
        }
        if ("CANCELADA".equals(r.getEstado()) || "CONFIRMADA".equals(r.getEstado())) {
            throw new IllegalStateException("La reserva ya no puede cancelarse. Estado actual: " + r.getEstado());
        }
        r.setEstado("CANCELADA");
        Reserva guardada = reservaRepository.save(r);
        auditoriaService.registrar("CANCELAR_RESERVA", "Reserva", String.valueOf(guardada.getIdReserva()), guardada.getEstado());
        return guardada;
    }

    @Transactional(readOnly = true)
    public List<ReservaDto> listar() {
        if (SesionActual.esAdmin()) {
            return reservaRepository.findTodas().stream().map(this::aDto).toList();
        }
        return listarPropias();
    }

    @Transactional(readOnly = true)
    public List<ReservaDto> listarPropias() {
        Integer idUsuario = SesionActual.idUsuario();
        if (idUsuario == null) {
            throw new AccesoDenegadoException("Debe iniciar sesión");
        }
        return reservaRepository.findDelUsuario(idUsuario).stream().map(this::aDto).toList();
    }

    private ReservaDto aDto(Reserva r) {
        return new ReservaDto(r.getIdReserva(), r.getProducto() != null ? r.getProducto().getIdProducto() : null, r.getProducto() != null ? r.getProducto().getSku() : null, r.getProducto() != null ? r.getProducto().getNombre() : null, r.getCantidad(), r.getFechaCreacion(), r.getFechaExpiracion(), r.getEstado(), r.getIdentificadorCliente());
    }

    public ReservaService(final ReservaRepository reservaRepository, final ProductoRepository productoRepository, final InventarioRepository inventarioRepository, final InventarioService inventarioService, final AuditoriaService auditoriaService) {
        this.reservaRepository = reservaRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.inventarioService = inventarioService;
        this.auditoriaService = auditoriaService;
    }
}
