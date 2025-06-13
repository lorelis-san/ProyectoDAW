package com.appWeb.cotizacion.repository.cotizacion;

import com.appWeb.cotizacion.model.cotizacion.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Long> {

    void deleteByCotizacionIdAndProductoId(Long cotizacionId, Long productoId);
    //enabled true
    List<DetalleCotizacion> findByCotizacionIdAndEnabledTrue(Long cotizacionId);

    List<DetalleCotizacion> findByCotizacionId(Long cotizacionId);

    void deleteAllByCotizacionId(Long id);
}
