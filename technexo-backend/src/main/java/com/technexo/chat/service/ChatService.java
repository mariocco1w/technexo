package com.technexo.chat.service;

import com.technexo.catalogo.dto.ProductoDto;
import com.technexo.catalogo.entity.AtributoTecnico;
import com.technexo.catalogo.entity.DisponibilidadProducto;
import com.technexo.catalogo.entity.Producto;
import com.technexo.catalogo.entity.ProductoAtributo;
import com.technexo.catalogo.repository.AtributoTecnicoRepository;
import com.technexo.catalogo.repository.CategoriaRepository;
import com.technexo.catalogo.repository.DisponibilidadProductoRepository;
import com.technexo.catalogo.repository.MarcaRepository;
import com.technexo.catalogo.repository.ProductoAtributoRepository;
import com.technexo.catalogo.repository.ProductoRepository;
import com.technexo.chat.consulta.AtributoValor;
import com.technexo.chat.consulta.FichaFiltro;
import com.technexo.chat.consulta.FiltroProductos;
import com.technexo.chat.consulta.InterpreteConsulta;
import com.technexo.chat.consulta.ValidadorFiltros;
import com.technexo.chat.dto.AtributoConocido;
import com.technexo.chat.dto.CatalogoReferencia;
import com.technexo.chat.dto.ChatPeticion;
import com.technexo.chat.dto.ChatRespuesta;
import com.technexo.chat.dto.ConsultaFiltros;
import com.technexo.chat.dto.ConversacionDto;
import com.technexo.chat.dto.Interpretacion;
import com.technexo.chat.dto.MensajeDto;
import com.technexo.chat.entity.Conversacion;
import com.technexo.chat.entity.Mensaje;
import com.technexo.chat.repository.ConversacionRepository;
import com.technexo.chat.repository.MensajeRepository;
import com.technexo.common.tiempo.FechaUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final ProductoRepository productoRepository;
    private final ProductoAtributoRepository productoAtributoRepository;
    private final DisponibilidadProductoRepository disponibilidadRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final AtributoTecnicoRepository atributoTecnicoRepository;

    public ChatService(ConversacionRepository conversacionRepository,
                       MensajeRepository mensajeRepository,
                       ProductoRepository productoRepository,
                       ProductoAtributoRepository productoAtributoRepository,
                       DisponibilidadProductoRepository disponibilidadRepository,
                       CategoriaRepository categoriaRepository,
                       MarcaRepository marcaRepository,
                       AtributoTecnicoRepository atributoTecnicoRepository) {
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.productoRepository = productoRepository;
        this.productoAtributoRepository = productoAtributoRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.categoriaRepository = categoriaRepository;
        this.marcaRepository = marcaRepository;
        this.atributoTecnicoRepository = atributoTecnicoRepository;
    }

    @Transactional
    public ChatRespuesta responder(ChatPeticion peticion) {
        CatalogoReferencia referencia = referencia();
        Interpretacion interpretacion = InterpreteConsulta.interpretar(peticion.mensaje(), referencia);
        List<ProductoDto> productos = List.of();
        String respuesta;
        boolean ambigua = interpretacion.ambigua();
        if (ambigua) {
            respuesta = interpretacion.aclaracion();
        } else {
            ValidadorFiltros.validar(interpretacion.filtros(), referencia);
            productos = buscar(interpretacion.filtros());
            respuesta = redactar(productos);
        }

        Conversacion conversacion = abrir(peticion);
        guardar(conversacion.getIdConversacion(), "USUARIO", peticion.mensaje().trim(), null);
        guardar(conversacion.getIdConversacion(), "ASISTENTE", respuesta, referencias(productos));
        return new ChatRespuesta(conversacion.getIdConversacion(), respuesta, ambigua, productos);
    }

    @Transactional(readOnly = true)
    public ConversacionDto historial(Long idConversacion) {
        Conversacion conversacion = conversacionRepository.findById(idConversacion)
                .orElseThrow(() -> new IllegalArgumentException("Conversación no encontrada"));
        List<MensajeDto> mensajes = mensajeRepository.findByIdConversacionOrderByFechaAscIdMensajeAsc(idConversacion)
                .stream()
                .map(m -> new MensajeDto(m.getIdMensaje(), m.getRol(), m.getContenido(), m.getFecha()))
                .toList();
        return new ConversacionDto(conversacion.getIdConversacion(), conversacion.getEstado(), mensajes);
    }

    private List<ProductoDto> buscar(ConsultaFiltros filtros) {
        Map<Integer, DisponibilidadProducto> disponibilidad = new LinkedHashMap<>();
        for (DisponibilidadProducto fila : disponibilidadRepository.findAll()) {
            disponibilidad.put(fila.getIdProducto(), fila);
        }
        Map<Integer, List<AtributoValor>> atributos = agruparAtributos();
        List<ProductoDto> resultado = new ArrayList<>();
        for (Producto producto : productoRepository.findActivosConRelaciones()) {
            DisponibilidadProducto disp = disponibilidad.get(producto.getIdProducto());
            int disponible = disp != null && disp.getStockDisponible() != null ? disp.getStockDisponible() : 0;
            FichaFiltro ficha = new FichaFiltro(
                    producto.getSku(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getMarca() != null ? producto.getMarca().getNombre() : null,
                    producto.getCategoria() != null ? producto.getCategoria().getNombre() : null,
                    producto.getPrecio(),
                    disponible,
                    atributos.getOrDefault(producto.getIdProducto(), List.of())
            );
            if (FiltroProductos.cumple(ficha, filtros)) {
                int fisico = disp != null && disp.getStockFisico() != null ? disp.getStockFisico() : 0;
                int reservado = disp != null && disp.getCantidadReservada() != null ? disp.getCantidadReservada() : 0;
                resultado.add(new ProductoDto(
                        producto.getIdProducto(),
                        producto.getSku(),
                        producto.getNombre(),
                        producto.getDescripcion(),
                        producto.getPrecio(),
                        producto.getMoneda(),
                        producto.getEstado(),
                        ficha.marca(),
                        ficha.categoria(),
                        fisico,
                        reservado,
                        disponible
                ));
            }
        }
        return resultado;
    }

    private Map<Integer, List<AtributoValor>> agruparAtributos() {
        Map<Integer, List<AtributoValor>> grupos = new LinkedHashMap<>();
        for (ProductoAtributo fila : productoAtributoRepository.findAllConAtributo()) {
            AtributoTecnico tecnico = fila.getAtributoTecnico();
            AtributoValor valor = new AtributoValor(
                    tecnico != null ? tecnico.getNombre() : null,
                    tecnico != null ? tecnico.getTipoDato() : null,
                    fila.getValorTexto(),
                    fila.getValorNumero(),
                    fila.getValorBooleano()
            );
            grupos.computeIfAbsent(fila.getIdProducto(), id -> new ArrayList<>()).add(valor);
        }
        return grupos;
    }

    private CatalogoReferencia referencia() {
        List<String> categorias = categoriaRepository.findAllByOrderByNombreAsc().stream().map(c -> c.getNombre()).toList();
        List<String> marcas = marcaRepository.findAllByOrderByNombreAsc().stream().map(m -> m.getNombre()).toList();
        List<AtributoConocido> atributos = atributoTecnicoRepository.findAllByOrderByNombreAsc().stream()
                .map(a -> new AtributoConocido(a.getNombre(), a.getTipoDato(), a.getUnidad()))
                .toList();
        return new CatalogoReferencia(categorias, marcas, atributos);
    }

    private Conversacion abrir(ChatPeticion peticion) {
        if (peticion.idConversacion() != null) {
            Conversacion existente = conversacionRepository.findById(peticion.idConversacion())
                    .orElseThrow(() -> new IllegalArgumentException("Conversación no encontrada"));
            if (!"ABIERTA".equals(existente.getEstado())) {
                throw new IllegalStateException("La conversación ya no está abierta");
            }
            return existente;
        }
        Conversacion nueva = new Conversacion();
        nueva.setFechaInicio(FechaUtil.ahoraUtc());
        nueva.setEstado("ABIERTA");
        nueva.setCanal("WEB");
        nueva.setIdentificadorSesion(peticion.identificadorSesion());
        return conversacionRepository.save(nueva);
    }

    private void guardar(Long idConversacion, String rol, String contenido, String referencias) {
        Mensaje mensaje = new Mensaje();
        mensaje.setIdConversacion(idConversacion);
        mensaje.setRol(rol);
        mensaje.setContenido(contenido);
        mensaje.setFecha(FechaUtil.ahoraUtc());
        mensaje.setReferenciasDatos(referencias);
        mensajeRepository.save(mensaje);
    }

    private String redactar(List<ProductoDto> productos) {
        if (productos.isEmpty()) {
            return "No hay productos activos que cumplan esas condiciones en el catálogo.";
        }
        StringBuilder texto = new StringBuilder();
        texto.append(productos.size() == 1
                ? "Encontré 1 producto en el catálogo."
                : "Encontré " + productos.size() + " productos en el catálogo.");
        for (ProductoDto producto : productos) {
            texto.append('\n')
                    .append(producto.sku())
                    .append(" — ")
                    .append(producto.nombre())
                    .append(" — ")
                    .append(producto.precio())
                    .append(' ')
                    .append(producto.moneda())
                    .append(" — disponible: ")
                    .append(producto.stockDisponible());
        }
        return texto.toString();
    }

    private String referencias(List<ProductoDto> productos) {
        if (productos.isEmpty()) {
            return null;
        }
        StringBuilder json = new StringBuilder("{\"productos\":[");
        for (int i = 0; i < productos.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(productos.get(i).id());
        }
        json.append("]}");
        return json.toString();
    }
}
