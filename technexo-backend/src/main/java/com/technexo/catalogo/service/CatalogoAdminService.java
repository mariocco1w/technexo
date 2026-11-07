package com.technexo.catalogo.service;

import com.technexo.catalogo.dto.ProductoForm;
import com.technexo.catalogo.entity.AtributoTecnico;
import com.technexo.catalogo.entity.Categoria;
import com.technexo.catalogo.entity.Marca;
import com.technexo.catalogo.entity.Producto;
import com.technexo.catalogo.entity.ProductoAtributo;
import com.technexo.catalogo.repository.AtributoTecnicoRepository;
import com.technexo.catalogo.repository.CategoriaRepository;
import com.technexo.catalogo.repository.MarcaRepository;
import com.technexo.catalogo.repository.ProductoAtributoRepository;
import com.technexo.catalogo.repository.ProductoRepository;
import com.technexo.common.tiempo.FechaUtil;
import com.technexo.inventario.entity.Inventario;
import com.technexo.inventario.repository.InventarioRepository;
import com.technexo.inventario.service.InventarioService;
import com.technexo.seguridad.AuditoriaService;
import com.technexo.seguridad.SesionActual;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class CatalogoAdminService {

    private final ProductoRepository productoRepository;
    private final MarcaRepository marcaRepository;
    private final CategoriaRepository categoriaRepository;
    private final AtributoTecnicoRepository atributoTecnicoRepository;
    private final ProductoAtributoRepository productoAtributoRepository;
    private final InventarioRepository inventarioRepository;
    private final InventarioService inventarioService;
    private final AuditoriaService auditoriaService;

    public CatalogoAdminService(ProductoRepository productoRepository, MarcaRepository marcaRepository,
                                CategoriaRepository categoriaRepository, AtributoTecnicoRepository atributoTecnicoRepository,
                                ProductoAtributoRepository productoAtributoRepository, InventarioRepository inventarioRepository,
                                InventarioService inventarioService, AuditoriaService auditoriaService) {
        this.productoRepository = productoRepository;
        this.marcaRepository = marcaRepository;
        this.categoriaRepository = categoriaRepository;
        this.atributoTecnicoRepository = atributoTecnicoRepository;
        this.productoAtributoRepository = productoAtributoRepository;
        this.inventarioRepository = inventarioRepository;
        this.inventarioService = inventarioService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.findTodosConRelaciones();
    }

    public Producto guardar(ProductoForm form) {
        validar(form);
        boolean nuevo = form.getId() == null;
        Producto producto = nuevo
                ? new Producto()
                : productoRepository.findById(form.getId()).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        Integer idActual = producto.getIdProducto();
        productoRepository.findBySku(form.getSku().trim()).ifPresent(existente -> {
            if (idActual == null || !existente.getIdProducto().equals(idActual)) {
                throw new IllegalArgumentException("El SKU ya está registrado");
            }
        });
        Marca marca = marcaRepository.findById(form.getIdMarca()).orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));
        Categoria categoria = categoriaRepository.findById(form.getIdCategoria()).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        if (nuevo) {
            producto.setFechaCreacion(FechaUtil.ahoraUtc());
            producto.setEstado("ACTIVO");
        } else if (form.getEstado() != null) {
            if (!List.of("ACTIVO", "INACTIVO", "DESCATALOGADO").contains(form.getEstado())) {
                throw new IllegalArgumentException("Estado de producto no permitido");
            }
            producto.setEstado(form.getEstado());
        }
        producto.setMarca(marca);
        producto.setCategoria(categoria);
        producto.setSku(form.getSku().trim());
        producto.setNombre(form.getNombre().trim());
        producto.setDescripcion(form.getDescripcion());
        producto.setPrecio(form.getPrecio());
        producto.setMoneda(form.getMoneda());
        producto.setFechaActualizacion(FechaUtil.ahoraUtc());
        Producto guardado = productoRepository.save(producto);
        if (nuevo) {
            int minimo = form.getStockMinimo() == null ? 0 : form.getStockMinimo();
            int inicial = form.getStockInicial() == null ? 0 : form.getStockInicial();
            if (minimo < 0 || inicial < 0) {
                throw new IllegalArgumentException("El inventario no puede ser negativo");
            }
            Inventario inventario = new Inventario();
            inventario.setIdProducto(guardado.getIdProducto());
            inventario.setStockActual(0);
            inventario.setStockMinimo(minimo);
            inventario.setUbicacion(form.getUbicacion());
            inventario.setFechaActualizacion(FechaUtil.ahoraUtc());
            inventarioRepository.saveAndFlush(inventario);
            if (inicial > 0) {
                inventarioService.registrarEntrada(guardado, inicial, "Inventario inicial", "ALTA-" + guardado.getSku(), SesionActual.correo());
            }
        }
        auditoriaService.registrar(nuevo ? "CREAR_PRODUCTO" : "EDITAR_PRODUCTO", "Producto", String.valueOf(guardado.getIdProducto()), guardado.getSku());
        return guardado;
    }

    public void cambiarEstado(Integer id, String estado) {
        if (!List.of("ACTIVO", "INACTIVO").contains(estado)) {
            throw new IllegalArgumentException("Solo se puede activar o desactivar");
        }
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        producto.setEstado(estado);
        producto.setFechaActualizacion(FechaUtil.ahoraUtc());
        productoRepository.save(producto);
        auditoriaService.registrar("CAMBIAR_ESTADO_PRODUCTO", "Producto", String.valueOf(id), estado);
    }

    public Categoria guardarCategoria(String nombre, String descripcion) {
        if (nombre == null || nombre.isBlank() || nombre.length() > 120) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre.trim());
        categoria.setDescripcion(descripcion);
        categoria.setEstado("ACTIVA");
        Categoria guardada = categoriaRepository.save(categoria);
        auditoriaService.registrar("CREAR_CATEGORIA", "Categoria", String.valueOf(guardada.getIdCategoria()), guardada.getNombre());
        return guardada;
    }

    public void cambiarEstadoCategoria(Integer id, String estado) {
        if (!List.of("ACTIVA", "INACTIVA").contains(estado)) {
            throw new IllegalArgumentException("Estado de categoría no permitido");
        }
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        categoria.setEstado(estado);
        categoriaRepository.save(categoria);
        auditoriaService.registrar("CAMBIAR_ESTADO_CATEGORIA", "Categoria", String.valueOf(id), estado);
    }

    public Marca guardarMarca(String nombre) {
        if (nombre == null || nombre.isBlank() || nombre.length() > 120) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio");
        }
        Marca marca = new Marca();
        marca.setNombre(nombre.trim());
        marca.setEstado("ACTIVA");
        marca.setFechaCreacion(FechaUtil.ahoraUtc());
        Marca guardada = marcaRepository.save(marca);
        auditoriaService.registrar("CREAR_MARCA", "Marca", String.valueOf(guardada.getIdMarca()), guardada.getNombre());
        return guardada;
    }

    public AtributoTecnico guardarAtributo(String nombre, String tipo, String unidad) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El atributo necesita un nombre");
        }
        if (!List.of("TEXTO", "NUMERO", "BOOLEANO", "FECHA").contains(tipo)) {
            throw new IllegalArgumentException("Tipo de dato no permitido");
        }
        AtributoTecnico atributo = new AtributoTecnico();
        atributo.setNombre(nombre.trim());
        atributo.setTipoDato(tipo);
        atributo.setUnidad(unidad);
        AtributoTecnico guardado = atributoTecnicoRepository.save(atributo);
        auditoriaService.registrar("CREAR_ATRIBUTO", "AtributoTecnico", String.valueOf(guardado.getIdAtributo()), guardado.getNombre());
        return guardado;
    }

    public void asignarAtributo(Integer idProducto, Integer idAtributo, String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Indica el valor de la característica");
        }
        Producto producto = productoRepository.findById(idProducto).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        AtributoTecnico atributo = atributoTecnicoRepository.findById(idAtributo).orElseThrow(() -> new IllegalArgumentException("Atributo no encontrado"));
        ProductoAtributo fila = productoAtributoRepository.findById(new com.technexo.catalogo.entity.ProductoAtributoId(idProducto, idAtributo))
                .orElseGet(ProductoAtributo::new);
        fila.setIdProducto(producto.getIdProducto());
        fila.setIdAtributo(atributo.getIdAtributo());
        fila.setValorTexto(null);
        fila.setValorNumero(null);
        fila.setValorBooleano(null);
        fila.setValorFecha(null);
        switch (atributo.getTipoDato()) {
            case "TEXTO" -> fila.setValorTexto(valor.trim());
            case "NUMERO" -> fila.setValorNumero(new BigDecimal(valor.trim()));
            case "BOOLEANO" -> fila.setValorBooleano(valor.trim().equalsIgnoreCase("si") || valor.trim().equalsIgnoreCase("sí") || valor.trim().equalsIgnoreCase("true"));
            case "FECHA" -> fila.setValorFecha(LocalDate.parse(valor.trim()));
            default -> throw new IllegalArgumentException("Tipo de atributo no soportado");
        }
        productoAtributoRepository.save(fila);
        auditoriaService.registrar("ASIGNAR_ATRIBUTO", "Producto", String.valueOf(idProducto), atributo.getNombre());
    }

    private void validar(ProductoForm form) {
        if (form.getPrecio() == null || form.getPrecio().signum() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        String moneda = form.getMoneda() == null ? "" : form.getMoneda().toUpperCase(Locale.ROOT);
        if (!List.of("GTQ", "USD").contains(moneda)) {
            throw new IllegalArgumentException("La moneda debe ser GTQ o USD");
        }
        form.setMoneda(moneda);
    }
}
