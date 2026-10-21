package com.technexo.pedidos.service;

import com.technexo.catalogo.entity.Producto;
import com.technexo.catalogo.repository.ProductoRepository;
import com.technexo.common.tiempo.FechaUtil;
import com.technexo.inventario.repository.InventarioRepository;
import com.technexo.inventario.service.InventarioService;
import com.technexo.pedidos.dto.PedidoCrearDto;
import com.technexo.pedidos.dto.PedidoDto;
import com.technexo.pedidos.entity.DetallePedido;
import com.technexo.pedidos.entity.Pedido;
import com.technexo.pedidos.repository.DetallePedidoRepository;
import com.technexo.pedidos.repository.PedidoRepository;
import com.technexo.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detalleRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final ReservaRepository reservaRepository;
    private final InventarioService inventarioService;

    public Pedido crearPedido(PedidoCrearDto dto) {
        LocalDateTime ahora = FechaUtil.ahoraUtc();
        List<DetalleTemp> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (PedidoCrearDto.Item item : dto.getItems()) {
            if (item.getIdProducto() == null || item.getCantidad() == null || item.getCantidad() < 1) {
                continue;
            }
            Producto p = productoRepository.findById(item.getIdProducto()).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getIdProducto()));
            int stockActual = inventarioRepository.findById(item.getIdProducto()).map(i -> i.getStockActual()).orElse(0);
            int reservadas = reservaRepository.sumReservasPendientesVigentes(item.getIdProducto(), ahora);
            int disponible = stockActual - reservadas;
            if (disponible < item.getCantidad()) {
                throw new IllegalStateException("Disponibilidad insuficiente para " + p.getSku() + ". Disponible: " + Math.max(disponible, 0) + ", solicitado: " + item.getCantidad());
            }
            subtotal = subtotal.add(p.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
            detalles.add(new DetalleTemp(p, item.getCantidad(), p.getPrecio()));
        }
        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un producto válido");
        }
        Pedido pedido = Pedido.builder().fecha(ahora).estado("PAGADO").subtotal(subtotal).total(subtotal).moneda("GTQ").nombreCliente(dto.getNombreCliente()).correoCliente(dto.getCorreoCliente()).telefonoCliente(dto.getTelefonoCliente()).claveIdempotencia(UUID.randomUUID()).build();
        pedido = pedidoRepository.save(pedido);
        for (DetalleTemp t : detalles) {
            DetallePedido d = DetallePedido.builder().pedido(pedido).producto(t.producto()).cantidad(t.cantidad()).precioUnitarioHistorico(t.precioHistorico()).build();
            detalleRepository.save(d);
            inventarioService.registrarSalida(t.producto(), t.cantidad(), "Confirmación de pedido/pago", "PED-" + pedido.getIdPedido(), "WEB");
        }
        return pedido;
    }

    @Transactional(readOnly = true)
    public List<PedidoDto> listar() {
        return pedidoRepository.findAll().stream().sorted((a, b) -> b.getIdPedido().compareTo(a.getIdPedido())).map(this::aDto).toList();
    }

    private PedidoDto aDto(Pedido p) {
        List<PedidoDto.ItemDto> items = detalleRepository.findByPedido_IdPedido(p.getIdPedido()).stream().map(d -> new PedidoDto.ItemDto(d.getProducto().getIdProducto(), d.getProducto().getSku(), d.getProducto().getNombre(), d.getCantidad(), d.getPrecioUnitarioHistorico(), d.getPrecioUnitarioHistorico().multiply(java.math.BigDecimal.valueOf(d.getCantidad())))).toList();
        return new PedidoDto(p.getIdPedido(), p.getEstado(), p.getFecha(), p.getSubtotal(), p.getTotal(), p.getMoneda(), p.getNombreCliente(), items);
    }

    private record DetalleTemp(Producto producto, Integer cantidad, BigDecimal precioHistorico) {
    }

    public PedidoService(final PedidoRepository pedidoRepository, final DetallePedidoRepository detalleRepository, final ProductoRepository productoRepository, final InventarioRepository inventarioRepository, final ReservaRepository reservaRepository, final InventarioService inventarioService) {
        this.pedidoRepository = pedidoRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.reservaRepository = reservaRepository;
        this.inventarioService = inventarioService;
    }
}
