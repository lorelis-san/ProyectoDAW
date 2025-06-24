package com.appWeb.cotizacion.service.cotizacion;


import com.appWeb.cotizacion.dto.cotizacion.CotizacionDTO;
import com.appWeb.cotizacion.dto.cotizacion.CotizacionResponseDTO;
import com.appWeb.cotizacion.model.cotizacion.Cotizacion;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CotizacionService {

    CotizacionResponseDTO mapToResponseDTO(Cotizacion cotizacion);
    ResponseEntity<Map<String, Object>> listarCotizaciones();

    ResponseEntity<Map<String, Object>> obtenerCotizacionPorId(Long id);

    Cotizacion obtenerPorId(Long id);

    ResponseEntity<Map<String, Object>> crearCotizacion(CotizacionDTO dto);

    ResponseEntity<Map<String, Object>> actualizarCotizacion(CotizacionResponseDTO dto);

    ResponseEntity<Map<String, Object>> eliminarCotizacion(Long id);

    ResponseEntity<Map<String, Object>> buscarPorTermino(String termino);
}









