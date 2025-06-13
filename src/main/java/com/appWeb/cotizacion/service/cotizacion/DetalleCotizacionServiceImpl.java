package com.appWeb.cotizacion.service.cotizacion;

import com.appWeb.cotizacion.dto.cotizacion.DetalleCotizacionDTO;
import com.appWeb.cotizacion.model.cotizacion.Cotizacion;
import com.appWeb.cotizacion.model.cotizacion.DetalleCotizacion;
import com.appWeb.cotizacion.model.productos.Products;
import com.appWeb.cotizacion.repository.cotizacion.CotizacionRepository;
import com.appWeb.cotizacion.repository.cotizacion.DetalleCotizacionRepository;
import com.appWeb.cotizacion.repository.productos.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DetalleCotizacionServiceImpl implements DetalleCotizacionService {

    @Autowired
    private DetalleCotizacionRepository detalleRepo;

    @Autowired
    private CotizacionRepository cotizacionRepo;

    @Autowired
    private ProductsRepository productsRepo;

    @Override
    public ResponseEntity<Map<String, Object>> listarPorCotizacionId(Long cotizacionId) {
        Map<String, Object> res = new HashMap<>();

        List<DetalleCotizacion> detalles = detalleRepo.findByCotizacionIdAndEnabledTrue(cotizacionId);
        List<DetalleCotizacionDTO> dtos = detalles.stream().map(this::convertToDTO).collect(Collectors.toList());

        res.put("data", dtos);
        res.put("mensaje", detalles.isEmpty() ? "Sin detalles" : "Detalles obtenidos");
        res.put("status", detalles.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
        res.put("fecha", new Date());

        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> agregarDetalle(Long cotizacionId, DetalleCotizacionDTO dto) {
        Map<String, Object> res = new HashMap<>();

        Optional<Cotizacion> cotOpt = cotizacionRepo.findById(cotizacionId);
        Optional<Products> prodOpt = productsRepo.findById(dto.getProductoId());

        if (cotOpt.isEmpty() || prodOpt.isEmpty()) {
            res.put("mensaje", "Cotización o producto no encontrados");
            res.put("status", HttpStatus.NOT_FOUND);
            res.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        DetalleCotizacion detalle = new DetalleCotizacion(
                cotOpt.get(),
                prodOpt.get(),
                dto.getCantidad(),
                dto.getPrecioUnitario()
        );

        detalleRepo.save(detalle);

        res.put("mensaje", "Detalle agregado correctamente");
        res.put("detalle", convertToDTO(detalle));
        res.put("status", HttpStatus.CREATED);
        res.put("fecha", new Date());

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deshabilitarDetallePorCotizacionYProducto(Long cotizacionId, Long productoId) {
        Map<String, Object> res = new HashMap<>();

        // Buscar detalle
        Optional<DetalleCotizacion> detalleOpt = detalleRepo
                .findByCotizacionId(cotizacionId).stream()
                .filter(d -> d.getProducto().getId().equals(productoId))
                .findFirst();

        if (detalleOpt.isPresent()) {
            DetalleCotizacion detalle = detalleOpt.get();
            detalle.setEnabled(false); // Necesitas tener la propiedad "enabled"
            detalleRepo.save(detalle);

            res.put("mensaje", "Producto eliminado de la cotización");
            res.put("status", HttpStatus.OK);
        } else {
            res.put("mensaje", "No se encontró el producto en la cotización");
            res.put("status", HttpStatus.NOT_FOUND);
        }

        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deshabilitarDetallesPorCotizacion(Long cotizacionId) {
        Map<String, Object> res = new HashMap<>();
        List<DetalleCotizacion> detalles = detalleRepo.findByCotizacionIdAndEnabledTrue(cotizacionId);

        if (detalles.isEmpty()) {
            res.put("mensaje", "No se encontraron detalles activos para esta cotización");
            res.put("status", HttpStatus.NOT_FOUND);
            res.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        for (DetalleCotizacion d : detalles) {
            d.setEnabled(false);
        }
        detalleRepo.saveAll(detalles);

        res.put("mensaje", "Detalles deshabilitados correctamente");
        res.put("totalDeshabilitados", detalles.size());
        res.put("status", HttpStatus.OK);
        res.put("fecha", new Date());
        return ResponseEntity.ok(res);
    }



    private DetalleCotizacionDTO convertToDTO(DetalleCotizacion detalle) {
        DetalleCotizacionDTO dto = new DetalleCotizacionDTO();
        dto.setProductoId(detalle.getProducto().getId());
        dto.setNombreProducto(detalle.getProducto().getName());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }
}
