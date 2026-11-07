package com.technexo.chat.service;

import com.technexo.catalogo.dto.ProductoDto;
import com.technexo.catalogo.entity.AtributoTecnico;
import com.technexo.catalogo.entity.Categoria;
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
import com.technexo.chat.consulta.InterpreteOperacion;
import com.technexo.chat.consulta.TextoUtil;
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
import com.technexo.pedidos.dto.PedidoDto;
import com.technexo.pedidos.service.PedidoService;
import com.technexo.reservas.dto.ReservaCrearDto;
import com.technexo.reservas.dto.ReservaDto;
import com.technexo.reservas.service.ReservaService;
import com.technexo.seguridad.AccesoDenegadoException;
import com.technexo.seguridad.SesionActual;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);
    private static final String PENDIENTE = "tnReservaPendiente";

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final ProductoRepository productoRepository;
    private final ProductoAtributoRepository productoAtributoRepository;
    private final DisponibilidadProductoRepository disponibilidadRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final AtributoTecnicoRepository atributoTecnicoRepository;
    private final ReservaService reservaService;
    private final PedidoService pedidoService;

    public ChatService(ConversacionRepository conversacionRepository,
                       MensajeRepository mensajeRepository,
                       ProductoRepository productoRepository,
                       ProductoAtributoRepository productoAtributoRepository,
                       DisponibilidadProductoRepository disponibilidadRepository,
                       CategoriaRepository categoriaRepository,
                       MarcaRepository marcaRepository,
                       AtributoTecnicoRepository atributoTecnicoRepository,
                       ReservaService reservaService,
                       PedidoService pedidoService) {
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.productoRepository = productoRepository;
        this.productoAtributoRepository = productoAtributoRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.categoriaRepository = categoriaRepository;
        this.marcaRepository = marcaRepository;
        this.atributoTecnicoRepository = atributoTecnicoRepository;
        this.reservaService = reservaService;
        this.pedidoService = pedidoService;
    }

    @Transactional
    public ChatRespuesta responder(ChatPeticion peticion) {
        try {
            return responderInterno(peticion);
        } catch (AccesoDenegadoException | IllegalArgumentException | IllegalStateException ex) {
            return guardarFallo(peticion, ex.getMessage());
        } catch (RuntimeException ex) {
            LOG.error("El asistente no pudo completar la consulta", ex);
            return guardarFallo(peticion, "No pude consultar el catálogo en este momento. La tienda sigue disponible en las demás páginas.");
        }
    }

    private ChatRespuesta responderInterno(ChatPeticion peticion) {
        CatalogoReferencia referencia = referencia();
        InterpreteOperacion.Decision decision = InterpreteOperacion.decidir(peticion.mensaje(), referencia);
        List<ProductoDto> productos = List.of();
        String respuesta;
        boolean ambigua = decision.operacion() == InterpreteOperacion.Operacion.ACLARAR;
        switch (decision.operacion()) {
            case RECHAZAR, ACLARAR -> respuesta = decision.mensaje();
            case SALUDO -> respuesta = "Hola. Puedo consultar el catálogo real de TechNexo: precios, fichas, existencias y, si iniciaste sesión, tus reservas y pedidos.";
            case INFO -> respuesta = "TechNexo es la tienda de equipos de red y telecomunicaciones de este sistema. Catálogo, inventario, reservas y pedidos salen de la base TechNexoDB. No invento productos ni precios.";
            case COMPRA -> respuesta = "Para comprar abre el catálogo, entra a la ficha y usa Checkout. El pedido solo se registra si hay existencia, y el precio de cada línea queda guardado. Si quieres apartar una unidad, pide la reserva y confírmala. Desde el chat no creo pedidos.";
            case COMPARAR -> respuesta = comparar(peticion.mensaje());
            case CATEGORIAS -> respuesta = categorias();
            case RECOMENDAR -> {
                productos = recomendar();
                respuesta = redactarRecomendacion(productos);
            }
            case RESERVAS -> respuesta = reservasPropias();
            case PEDIDOS -> respuesta = pedidosPropios();
            case PREPARAR_RESERVA -> {
                ResultadoConsulta consulta = consultar(decision.busqueda(), referencia, false);
                productos = consulta.productos();
                respuesta = prepararReserva(peticion.mensaje(), productos);
                ambigua = productos.size() != 1;
            }
            case CONFIRMAR_RESERVA -> respuesta = confirmarReserva(peticion.mensaje());
            case CARACTERISTICAS -> {
                ResultadoConsulta consulta = consultar(decision.busqueda(), referencia, false);
                productos = consulta.productos();
                respuesta = consulta.ambigua() ? consulta.texto() : caracteristicas(productos);
                ambigua = consulta.ambigua() || productos.size() > 3;
            }
            case PRECIO, EXISTENCIAS, BUSCAR -> {
                boolean forzarStockVisible = decision.operacion() == InterpreteOperacion.Operacion.EXISTENCIAS;
                ResultadoConsulta consulta = consultar(decision.busqueda(), referencia, forzarStockVisible);
                productos = consulta.productos();
                respuesta = consulta.ambigua() ? consulta.texto() : redactar(productos, decision.busqueda());
                ambigua = consulta.ambigua();
            }
            default -> throw new IllegalStateException("Operación no contemplada: " + decision.operacion());
        }
        Conversacion conversacion = abrir(peticion);
        guardar(conversacion.getIdConversacion(), "USUARIO", peticion.mensaje().trim(), null);
        guardar(conversacion.getIdConversacion(), "ASISTENTE", respuesta, referencias(productos));
        return new ChatRespuesta(conversacion.getIdConversacion(), respuesta, ambigua, productos);
    }

    private ChatRespuesta guardarFallo(ChatPeticion peticion, String respuesta) {
        Conversacion conversacion = abrir(peticion);
        guardar(conversacion.getIdConversacion(), "USUARIO", peticion.mensaje().trim(), null);
        guardar(conversacion.getIdConversacion(), "ASISTENTE", respuesta, null);
        return new ChatRespuesta(conversacion.getIdConversacion(), respuesta, false, List.of());
    }

    @Transactional(readOnly = true)
    public ConversacionDto historial(Long idConversacion, String sesion) {
        Conversacion conversacion = conversacionRepository.findById(idConversacion)
                .orElseThrow(() -> new IllegalArgumentException("Conversación no encontrada"));
        if (!puedeVer(conversacion, sesion)) {
            throw new AccesoDenegadoException("No puede consultar esta conversación");
        }
        List<MensajeDto> mensajes = mensajeRepository.findByIdConversacionOrderByFechaAscIdMensajeAsc(idConversacion)
                .stream()
                .map(m -> new MensajeDto(m.getIdMensaje(), m.getRol(), m.getContenido(), m.getFecha()))
                .toList();
        return new ConversacionDto(conversacion.getIdConversacion(), conversacion.getEstado(), mensajes);
    }

    private boolean puedeVer(Conversacion conversacion, String sesion) {
        if (SesionActual.esAdmin()) {
            return true;
        }
        Integer idUsuario = SesionActual.idUsuario();
        if (idUsuario != null && idUsuario.equals(conversacion.getIdUsuario())) {
            return true;
        }
        return sesion != null && sesion.length() >= 20 && sesion.equals(conversacion.getIdentificadorSesion());
    }

    private ResultadoConsulta consultar(Interpretacion interpretacion, CatalogoReferencia referencia, boolean mostrarAgotados) {
        if (interpretacion == null || interpretacion.ambigua()) {
            String texto = interpretacion == null ? "Necesito el nombre o el SKU del producto." : interpretacion.aclaracion();
            return new ResultadoConsulta(true, texto, List.of());
        }
        ValidadorFiltros.validar(interpretacion.filtros(), referencia);
        ConsultaFiltros filtros = interpretacion.filtros();
        if (mostrarAgotados && filtros.soloDisponibles()) {
            filtros = new ConsultaFiltros(filtros.texto(), filtros.categoria(), filtros.marca(), filtros.precioMin(), filtros.precioMax(), false, filtros.atributos());
        }
        return new ResultadoConsulta(false, null, buscar(filtros));
    }

    private String categorias() {
        List<Categoria> categorias = categoriaRepository.findAllByOrderByNombreAsc();
        if (categorias.isEmpty()) {
            return "No hay categorías registradas.";
        }
        StringBuilder texto = new StringBuilder("Categorías registradas:");
        for (Categoria categoria : categorias) {
            texto.append("\n- ").append(categoria.getNombre()).append(" (").append(categoria.getEstado()).append(')');
        }
        return texto.toString();
    }

    private List<ProductoDto> recomendar() {
        ConsultaFiltros filtros = new ConsultaFiltros(null, null, null, null, null, true, List.of());
        List<ProductoDto> disponibles = new ArrayList<>();
        for (ProductoDto producto : buscar(filtros)) {
            if (producto.stockDisponible() != null && producto.stockDisponible() > 0) {
                disponibles.add(producto);
            }
            if (disponibles.size() == 4) {
                break;
            }
        }
        return disponibles;
    }

    private String redactarRecomendacion(List<ProductoDto> productos) {
        if (productos.isEmpty()) {
            return "No hay productos activos con existencia disponible para recomendar.";
        }
        return "Estas opciones salen del catálogo real y tienen existencia disponible. No armé un paquete que no esté registrado.\n"
                + redactar(productos, null);
    }

    private static final Pattern SKU = Pattern.compile("\\b[a-z]{2,}(?:-[a-z0-9]+)+\\b");

    private String comparar(String mensaje) {
        Matcher coincidencia = SKU.matcher(TextoUtil.normalizar(mensaje));
        ArrayList<String> skus = new ArrayList<>();
        while (coincidencia.find() && skus.size() < 2) {
            skus.add(coincidencia.group().toUpperCase(Locale.ROOT));
        }
        if (skus.size() < 2) {
            return "Para comparar necesito dos SKU del catálogo. Por ejemplo: compara TNX-SW-024 y TNX-RT-006.";
        }
        StringBuilder texto = new StringBuilder("Comparación con datos registrados:");
        for (String sku : skus) {
            ConsultaFiltros filtros = new ConsultaFiltros(sku, null, null, null, null, false, List.of());
            List<ProductoDto> encontrados = buscar(filtros);
            ProductoDto producto = encontrados.stream()
                    .filter(p -> sku.equalsIgnoreCase(p.sku()))
                    .findFirst()
                    .orElse(null);
            if (producto == null) {
                texto.append("\n").append(sku).append(": no está en el catálogo activo.");
                continue;
            }
            texto.append("\n").append(producto.sku()).append(" — ").append(producto.nombre())
                    .append(" — ").append(producto.precio()).append(' ').append(producto.moneda())
                    .append(" — disponible: ").append(producto.stockDisponible());
        }
        return texto.toString();
    }

    private String reservasPropias() {
        if (!SesionActual.autenticado()) {
            return "Inicia sesión para consultar tus reservas. No puedo mostrar reservas de otras cuentas.";
        }
        List<ReservaDto> reservas = reservaService.listarPropias();
        if (reservas.isEmpty()) {
            return "No tienes reservas registradas en esta cuenta.";
        }
        StringBuilder texto = new StringBuilder("Tus reservas:");
        for (ReservaDto reserva : reservas) {
            texto.append("\n#").append(reserva.id()).append(' ').append(reserva.sku()).append(" × ")
                    .append(reserva.cantidad()).append(" — ").append(reserva.estado());
        }
        return texto.toString();
    }

    private String pedidosPropios() {
        if (!SesionActual.autenticado()) {
            return "Inicia sesión y abre Mis pedidos para ver el estado. No puedo mostrar pedidos de otras cuentas.";
        }
        List<PedidoDto> pedidos = pedidoService.listarPropios();
        if (pedidos.isEmpty()) {
            return "No tienes pedidos registrados en esta cuenta.";
        }
        StringBuilder texto = new StringBuilder("Tus pedidos:");
        for (PedidoDto pedido : pedidos) {
            texto.append("\n#").append(pedido.id()).append(" — ").append(pedido.estado())
                    .append(" — ").append(pedido.total()).append(' ').append(pedido.moneda());
        }
        return texto.toString();
    }

    private String prepararReserva(String mensaje, List<ProductoDto> productos) {
        if (!SesionActual.autenticado()) {
            return "Para reservar necesitas iniciar sesión. No aparto inventario de forma anónima.";
        }
        if (productos.isEmpty()) {
            return "No encontré un producto activo con ese nombre o SKU, así que no preparé ninguna reserva.";
        }
        if (productos.size() > 1) {
            return "Hay varios productos. Indica el SKU exacto para preparar una sola reserva.\n" + redactar(productos, null);
        }
        ProductoDto producto = productos.get(0);
        if (producto.stockDisponible() == null || producto.stockDisponible() < 1) {
            return producto.sku() + " no tiene unidades disponibles. No preparé la reserva.";
        }
        int cantidad = 1;
        HttpSession sesion = sesion(true);
        long vence = System.currentTimeMillis() + 10 * 60 * 1000L;
        sesion.setAttribute(PENDIENTE, producto.id() + "|" + cantidad + "|" + producto.sku() + "|" + vence);
        return "Puedo reservar 1 unidad de " + producto.sku() + " — " + producto.nombre()
                + " a " + producto.precio() + " " + producto.moneda()
                + ". Disponible: " + producto.stockDisponible()
                + ". Para crearla responde exactamente: confirmo " + producto.sku();
    }

    private String confirmarReserva(String mensaje) {
        if (!SesionActual.autenticado()) {
            return "Inicia sesión antes de confirmar una reserva.";
        }
        HttpSession sesion = sesion(false);
        if (sesion == null || sesion.getAttribute(PENDIENTE) == null) {
            return "No hay una reserva pendiente de confirmación. Primero indica qué producto quieres reservar.";
        }
        String[] partes = String.valueOf(sesion.getAttribute(PENDIENTE)).split("\\|");
        if (partes.length != 4) {
            sesion.removeAttribute(PENDIENTE);
            return "La confirmación pendiente no es válida. Vuelve a solicitar la reserva.";
        }
        if (System.currentTimeMillis() > Long.parseLong(partes[3])) {
            sesion.removeAttribute(PENDIENTE);
            return "La confirmación venció. Vuelve a pedir la reserva.";
        }
        String sku = partes[2];
        if (!mensaje.toUpperCase().contains(sku)) {
            return "Para crear la reserva responde: confirmo " + sku;
        }
        ReservaCrearDto dto = new ReservaCrearDto();
        dto.setIdProducto(Integer.valueOf(partes[0]));
        dto.setCantidad(Integer.valueOf(partes[1]));
        var reserva = reservaService.crearReserva(dto);
        sesion.removeAttribute(PENDIENTE);
        return "Reserva #" + reserva.getIdReserva() + " creada para " + sku
                + " en estado " + reserva.getEstado() + ". Expira en 30 minutos.";
    }

    private String caracteristicas(List<ProductoDto> productos) {
        if (productos.isEmpty()) {
            return "No hay productos activos que cumplan esa consulta, así que no hay características que mostrar.";
        }
        if (productos.size() > 3) {
            return "Hay varias coincidencias. Indica el SKU para ver una sola ficha.\n" + redactar(productos, null);
        }
        Map<Integer, List<AtributoValor>> atributos = agruparAtributos();
        StringBuilder texto = new StringBuilder();
        for (ProductoDto producto : productos) {
            texto.append(producto.sku()).append(" — ").append(producto.nombre()).append('\n');
            List<AtributoValor> valores = atributos.getOrDefault(producto.id(), List.of());
            if (valores.isEmpty()) {
                texto.append("Sin características técnicas registradas.\n");
            }
            for (AtributoValor valor : valores) {
                texto.append("- ").append(valor.nombre()).append(": ").append(mostrar(valor)).append('\n');
            }
        }
        return texto.toString().trim();
    }

    private String mostrar(AtributoValor valor) {
        if (valor.texto() != null) {
            return valor.texto();
        }
        if (valor.numero() != null) {
            return valor.numero().stripTrailingZeros().toPlainString();
        }
        if (valor.booleano() != null) {
            return Boolean.TRUE.equals(valor.booleano()) ? "sí" : "no";
        }
        return "sin valor";
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
        List<String> categorias = categoriaRepository.findAllByOrderByNombreAsc().stream().map(Categoria::getNombre).toList();
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
            if (!puedeVer(existente, peticion.identificadorSesion())) {
                throw new AccesoDenegadoException("No puede continuar esta conversación");
            }
            if (!"ABIERTA".equals(existente.getEstado())) {
                throw new IllegalStateException("La conversación ya no está abierta");
            }
            return existente;
        }
        Conversacion nueva = new Conversacion();
        nueva.setFechaInicio(FechaUtil.ahoraUtc());
        nueva.setEstado("ABIERTA");
        nueva.setCanal("WEB");
        String sesion = peticion.identificadorSesion();
        nueva.setIdentificadorSesion(sesion != null && sesion.length() >= 20 ? sesion : null);
        nueva.setIdUsuario(SesionActual.idUsuario());
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

    private String redactar(List<ProductoDto> productos, Interpretacion interpretacion) {
        if (productos.isEmpty()) {
            String texto = interpretacion != null && interpretacion.filtros() != null ? interpretacion.filtros().texto() : null;
            if ("computadora".equals(texto)) {
                return "No hay computadoras activas en el catálogo. Puedo consultar switches, routers, ONT o cámaras que sí estén registrados.";
            }
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

    private HttpSession sesion(boolean crear) {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes atributos)) {
            return null;
        }
        return atributos.getRequest().getSession(crear);
    }

    @Transactional
    public void nuevaConversacion(Long idConversacion, String sesion) {
        if (idConversacion == null) {
            return;
        }
        conversacionRepository.findById(idConversacion).ifPresent(conversacion -> {
            if (puedeVer(conversacion, sesion) && "ABIERTA".equals(conversacion.getEstado())) {
                conversacion.setEstado("CERRADA");
                conversacion.setFechaFin(FechaUtil.ahoraUtc());
                conversacionRepository.save(conversacion);
            }
        });
    }

    private record ResultadoConsulta(boolean ambigua, String texto, List<ProductoDto> productos) {
    }
}
