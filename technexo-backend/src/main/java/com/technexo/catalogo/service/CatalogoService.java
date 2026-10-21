package com.technexo.catalogo.service;

import com.technexo.catalogo.dto.AtributoDto;
import com.technexo.catalogo.dto.CategoriaDto;
import com.technexo.catalogo.dto.ProductoDto;
import com.technexo.catalogo.entity.Categoria;
import com.technexo.catalogo.entity.DisponibilidadProducto;
import com.technexo.catalogo.entity.Producto;
import com.technexo.catalogo.entity.ProductoAtributo;
import com.technexo.catalogo.repository.CategoriaRepository;
import com.technexo.catalogo.repository.DisponibilidadProductoRepository;
import com.technexo.catalogo.repository.ProductoAtributoRepository;
import com.technexo.catalogo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CatalogoService {
    private final ProductoRepository productoRepository;
    private final ProductoAtributoRepository productoAtributoRepository;
    private final DisponibilidadProductoRepository disponibilidadRepository;
    private final CategoriaRepository categoriaRepository;

    private Map<Integer, DisponibilidadProducto> mapaDisponibilidad() {
        return disponibilidadRepository.findAll().stream().collect(Collectors.toMap(DisponibilidadProducto::getIdProducto, Function.identity()));
    }

    public List<ProductoDto> listarProductos() {
        Map<Integer, DisponibilidadProducto> disp = mapaDisponibilidad();
        return productoRepository.findActivosConRelaciones().stream().map(p -> aDto(p, disp.get(p.getIdProducto()))).toList();
    }

    public List<ProductoDto> buscar(String q) {
        Map<Integer, DisponibilidadProducto> disp = mapaDisponibilidad();
        return productoRepository.buscarPorTexto(q).stream().map(p -> aDto(p, disp.get(p.getIdProducto()))).toList();
    }

    public ProductoDto obtenerProducto(Integer idProducto) {
        Producto p = productoRepository.findById(idProducto).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + idProducto));
        DisponibilidadProducto d = disponibilidadRepository.findById(idProducto).orElse(null);
        return aDto(p, d);
    }

    public List<AtributoDto> listarAtributos(Integer idProducto) {
        return productoAtributoRepository.findByProductoIdConAtributo(idProducto).stream().map(this::aAtributoDto).toList();
    }

    public List<CategoriaDto> listarCategorias() {
        return categoriaRepository.findAllByOrderByNombreAsc().stream().map(this::aCategoriaDto).toList();
    }

    private ProductoDto aDto(Producto p, DisponibilidadProducto d) {
        return new ProductoDto(p.getIdProducto(), p.getSku(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getMoneda(), p.getEstado(), p.getMarca() != null ? p.getMarca().getNombre() : null, p.getCategoria() != null ? p.getCategoria().getNombre() : null, d != null ? d.getStockFisico() : 0, d != null ? d.getCantidadReservada() : 0, d != null ? d.getStockDisponible() : 0);
    }

    private AtributoDto aAtributoDto(ProductoAtributo pa) {
        String valor = "";
        String tipo = pa.getAtributoTecnico() != null ? pa.getAtributoTecnico().getTipoDato() : "";
        if (pa.getValorTexto() != null) valor = pa.getValorTexto();
         else if (pa.getValorNumero() != null) valor = pa.getValorNumero().stripTrailingZeros().toPlainString();
         else if (pa.getValorBooleano() != null) valor = Boolean.TRUE.equals(pa.getValorBooleano()) ? "Sí" : "No";
         else if (pa.getValorFecha() != null) valor = pa.getValorFecha().toString();
        return new AtributoDto(pa.getAtributoTecnico() != null ? pa.getAtributoTecnico().getNombre() : null, pa.getAtributoTecnico() != null ? pa.getAtributoTecnico().getUnidad() : null, tipo, valor);
    }

    private CategoriaDto aCategoriaDto(Categoria c) {
        return new CategoriaDto(c.getIdCategoria(), c.getNombre(), c.getCategoriaPadre() != null ? c.getCategoriaPadre().getIdCategoria() : null, c.getEstado());
    }

    public CatalogoService(final ProductoRepository productoRepository, final ProductoAtributoRepository productoAtributoRepository, final DisponibilidadProductoRepository disponibilidadRepository, final CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.productoAtributoRepository = productoAtributoRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.categoriaRepository = categoriaRepository;
    }
}
