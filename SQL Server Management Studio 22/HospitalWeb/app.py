"""
Hospital W 2.0 - Panel de Administracion (pagina de pruebas)
Backend Flask + pyodbc (SQL Server, autenticacion de Windows).

Permite al administrador LISTAR, INGRESAR y ELIMINAR datos de:
  - Pacientes
  - Empleados
  - Insumos

Ejecutar:  python app.py   y abrir http://127.0.0.1:5000
"""

import os
from contextlib import contextmanager
from datetime import date, datetime
from decimal import Decimal
from functools import wraps

import pyodbc
from flask import Flask, jsonify, render_template, request, session

# ---------------------------------------------------------------------------
# Configuracion (se puede cambiar con variables de entorno)
# ---------------------------------------------------------------------------
DB_DRIVER = os.environ.get("HOSPITAL_DRIVER", "ODBC Driver 18 for SQL Server")
DB_SERVER = os.environ.get("HOSPITAL_SERVER", r".\SQLEXPRESS")
DB_NAME = os.environ.get("HOSPITAL_DB", "HospitalW2")
DB_TRUSTED = os.environ.get("HOSPITAL_TRUSTED", "yes")  # autenticacion Windows

ADMIN_USER = os.environ.get("HOSPITAL_ADMIN_USER", "admin")
ADMIN_PASS = os.environ.get("HOSPITAL_ADMIN_PASS", "admin123")

CONN_STR = (
    f"DRIVER={{{DB_DRIVER}}};"
    f"SERVER={DB_SERVER};"
    f"DATABASE={DB_NAME};"
    f"Trusted_Connection={DB_TRUSTED};"
    "TrustServerCertificate=yes;"
)

app = Flask(__name__)
app.secret_key = os.environ.get("HOSPITAL_SECRET", "hospital-w2-panel-secret")


# ---------------------------------------------------------------------------
# Utilidades
# ---------------------------------------------------------------------------
@contextmanager
def db():
    """Conexion con commit/rollback automatico y cierre garantizado."""
    conn = pyodbc.connect(CONN_STR, autocommit=False)
    try:
        yield conn
        conn.commit()
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()


def to_jsonable(value):
    if isinstance(value, Decimal):
        return float(value)
    if isinstance(value, (datetime, date)):
        return value.isoformat()
    return value


def fetch(cursor, sql, params=()):
    cursor.execute(sql, params)
    cols = [c[0] for c in cursor.description]
    return [{c: to_jsonable(v) for c, v in zip(cols, row)} for row in cursor.fetchall()]


def friendly_error(exc):
    if isinstance(exc, pyodbc.Error) and len(exc.args) > 1:
        return str(exc.args[1])
    return str(exc)


def login_required(view):
    @wraps(view)
    def wrapper(*args, **kwargs):
        if not session.get("user"):
            return jsonify(error="Sesion no iniciada."), 401
        return view(*args, **kwargs)

    return wrapper


def error_response(exc):
    msg = friendly_error(exc)
    if isinstance(exc, pyodbc.IntegrityError):
        return jsonify(error="Restriccion de integridad: " + msg), 409
    return jsonify(error=msg), 400


# ---------------------------------------------------------------------------
# Autenticacion del administrador
# ---------------------------------------------------------------------------
@app.get("/")
def index():
    return render_template("index.html")


@app.post("/api/login")
def api_login():
    data = request.get_json(silent=True) or {}
    if data.get("usuario") == ADMIN_USER and data.get("clave") == ADMIN_PASS:
        session["user"] = ADMIN_USER
        return jsonify(user=ADMIN_USER)
    return jsonify(error="Usuario o clave incorrectos."), 401


@app.post("/api/logout")
def api_logout():
    session.clear()
    return jsonify(ok=True)


@app.get("/api/me")
def api_me():
    return jsonify(user=session.get("user"))


