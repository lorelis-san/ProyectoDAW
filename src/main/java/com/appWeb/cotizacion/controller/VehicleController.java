package com.appWeb.cotizacion.controller;

import com.appWeb.cotizacion.dto.vehicle.VehicleDTO;
import com.appWeb.cotizacion.service.vehicle.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarVehiculos() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerVehiculoPorId(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearVehiculo(@Validated @RequestBody VehicleDTO vehicleDTO) {
        return vehicleService.saveVehicle(vehicleDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarVehiculo(@RequestBody VehicleDTO vehicleDTO,
                                                                  @PathVariable Long id) {
        vehicleDTO.setId(id);
        return vehicleService.updateVehicle(vehicleDTO);
    }

    // Eliminar (deshabilitar) vehículo
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarVehiculo(@PathVariable Long id) {
        return vehicleService.deleteVehicle(id);
    }

    // Buscar por placa
    @GetMapping("/placa")
    public ResponseEntity<Map<String, Object>> buscarPorPlaca(@RequestParam String placa) {
        return vehicleService.getByPlaca(placa);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(@RequestParam String termino) {
        return vehicleService.buscarPorPlacaMarcaModelo(termino);
    }

}
