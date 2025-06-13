package com.appWeb.cotizacion.service.vehicle;

import com.appWeb.cotizacion.dto.vehicle.VehicleDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface VehicleService {

    ResponseEntity<Map<String, Object>> saveVehicle(VehicleDTO vehicleDTO);

    ResponseEntity<Map<String, Object>> getAllVehicles();

    ResponseEntity<Map<String, Object>> getVehicleById(Long id);

    ResponseEntity<Map<String, Object>> updateVehicle(VehicleDTO vehicleDTO);

    ResponseEntity<Map<String, Object>> deleteVehicle(Long id); // inhabilitar

    ResponseEntity<Map<String, Object>> getByPlaca(String placa);


}
