(function () {
    var toggle = document.getElementById("tn-chat-toggle");
    var panel = document.getElementById("tn-chat-panel");
    var close = document.getElementById("tn-chat-close");
    var nueva = document.getElementById("tn-chat-new");
    var form = document.getElementById("tn-chat-form");
    var input = document.getElementById("tn-chat-input");
    var log = document.getElementById("tn-chat-log");
    if (!toggle || !panel || !form || !input || !log) {
        return;
    }

    var conversacion = sessionStorage.getItem("tnConversacion");
    var sesion = sessionStorage.getItem("tnSesion");
    if (!sesion || sesion.length < 20) {
        sesion = (window.crypto && crypto.randomUUID) ? crypto.randomUUID() : ("web-" + Math.random().toString(16).slice(2) + Date.now());
        sessionStorage.setItem("tnSesion", sesion);
    }

    function csrf() {
        var coincidencia = document.cookie.match(/(?:^|; )XSRF-TOKEN=([^;]+)/);
        return coincidencia ? decodeURIComponent(coincidencia[1]) : "";
    }

    function mostrarPanel(abierto) {
        panel.hidden = !abierto;
        toggle.setAttribute("aria-expanded", abierto ? "true" : "false");
        if (abierto) {
            input.focus();
        }
    }

    toggle.addEventListener("click", function () {
        mostrarPanel(panel.hidden);
    });
    if (close) {
        close.addEventListener("click", function () {
            mostrarPanel(false);
        });
    }
    if (nueva) {
        nueva.addEventListener("click", function () {
            if (conversacion) {
                fetch("/api/chat/" + conversacion + "/cerrar?sesion=" + encodeURIComponent(sesion), {
                    method: "POST",
                    headers: { "X-XSRF-TOKEN": csrf() }
                }).catch(function () { /* la conversación local se reinicia aunque el cierre falle */ });
            }
            conversacion = null;
            sessionStorage.removeItem("tnConversacion");
            log.replaceChildren();
            var ayuda = document.createElement("p");
            ayuda.className = "tn-chat-ayuda";
            ayuda.textContent = "Nueva conversación. Pregunta por un equipo, un precio o tu pedido.";
            log.appendChild(ayuda);
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
        var espera = agregar("espera", "Consultando el catálogo…", []);
        fetch("/api/chat", {
            method: "POST",
            credentials: "same-origin",
            headers: {
                "Content-Type": "application/json",
                "X-XSRF-TOKEN": csrf()
            },
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
            espera.remove();
            conversacion = String(cuerpo.idConversacion);
            sessionStorage.setItem("tnConversacion", conversacion);
            agregar("asistente", cuerpo.respuesta, cuerpo.productos || []);
        }).catch(function (error) {
            espera.remove();
            agregar("error", error.message || "El asistente no respondió.", []);
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
        return bloque;
    }
})();
