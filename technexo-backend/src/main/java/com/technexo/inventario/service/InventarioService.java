package com.technexo.inventario.service;

import com.technexo.catalogo.entity.Producto;
import com.technexo.catalogo.repository.ProductoRepository;
import com.technexo.common.tiempo.FechaUtil;
import com.technexo.inventario.dto.IngresoDto;
import com.technexo.inventario.dto.InventarioDto;
import com.technexo.inventario.dto.MovimientoDto;
import com.technexo.inventario.entity.Inventario;
import com.technexo.inventario.entity.MovimientoInventario;
import com.technexo.inventario.repository.InventarioRepository;
import com.technexo.inventario.repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class InventarioService {
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    public void registrarEntrada(Producto producto, int cantidad, String motivo, String referencia, String creadoPor) {
        Inventario inv = inventarioRepository.findByIdProductoForUpdate(producto.getIdProducto()).orElseThrow(() -> new IllegalStateException("Inventario no existe para producto " + producto.getSku()));
        inv.setStockActual(inv.getStockActual() + cantidad);
        inv.setFechaActualizacion(FechaUtil.ahoraUtc());
        inventarioRepository.save(inv);
        MovimientoInventario mov = MovimientoInventario.builder().producto(producto).tipoMovimiento("ENTRADA").cantidad(cantidad).fecha(FechaUtil.ahoraUtc()).motivo(motivo).referencia(referencia).creadoPor(creadoPor).build();
        movimientoRepository.save(mov);
    }

    public void registrarSalida(Producto producto, int cantidad, String motivo, String referencia, String creadoPor) {
        Inventario inv = inventarioRepository.findByIdProductoForUpdate(producto.getIdProducto()).orElseThrow(() -> new IllegalStateException("Inventario no existe para producto " + producto.getSku()));
        if (inv.getStockActual() < cantidad) {
            throw new IllegalStateException("Stock fisico insuficiente para " + producto.getSku() + ". Stock actual: " + inv.getStockActual() + ", solicitado: " + cantidad);
        }
        inv.setStockActual(inv.getStockActual() - cantidad);
        inv.setFechaActualizacion(FechaUtil.ahoraUtc());
        inventarioRepository.save(inv);
        MovimientoInventario mov = MovimientoInventario.builder().producto(producto).tipoMovimiento("SALIDA").cantidad(cantidad).fecha(FechaUtil.ahoraUtc()).motivo(motivo).referencia(referencia).creadoPor(creadoPor).build();
        movimientoRepository.save(mov);
    }

    public void registrarIngreso(IngresoDto dto, String creadoPor) {
        Producto producto = productoRepository.findById(dto.idProducto()).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + dto.idProducto()));
        String motivo = (dto.motivo() == null || dto.motivo().isBlank()) ? "Registro de ingreso de producto" : dto.motivo();
        String referencia = (dto.referencia() == null || dto.referencia().isBlank()) ? "ING-" + System.currentTimeMillis() : dto.referencia();
        registrarEntrada(producto, dto.cantidad(), motivo, referencia, creadoPor);
    }

    @Transactional(readOnly = true)
    public List<MovimientoDto> listarMovimientos() {
        return movimientoRepository.findAllByOrderByIdMovimientoDesc().stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public List<InventarioDto> listarInventario() {
        return inventarioRepository.findAll().stream().map(i -> new InventarioDto(i.getIdProducto(), i.getProducto() != null ? i.getProducto().getSku() : null, i.getProducto() != null ? i.getProducto().getNombre() : null, i.getStockActual(), i.getStockMinimo(), i.getUbicacion())).toList();
    }

    private MovimientoDto aDto(MovimientoInventario m) {
        return new MovimientoDto(m.getIdMovimiento(), m.getProducto() != null ? m.getProducto().getIdProducto() : null, m.getProducto() != null ? m.getProducto().getSku() : null, m.getProducto() != null ? m.getProducto().getNombre() : null, m.getTipoMovimiento(), m.getCantidad(), m.getFecha(), m.getMotivo(), m.getReferencia(), m.getCreadoPor());
    }

    public InventarioService(final InventarioRepository inventarioRepository, final MovimientoInventarioRepository movimientoRepository, final ProductoRepository productoRepository) {
        this.inventarioRepository = inventarioRepository;
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }
}
