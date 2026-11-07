package com.technexo.web.controller;

import com.technexo.catalogo.dto.ProductoForm;
import com.technexo.catalogo.repository.AtributoTecnicoRepository;
import com.technexo.catalogo.repository.CategoriaRepository;
import com.technexo.catalogo.repository.MarcaRepository;
import com.technexo.catalogo.service.CatalogoAdminService;
import com.technexo.catalogo.service.CatalogoService;
import com.technexo.inventario.dto.IngresoDto;
import com.technexo.inventario.service.InventarioService;
import com.technexo.seguridad.SesionActual;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminCatalogoController {

    private final CatalogoAdminService catalogoAdminService;
    private final CatalogoService catalogoService;
    private final MarcaRepository marcaRepository;
    private final CategoriaRepository categoriaRepository;
    private final AtributoTecnicoRepository atributoTecnicoRepository;
    private final InventarioService inventarioService;

    public AdminCatalogoController(CatalogoAdminService catalogoAdminService, CatalogoService catalogoService,
                                   MarcaRepository marcaRepository, CategoriaRepository categoriaRepository,
                                   AtributoTecnicoRepository atributoTecnicoRepository, InventarioService inventarioService) {
        this.catalogoAdminService = catalogoAdminService;
        this.catalogoService = catalogoService;
        this.marcaRepository = marcaRepository;
        this.categoriaRepository = categoriaRepository;
        this.atributoTecnicoRepository = atributoTecnicoRepository;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/admin/productos")
    public String productos(Model model) {
        model.addAttribute("productos", catalogoAdminService.listar());
        return "admin/productos";
    }

    @GetMapping("/admin/productos/nuevo")
    public String nuevo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ProductoForm());
        }
        opciones(model);
        return "admin/producto-form";
    }

    @GetMapping("/admin/productos/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        var producto = catalogoAdminService.listar().stream().filter(p -> p.getIdProducto().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        if (!model.containsAttribute("form")) {
            ProductoForm form = new ProductoForm();
            form.setId(producto.getIdProducto());
            form.setSku(producto.getSku());
            form.setNombre(producto.getNombre());
            form.setDescripcion(producto.getDescripcion());
            form.setPrecio(producto.getPrecio());
            form.setMoneda(producto.getMoneda());
            form.setIdMarca(producto.getMarca().getIdMarca());
            form.setIdCategoria(producto.getCategoria().getIdCategoria());
            form.setEstado(producto.getEstado());
            model.addAttribute("form", form);
        }
        model.addAttribute("atributosProducto", catalogoService.listarAtributos(id));
        opciones(model);
        return "admin/producto-form";
    }

    @PostMapping("/admin/productos")
    public String guardar(@Valid @ModelAttribute("form") ProductoForm form, BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            opciones(model);
            model.addAttribute("error", "Revisa los datos del producto.");
            return "admin/producto-form";
        }
        try {
            var guardado = catalogoAdminService.guardar(form);
            ra.addFlashAttribute("ok", "Producto guardado: " + guardado.getSku());
            return "redirect:/admin/productos/" + guardado.getIdProducto();
        } catch (RuntimeException ex) {
            opciones(model);
            model.addAttribute("error", ex.getMessage());
            return "admin/producto-form";
        }
    }

    @PostMapping("/admin/productos/{id}/estado")
    public String estado(@PathVariable Integer id, @RequestParam String estado, RedirectAttributes ra) {
        try {
            catalogoAdminService.cambiarEstado(id, estado);
            ra.addFlashAttribute("ok", "Estado actualizado.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/productos";
    }

    @GetMapping("/admin/categorias")
    public String categorias(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAllByOrderByNombreAsc());
        model.addAttribute("marcas", marcaRepository.findAllByOrderByNombreAsc());
        model.addAttribute("atributos", atributoTecnicoRepository.findAllByOrderByNombreAsc());
        return "admin/categorias";
    }

    @PostMapping("/admin/categorias")
    public String crearCategoria(@RequestParam String nombre, @RequestParam(required = false) String descripcion, RedirectAttributes ra) {
        return guardarSimple(ra, "/admin/categorias", () -> { catalogoAdminService.guardarCategoria(nombre, descripcion); });
    }

    @PostMapping("/admin/categorias/{id}/estado")
    public String estadoCategoria(@PathVariable Integer id, @RequestParam String estado, RedirectAttributes ra) {
        return guardarSimple(ra, "/admin/categorias", () -> { catalogoAdminService.cambiarEstadoCategoria(id, estado); });
    }

    @PostMapping("/admin/marcas")
    public String crearMarca(@RequestParam String nombre, RedirectAttributes ra) {
        return guardarSimple(ra, "/admin/categorias", () -> { catalogoAdminService.guardarMarca(nombre); });
    }

    @PostMapping("/admin/atributos")
    public String crearAtributo(@RequestParam String nombre, @RequestParam String tipo, @RequestParam(required = false) String unidad, RedirectAttributes ra) {
        return guardarSimple(ra, "/admin/categorias", () -> { catalogoAdminService.guardarAtributo(nombre, tipo, unidad); });
    }

    @PostMapping("/admin/productos/{id}/atributos")
    public String asignar(@PathVariable Integer id, @RequestParam Integer idAtributo, @RequestParam String valor, RedirectAttributes ra) {
        try {
            catalogoAdminService.asignarAtributo(id, idAtributo, valor);
            ra.addFlashAttribute("ok", "Característica guardada.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", mensaje(ex));
        }
        return "redirect:/admin/productos/" + id;
    }

    @GetMapping("/admin/inventario")
    public String inventario(Model model) {
        model.addAttribute("inventario", inventarioService.listarInventario());
        model.addAttribute("movimientos", inventarioService.listarMovimientos());
        model.addAttribute("bajos", inventarioRepositoryBajos());
        model.addAttribute("productos", catalogoAdminService.listar());
        return "admin/inventario";
    }

    @PostMapping("/admin/inventario/entrada")
    public String entrada(IngresoDto dto, RedirectAttributes ra) {
        return movimiento(ra, dto, true);
    }

    @PostMapping("/admin/inventario/salida")
    public String salida(IngresoDto dto, RedirectAttributes ra) {
        return movimiento(ra, dto, false);
    }

    private String movimiento(RedirectAttributes ra, IngresoDto dto, boolean entrada) {
        try {
            if (dto.idProducto() == null || dto.cantidad() == null || dto.cantidad() < 1) {
                throw new IllegalArgumentException("Indica el producto y una cantidad mayor que cero");
            }
            var producto = catalogoAdminService.listar().stream().filter(p -> p.getIdProducto().equals(dto.idProducto())).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            String motivo = dto.motivo() == null || dto.motivo().isBlank() ? (entrada ? "Entrada autorizada" : "Salida autorizada") : dto.motivo();
            String referencia = dto.referencia() == null || dto.referencia().isBlank() ? (entrada ? "ENT-" : "SAL-") + System.currentTimeMillis() : dto.referencia();
            if (entrada) {
                inventarioService.registrarEntrada(producto, dto.cantidad(), motivo, referencia, SesionActual.correo());
            } else {
                inventarioService.registrarSalida(producto, dto.cantidad(), motivo, referencia, SesionActual.correo());
            }
            ra.addFlashAttribute("ok", (entrada ? "Entrada" : "Salida") + " registrada.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", mensaje(ex));
        }
        return "redirect:/admin/inventario";
    }

    private java.util.List<?> inventarioRepositoryBajos() {
        return inventarioService.listarInventario().stream()
                .filter(i -> i.stockActual() != null && i.stockMinimo() != null && i.stockActual() <= i.stockMinimo())
                .toList();
    }

    private void opciones(Model model) {
        model.addAttribute("marcas", marcaRepository.findAllByOrderByNombreAsc());
        model.addAttribute("categorias", categoriaRepository.findAllByOrderByNombreAsc());
        model.addAttribute("atributos", atributoTecnicoRepository.findAllByOrderByNombreAsc());
    }

    private String guardarSimple(RedirectAttributes ra, String destino, Runnable accion) {
        try {
            accion.run();
            ra.addFlashAttribute("ok", "Cambio guardado.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", mensaje(ex));
        }
        return "redirect:" + destino;
    }

    private String mensaje(RuntimeException ex) {
        String texto = ex.getMessage();
        if (texto == null || texto.contains("Constraint") || texto.contains("SQL")) {
            return "No se pudo guardar por una restricción de los datos.";
        }
        return texto;
    }
}
