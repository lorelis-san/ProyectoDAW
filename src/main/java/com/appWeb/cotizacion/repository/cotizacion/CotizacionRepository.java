package com.appWeb.cotizacion.repository.cotizacion;

import com.appWeb.cotizacion.enums.EstadoCotizacion;
import com.appWeb.cotizacion.model.cotizacion.Cotizacion;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    List<Cotizacion> findByEstadoNot(EstadoCotizacion estado, Sort sort);
    Optional<Cotizacion> findByIdAndEstadoNot(Long id, EstadoCotizacion estado);
}