# ---------------------------------------------------------------------------
# Datos para los formularios (llaves foraneas y opciones validas)
# ---------------------------------------------------------------------------
@app.get("/api/catalogos")
@login_required
def api_catalogos():
    with db() as conn:
        cur = conn.cursor()
        data = {
            "turnos": fetch(cur, "SELECT id_turno, nombre_turno FROM dbo.Turno ORDER BY nombre_turno;"),
            "especialidades": fetch(
                cur,
                "SELECT id_especialidad, nombre_especialidad FROM dbo.Especialidad ORDER BY nombre_especialidad;",
            ),
            "familiares": fetch(
                cur,
                "SELECT id_familiar, nombre + ' ' + apellido AS nombre_completo "
                "FROM dbo.FamiliarResponsable ORDER BY nombre, apellido;",
            ),
            "medicos": fetch(
                cur,
                "SELECT id_medico_externo, nombre + ' ' + apellido AS nombre_completo "
                "FROM dbo.MedicoExterno ORDER BY nombre, apellido;",
            ),
            "proveedores": fetch(
                cur,
                "SELECT id_proveedor, razon_social FROM dbo.Proveedor ORDER BY razon_social;",
            ),
        }
    data["generos"] = ["Femenino", "Masculino", "Otro"]
    data["roles"] = ["Medico", "Enfermeria", "Administrativo", "Tecnico", "Otro"]
    return jsonify(data)


# ---------------------------------------------------------------------------
# PACIENTES
# ---------------------------------------------------------------------------
PACIENTE_SELECT = """
SELECT p.id_paciente,
       p.dpi,
       p.nombre,
       p.apellido,
       CONVERT(varchar(10), p.fecha_nacimiento, 23) AS fecha_nacimiento,
       p.genero,
       p.direccion,
       p.telefono,
       p.es_adulto_mayor,
       p.id_familiar,
       ISNULL(fr.nombre + ' ' + fr.apellido, '(sin asignar)') AS familiar,
       p.id_medico_externo,
       ISNULL(me.nombre + ' ' + me.apellido, '(sin asignar)') AS medico_externo,
       (SELECT COUNT(*) FROM dbo.Cita c WHERE c.id_paciente = p.id_paciente) AS citas,
       (SELECT COUNT(*) FROM dbo.Hospitalizacion h WHERE h.id_paciente = p.id_paciente) AS hospitalizaciones
FROM dbo.Paciente p
LEFT JOIN dbo.FamiliarResponsable fr ON fr.id_familiar = p.id_familiar
LEFT JOIN dbo.MedicoExterno me ON me.id_medico_externo = p.id_medico_externo
ORDER BY p.id_paciente;
"""


@app.get("/api/pacientes")
@login_required
def api_pacientes():
    with db() as conn:
        return jsonify(fetch(conn.cursor(), PACIENTE_SELECT))


@app.post("/api/pacientes")
@login_required
def api_paciente_crear():
    d = request.get_json(silent=True) or {}
    requeridos = [
        "dpi", "nombre", "apellido", "fecha_nacimiento", "genero",
        "direccion", "telefono", "id_familiar", "id_medico_externo",
    ]
    faltan = [c for c in requeridos if d.get(c) in (None, "")]
    if faltan:
        return jsonify(error="Campos obligatorios faltantes: " + ", ".join(faltan)), 400
    if d["genero"] not in ("Femenino", "Masculino", "Otro"):
        return jsonify(error="Genero no valido."), 400
    try:
        with db() as conn:
            conn.cursor().execute(
                """
                INSERT INTO dbo.Paciente
                    (dpi, nombre, apellido, fecha_nacimiento, genero, direccion,
                     telefono, es_adulto_mayor, id_familiar, id_medico_externo)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """,
                (
                    d["dpi"].strip(),
                    d["nombre"].strip(),
                    d["apellido"].strip(),
                    d["fecha_nacimiento"],
                    d["genero"],
                    d["direccion"].strip(),
                    d["telefono"].strip(),
                    1 if d.get("es_adulto_mayor") else 0,
                    int(d["id_familiar"]),
                    int(d["id_medico_externo"]),
                ),
            )
    except pyodbc.Error as exc:
        return error_response(exc)
    return jsonify(ok=True)


