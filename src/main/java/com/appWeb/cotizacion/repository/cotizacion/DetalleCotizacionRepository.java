package com.appWeb.cotizacion.repository.cotizacion;

import com.appWeb.cotizacion.model.cotizacion.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Long> {

    void deleteByCotizacionIdAndProductoId(Long cotizacionId, Long productoId);
    //enabled true

    Optional<DetalleCotizacion> findByCotizacionIdAndProductoId(Long cotizacionId, Long productoId);

    void deleteAllByCotizacionId(Long id);
}
