package com.appWeb.cotizacion.controller;

import com.appWeb.cotizacion.dto.cotizacion.DetalleCotizacionDTO;
import com.appWeb.cotizacion.service.cotizacion.DetalleCotizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/detalle-cotizacion")
@CrossOrigin("*")
public class DetalleCotizacionController {

    @Autowired
    private DetalleCotizacionService detalleService;

    @GetMapping("/listar/{cotizacionId}")
    public ResponseEntity<Map<String, Object>> listar(@PathVariable Long cotizacionId) {
        return detalleService.listarPorCotizacionId(cotizacionId);
    }

    @PostMapping("/agregar/{cotizacionId}")
    public ResponseEntity<Map<String, Object>> agregar(@PathVariable Long cotizacionId,
                                                       @RequestBody DetalleCotizacionDTO dto) {
        return detalleService.agregarDetalle(cotizacionId, dto);
    }

    @PutMapping("/detalle/deshabilitar/{cotizacionId}/{productoId}")
    public ResponseEntity<Map<String, Object>> deshabilitarDetalleEspecifico(
            @PathVariable Long cotizacionId,
            @PathVariable Long productoId
    ) {
        return detalleService.deshabilitarDetallePorCotizacionYProducto(cotizacionId, productoId);
    }

    @PutMapping("/deshabilitar/{cotizacionId}")
    public ResponseEntity<Map<String, Object>> deshabilitarDetalles(@PathVariable Long cotizacionId) {
        return detalleService.deshabilitarDetallesPorCotizacion(cotizacionId);
    }


}