@app.delete("/api/pacientes/<int:id_paciente>")
@login_required
def api_paciente_eliminar(id_paciente):
    try:
        with db() as conn:
            cur = conn.cursor()
            cur.execute("DELETE FROM dbo.Hospitalizacion WHERE id_paciente=?;", (id_paciente,))
            hosp = cur.rowcount
            cur.execute("DELETE FROM dbo.Cita WHERE id_paciente=?;", (id_paciente,))
            citas = cur.rowcount
            cur.execute("DELETE FROM dbo.Paciente WHERE id_paciente=?;", (id_paciente,))
            if cur.rowcount == 0:
                raise LookupError("No existe el paciente indicado.")
    except LookupError as exc:
        return jsonify(error=str(exc)), 404
    except pyodbc.Error as exc:
        return error_response(exc)
    return jsonify(
        ok=True,
        detalle=f"Paciente eliminado. Tambien se borraron {citas} cita(s) y {hosp} hospitalizacion(es).",
    )


# ---------------------------------------------------------------------------
# EMPLEADOS
# ---------------------------------------------------------------------------
EMPLEADO_SELECT = """
SELECT e.id_empleado,
       e.nss,
       e.nombre,
       e.apellido,
       e.rol,
       e.estado_activo,
       e.id_turno,
       ISNULL(t.nombre_turno, '(sin turno)') AS nombre_turno,
       e.id_especialidad,
       ISNULL(esp.nombre_especialidad, '(sin especialidad)') AS nombre_especialidad,
       (SELECT COUNT(*) FROM dbo.Cita c WHERE c.id_empleado = e.id_empleado) AS citas,
       (SELECT COUNT(*) FROM dbo.AsignacionPersonalSala a WHERE a.id_empleado = e.id_empleado) AS asignaciones
FROM dbo.Empleado e
LEFT JOIN dbo.Turno t ON t.id_turno = e.id_turno
LEFT JOIN dbo.Especialidad esp ON esp.id_especialidad = e.id_especialidad
ORDER BY e.id_empleado;
"""


@app.get("/api/empleados")
@login_required
def api_empleados():
    with db() as conn:
        return jsonify(fetch(conn.cursor(), EMPLEADO_SELECT))


@app.post("/api/empleados")
@login_required
def api_empleado_crear():
    d = request.get_json(silent=True) or {}
    requeridos = ["nss", "nombre", "apellido", "rol", "id_turno"]
    faltan = [c for c in requeridos if d.get(c) in (None, "")]
    if faltan:
        return jsonify(error="Campos obligatorios faltantes: " + ", ".join(faltan)), 400
    if d["rol"] not in ("Medico", "Enfermeria", "Administrativo", "Tecnico", "Otro"):
        return jsonify(error="Rol no valido."), 400
    id_especialidad = d.get("id_especialidad") or None
    try:
        with db() as conn:
            conn.cursor().execute(
                """
                INSERT INTO dbo.Empleado
                    (nss, nombre, apellido, rol, estado_activo, id_turno, id_especialidad)
                VALUES (?,?,?,?,?,?,?)
                """,
                (
                    d["nss"].strip(),
                    d["nombre"].strip(),
                    d["apellido"].strip(),
                    d["rol"],
                    1 if d.get("estado_activo", True) else 0,
                    int(d["id_turno"]),
                    int(id_especialidad) if id_especialidad else None,
                ),
            )
    except pyodbc.Error as exc:
        return error_response(exc)
    return jsonify(ok=True)


@app.delete("/api/empleados/<int:id_empleado>")
@login_required
def api_empleado_eliminar(id_empleado):
    try:
        with db() as conn:
            cur = conn.cursor()
            cur.execute("DELETE FROM dbo.Cita WHERE id_empleado=?;", (id_empleado,))
            citas = cur.rowcount
            cur.execute("DELETE FROM dbo.AsignacionPersonalSala WHERE id_empleado=?;", (id_empleado,))
            asig = cur.rowcount
            cur.execute("DELETE FROM dbo.Empleado WHERE id_empleado=?;", (id_empleado,))
            if cur.rowcount == 0:
                raise LookupError("No existe el empleado indicado.")
    except LookupError as exc:
        return jsonify(error=str(exc)), 404
    except pyodbc.Error as exc:
        return error_response(exc)
    return jsonify(
        ok=True,
        detalle=f"Empleado eliminado. Tambien se borraron {citas} cita(s) y {asig} asignacion(es).",
    )


