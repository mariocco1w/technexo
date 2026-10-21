(function () {
    var toggle = document.getElementById("tn-chat-toggle");
    var panel = document.getElementById("tn-chat-panel");
    var close = document.getElementById("tn-chat-close");
    var form = document.getElementById("tn-chat-form");
    var input = document.getElementById("tn-chat-input");
    var log = document.getElementById("tn-chat-log");
    if (!toggle || !panel || !form || !input || !log) {
        return;
    }

    var conversacion = sessionStorage.getItem("tnConversacion");
    var sesion = sessionStorage.getItem("tnSesion");
    if (!sesion) {
        sesion = "web-" + Math.random().toString(16).slice(2);
        sessionStorage.setItem("tnSesion", sesion);
    }

    toggle.addEventListener("click", function () {
        panel.hidden = !panel.hidden;
        if (!panel.hidden) {
            input.focus();
        }
    });
    if (close) {
        close.addEventListener("click", function () {
            panel.hidden = true;
        });
    }

    form.addEventListener("submit", function (evento) {
        evento.preventDefault();
        var texto = input.value.trim();
        if (!texto) {
            return;
        }
        agregar("usuario", texto, []);
        input.value = "";
        fetch("/api/chat", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                idConversacion: conversacion ? Number(conversacion) : null,
                mensaje: texto,
                identificadorSesion: sesion
            })
        }).then(function (respuesta) {
            return respuesta.json().then(function (cuerpo) {
                if (!respuesta.ok) {
                    throw new Error(cuerpo.error || "No se pudo consultar el catálogo");
                }
                return cuerpo;
            });
        }).then(function (cuerpo) {
            conversacion = String(cuerpo.idConversacion);
            sessionStorage.setItem("tnConversacion", conversacion);
            agregar("asistente", cuerpo.respuesta, cuerpo.productos || []);
        }).catch(function (error) {
            agregar("asistente", error.message, []);
        });
    });

    function agregar(rol, texto, productos) {
        var bloque = document.createElement("div");
        bloque.className = "tn-chat-msg " + rol;
        bloque.appendChild(document.createTextNode(texto));
        productos.forEach(function (producto) {
            var enlace = document.createElement("a");
            enlace.href = "/producto/" + producto.id;
            enlace.textContent = producto.sku + " · " + producto.precio + " " + producto.moneda + " · disp. " + producto.stockDisponible;
            bloque.appendChild(enlace);
        });
        log.appendChild(bloque);
        log.scrollTop = log.scrollHeight;
    }
})();
