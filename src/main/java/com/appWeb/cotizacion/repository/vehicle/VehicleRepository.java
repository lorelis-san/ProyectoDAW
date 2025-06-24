package com.appWeb.cotizacion.repository.vehicle;

import com.appWeb.cotizacion.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Vehicle findByPlaca(String placa);

    List<Vehicle> findByEnabledTrue();

    @Query("""
    SELECT v FROM Vehicle v
    WHERE v.enabled = true AND (
        LOWER(v.placa) LIKE LOWER(CONCAT('%', :term, '%')) OR
        LOWER(v.marca) LIKE LOWER(CONCAT('%', :term, '%')) OR
        LOWER(v.modelo) LIKE LOWER(CONCAT('%', :term, '%'))
    )
""")
    List<Vehicle> buscarPorPlacaMarcaModelo(@Param("term") String term);

}
