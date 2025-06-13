package com.appWeb.cotizacion.repository.cotizacion;

import com.appWeb.cotizacion.model.cotizacion.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
}
