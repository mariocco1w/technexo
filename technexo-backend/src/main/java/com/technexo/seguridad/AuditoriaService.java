package com.technexo.seguridad;

import com.technexo.seguridad.entity.AuditoriaOperacion;
import com.technexo.seguridad.repository.AuditoriaRepository;
import com.technexo.common.tiempo.FechaUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public void registrar(String accion, String entidad, String idEntidad, String detalle) {
        AuditoriaOperacion fila = new AuditoriaOperacion();
        fila.setIdUsuario(SesionActual.idUsuario());
        fila.setAccion(limitar(accion, 80));
        fila.setEntidad(limitar(entidad, 40));
        fila.setIdEntidad(limitar(idEntidad, 40));
        fila.setDetalle(limitar(detalle, 500));
        fila.setFecha(FechaUtil.ahoraUtc());
        auditoriaRepository.save(fila);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaOperacion> recientes() {
        return auditoriaRepository.findTop12ByOrderByIdAuditoriaDesc();
    }

    private String limitar(String valor, int maximo) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.replaceAll("[\\r\\n]+", " ").trim();
        return limpio.length() <= maximo ? limpio : limpio.substring(0, maximo);
    }
}
