package com.appWeb.cotizacion.service.cotizacion;

import com.appWeb.cotizacion.dto.cotizacion.DetalleCotizacionDTO;
import com.appWeb.cotizacion.enums.EstadoCotizacion;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

//    @Override
//    public ResponseEntity<Map<String, Object>> listarPorCotizacionId(Long cotizacionId) {
//        Map<String, Object> res = new HashMap<>();
//
//        List<DetalleCotizacion> detalles = detalleRepo.findByCotizacionId(cotizacionId);
//        List<DetalleCotizacionDTO> dtos = detalles.stream().map(this::convertToDTO).collect(Collectors.toList());
//
//        res.put("data", dtos);
//        res.put("mensaje", detalles.isEmpty() ? "Sin detalles" : "Detalles obtenidos");
//        res.put("status", detalles.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
//        res.put("fecha", new Date());
//
//        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
//    }
//
//    @Override
//    public ResponseEntity<Map<String, Object>> agregarDetalle(Long cotizacionId, DetalleCotizacionDTO dto) {
//        Map<String, Object> res = new HashMap<>();
//
//        Cotizacion cotizacion = cotizacionRepo.findById(cotizacionId)
//                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
//
//        Products producto = productsRepo.findById(dto.getProductoId())
//                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
//
//        DetalleCotizacion nuevoDetalle = new DetalleCotizacion(cotizacion, producto, dto.getCantidad(), dto.getPrecioUnitario());
//        detalleRepo.save(nuevoDetalle);
//
//        // ✅ Recalcular totales
//        List<DetalleCotizacion> detallesActivos = detalleRepo.findByCotizacionId(cotizacionId);
//        BigDecimal nuevoSubtotal = detallesActivos.stream()
//                .map(DetalleCotizacion::getSubtotal)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        cotizacion.setSubtotal(nuevoSubtotal);
//        cotizacion.setIgv(BigDecimal.ZERO); // Puedes cambiar si aplicas IGV
//        cotizacion.setTotal(nuevoSubtotal);
//        cotizacion.setFechaModificacion(LocalDateTime.now());
//        cotizacion.setEstado(EstadoCotizacion.MODIFICADA);
//        cotizacionRepo.save(cotizacion);
//        res.put("mensaje", "Detalle agregado correctamente");
//        res.put("data", dto);
//        res.put("status", HttpStatus.CREATED);
//        res.put("fecha", new Date());
//        return ResponseEntity.status(HttpStatus.CREATED).body(res);
//    }

//    @Override
//    public ResponseEntity<Map<String, Object>> deshabilitarDetallePorCotizacionYProducto(Long cotizacionId, Long productoId) {
//        Map<String, Object> res = new HashMap<>();
//
//        Optional<DetalleCotizacion> detalleOpt = detalleRepo
//                .findByCotizacionId(cotizacionId).stream()
//                .filter(d -> d.getProducto().getId().equals(productoId))
//                .findFirst();
//
//        if (detalleOpt.isPresent()) {
//            detalleRepo.delete(detalleOpt.get()); // 🔥 Eliminación definitiva
//
//            // Recalcular totales
//            List<DetalleCotizacion> detallesRestantes = detalleRepo.findByCotizacionIdAndProductoId(cotizacionId);
//            BigDecimal nuevoSubtotal = detallesRestantes.stream()
//                    .map(DetalleCotizacion::getSubtotal)
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//            Cotizacion cotizacion = cotizacionRepo.findById(cotizacionId).orElseThrow();
//            cotizacion.setSubtotal(nuevoSubtotal);
//            cotizacion.setIgv(BigDecimal.ZERO); // Si usas IGV
//            cotizacion.setTotal(nuevoSubtotal);
//            cotizacion.setFechaModificacion(LocalDateTime.now());
//            cotizacion.setEstado(EstadoCotizacion.MODIFICADA);
//            cotizacionRepo.save(cotizacion);
//
//            res.put("mensaje", "Producto eliminado permanentemente de la cotización");
//            res.put("status", HttpStatus.OK);
//        } else {
//            res.put("mensaje", "No se encontró el producto en la cotización");
//            res.put("status", HttpStatus.NOT_FOUND);
//        }
//
//        res.put("fecha", new Date());
//        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
//    }
//
//    @Override
//    public ResponseEntity<Map<String, Object>> deshabilitarDetallesPorCotizacion(Long cotizacionId) {
//        Map<String, Object> res = new HashMap<>();
//        List<DetalleCotizacion> detalles = detalleRepo.findByCotizacionId(cotizacionId);
//
//        if (detalles.isEmpty()) {
//            res.put("mensaje", "No se encontraron detalles activos para esta cotización");
//            res.put("status", HttpStatus.NOT_FOUND);
//            res.put("fecha", new Date());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
//        }
//
//        for (DetalleCotizacion d : detalles) {
////            d.setEnabled(false);
//        }
//        detalleRepo.saveAll(detalles);
//
//        res.put("mensaje", "Detalles deshabilitados correctamente");
//        res.put("totalDeshabilitados", detalles.size());
//        res.put("status", HttpStatus.OK);
//        res.put("fecha", new Date());
//        return ResponseEntity.ok(res);
//    }
//
//

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
