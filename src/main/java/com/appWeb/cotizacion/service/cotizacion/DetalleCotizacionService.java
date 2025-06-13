package com.appWeb.cotizacion.service.cotizacion;

import com.appWeb.cotizacion.dto.cotizacion.DetalleCotizacionDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DetalleCotizacionService {

    ResponseEntity<Map<String, Object>> listarPorCotizacionId(Long cotizacionId);

    ResponseEntity<Map<String, Object>> agregarDetalle(Long cotizacionId, DetalleCotizacionDTO detalleDTO);

    ResponseEntity<Map<String, Object>> deshabilitarDetallesPorCotizacion(Long cotizacionId);

    ResponseEntity<Map<String, Object>> deshabilitarDetallePorCotizacionYProducto(Long cotizacionId, Long productoId);

}