# ---------------------------------------------------------------------------
# INSUMOS
# ---------------------------------------------------------------------------
INSUMO_SELECT = """
SELECT i.id_insumo,
       i.codigo_insumo,
       i.nombre_insumo,
       i.tipo_insumo,
       i.stock_existencia,
       i.stock_maximo,
       i.costo_unitario,
       i.id_proveedor,
       ISNULL(p.razon_social, '(sin proveedor)') AS proveedor
FROM dbo.Insumo i
LEFT JOIN dbo.Proveedor p ON p.id_proveedor = i.id_proveedor
ORDER BY i.id_insumo;
"""


@app.get("/api/insumos")
@login_required
def api_insumos():
    with db() as conn:
        return jsonify(fetch(conn.cursor(), INSUMO_SELECT))


@app.post("/api/insumos")
@login_required
def api_insumo_crear():
    d = request.get_json(silent=True) or {}
    requeridos = [
        "codigo_insumo", "nombre_insumo", "tipo_insumo",
        "stock_existencia", "stock_maximo", "costo_unitario", "id_proveedor",
    ]
    faltan = [c for c in requeridos if d.get(c) in (None, "")]
    if faltan:
        return jsonify(error="Campos obligatorios faltantes: " + ", ".join(faltan)), 400
    try:
        existencia = Decimal(str(d["stock_existencia"]))
        maximo = Decimal(str(d["stock_maximo"]))
        costo = Decimal(str(d["costo_unitario"]))
    except Exception:
        return jsonify(error="Los valores de existencia, maximo y costo deben ser numericos."), 400
    if existencia < 0 or maximo <= 0 or existencia > maximo:
        return jsonify(
            error="El stock de existencia debe estar entre 0 y el stock maximo, "
            "y el stock maximo debe ser mayor que 0.",
        ), 400
    if costo < 0:
        return jsonify(error="El costo unitario no puede ser negativo."), 400
    try:
        with db() as conn:
            conn.cursor().execute(
                """
                INSERT INTO dbo.Insumo
                    (codigo_insumo, nombre_insumo, tipo_insumo,
                     stock_existencia, stock_maximo, costo_unitario, id_proveedor)
                VALUES (?,?,?,?,?,?,?)
                """,
                (
                    d["codigo_insumo"].strip(),
                    d["nombre_insumo"].strip(),
                    d["tipo_insumo"].strip(),
                    existencia,
                    maximo,
                    costo,
                    int(d["id_proveedor"]),
                ),
            )
    except pyodbc.Error as exc:
        return error_response(exc)
    return jsonify(ok=True)


@app.delete("/api/insumos/<int:id_insumo>")
@login_required
def api_insumo_eliminar(id_insumo):
    try:
        with db() as conn:
            cur = conn.cursor()
            cur.execute("DELETE FROM dbo.Insumo WHERE id_insumo=?;", (id_insumo,))
            if cur.rowcount == 0:
                raise LookupError("No existe el insumo indicado.")
    except LookupError as exc:
        return jsonify(error=str(exc)), 404
    except pyodbc.Error as exc:
        return error_response(exc)
    return jsonify(ok=True, detalle="Insumo eliminado.")


if __name__ == "__main__":
    print("=" * 60)
    print(" Hospital W 2.0 - Panel de Administracion")
    print(f" Servidor : {DB_SERVER}")
    print(f" Base     : {DB_NAME}")
    print(" Usuario  : " + ADMIN_USER + "   (clave configurable)")
    print(" Abrir    : http://127.0.0.1:5000")
    print("=" * 60)
    app.run(host="127.0.0.1", port=5000, debug=False)
