package com.technexo.web.controller;

import com.technexo.catalogo.entity.DisponibilidadProducto;
import com.technexo.catalogo.repository.DisponibilidadProductoRepository;
import com.technexo.catalogo.repository.ProductoRepository;
import com.technexo.inventario.repository.InventarioRepository;
import com.technexo.inventario.service.InventarioService;
import com.technexo.pedidos.repository.PedidoRepository;
import com.technexo.pedidos.service.PedidoService;
import com.technexo.reservas.repository.ReservaRepository;
import com.technexo.reservas.service.ReservaService;
import com.technexo.seguridad.AuditoriaService;
import com.technexo.seguridad.SesionActual;
import com.technexo.seguridad.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminPanelController {

    private final ProductoRepository productoRepository;
    private final DisponibilidadProductoRepository disponibilidadRepository;
    private final ReservaRepository reservaRepository;
    private final ReservaService reservaService;
    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;
    private final InventarioRepository inventarioRepository;
    private final InventarioService inventarioService;
    private final AuditoriaService auditoriaService;
    private final UsuarioService usuarioService;

    public AdminPanelController(ProductoRepository productoRepository, DisponibilidadProductoRepository disponibilidadRepository,
                                ReservaRepository reservaRepository, ReservaService reservaService, PedidoRepository pedidoRepository,
                                PedidoService pedidoService, InventarioRepository inventarioRepository, InventarioService inventarioService,
                                AuditoriaService auditoriaService, UsuarioService usuarioService) {
        this.productoRepository = productoRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.reservaRepository = reservaRepository;
        this.reservaService = reservaService;
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
        this.inventarioRepository = inventarioRepository;
        this.inventarioService = inventarioService;
        this.auditoriaService = auditoriaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/admin")
    public String inicio(Model model) {
        long disponibles = disponibilidadRepository.findAll().stream()
                .filter(fila -> "ACTIVO".equals(fila.getEstadoProducto()) && entero(fila.getStockDisponible()) > 0)
                .count();
        long entradas = inventarioService.listarMovimientos().stream().filter(m -> "ENTRADA".equals(m.tipoMovimiento())).count();
        long salidas = inventarioService.listarMovimientos().stream().filter(m -> "SALIDA".equals(m.tipoMovimiento())).count();
        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("productosActivos", productoRepository.countByEstado("ACTIVO"));
        model.addAttribute("disponibles", disponibles);
        model.addAttribute("stockBajo", inventarioRepository.findConStockBajo().size());
        model.addAttribute("totalReservas", reservaRepository.count());
        model.addAttribute("reservasPendientes", reservaRepository.countByEstado("PENDIENTE"));
        model.addAttribute("totalPedidos", pedidoRepository.count());
        model.addAttribute("pedidosPendientes", pedidoRepository.countByEstado("PENDIENTE"));
        model.addAttribute("movimientos", inventarioService.listarMovimientos().stream().limit(8).toList());
        model.addAttribute("entradas", entradas);
        model.addAttribute("salidas", salidas);
        model.addAttribute("auditoria", auditoriaService.recientes());
        return "admin/inicio";
    }

    @GetMapping("/admin/reservas")
    public String reservas(Model model) {
        model.addAttribute("reservas", reservaService.listar());
        return "admin/reservas";
    }

    @PostMapping("/admin/reservas/{id}/confirmar")
    public String confirmarReserva(@PathVariable Long id, RedirectAttributes ra) {
        return ejecutar(ra, "/admin/reservas", () -> {
            reservaService.confirmar(id);
            return "Reserva confirmada.";
        });
    }

    @PostMapping("/admin/reservas/{id}/cancelar")
    public String cancelarReserva(@PathVariable Long id, RedirectAttributes ra) {
        return ejecutar(ra, "/admin/reservas", () -> {
            reservaService.cancelar(id);
            return "Reserva cancelada.";
        });
    }

    @GetMapping("/admin/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.listar());
        return "admin/pedidos";
    }

    @PostMapping("/admin/pedidos/{id}/estado")
    public String estadoPedido(@PathVariable Long id, @RequestParam String estado, RedirectAttributes ra) {
        return ejecutar(ra, "/admin/pedidos", () -> {
            pedidoService.actualizarEstado(id, estado);
            return "Estado del pedido actualizado.";
        });
    }

    @GetMapping("/admin/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("yo", SesionActual.idUsuario());
        return "admin/usuarios";
    }

    @PostMapping("/admin/usuarios/{id}/rol")
    public String rol(@PathVariable Integer id, @RequestParam String rol, RedirectAttributes ra) {
        return ejecutar(ra, "/admin/usuarios", () -> {
            usuarioService.cambiarRol(id, rol);
            return "Rol actualizado.";
        });
    }

    @PostMapping("/admin/usuarios/{id}/activo")
    public String activo(@PathVariable Integer id, @RequestParam boolean activo, RedirectAttributes ra) {
        return ejecutar(ra, "/admin/usuarios", () -> {
            usuarioService.cambiarActivo(id, activo);
            return activo ? "Cuenta activada." : "Cuenta desactivada.";
        });
    }

    private String ejecutar(RedirectAttributes ra, String destino, java.util.function.Supplier<String> accion) {
        try {
            ra.addFlashAttribute("ok", accion.get());
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:" + destino;
    }

    private int entero(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
