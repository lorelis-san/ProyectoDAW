package com.appWeb.cotizacion.repository.cotizacion;

import com.appWeb.cotizacion.enums.EstadoCotizacion;
import com.appWeb.cotizacion.model.cotizacion.Cotizacion;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    List<Cotizacion> findByEstadoNot(EstadoCotizacion estado, Sort sort);
    Optional<Cotizacion> findByIdAndEstadoNot(Long id, EstadoCotizacion estado);

    @Query("""
    SELECT c FROM Cotizacion c
    JOIN c.cliente cli
    JOIN c.vehiculo veh
    JOIN c.user u
    WHERE 
        LOWER(c.numeroCotizacion) LIKE LOWER(CONCAT('%', :term, '%')) OR
        LOWER(c.estado) LIKE LOWER(CONCAT('%', :term, '%')) OR
        LOWER(cli.documentNumber) LIKE LOWER(CONCAT('%', :term, '%')) OR
        LOWER(veh.placa) LIKE LOWER(CONCAT('%', :term, '%')) OR
        LOWER(u.nombre) LIKE LOWER(CONCAT('%', :term, '%'))
""")
    List<Cotizacion> buscarPorTermino(@Param("term") String term);



}
