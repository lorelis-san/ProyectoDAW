package com.appWeb.cotizacion.repository.vehicle;

import com.appWeb.cotizacion.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Vehicle findByPlaca(String placa);

    List<Vehicle> findByEnabledTrue();
}
