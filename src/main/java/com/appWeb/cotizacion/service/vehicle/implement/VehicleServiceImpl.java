package com.appWeb.cotizacion.service.vehicle.implement;

import com.appWeb.cotizacion.dto.vehicle.VehicleDTO;
import com.appWeb.cotizacion.model.vehicle.Vehicle;
import com.appWeb.cotizacion.repository.vehicle.VehicleRepository;
import com.appWeb.cotizacion.service.vehicle.VehicleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle convertToEntity(VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(dto.getId());
        vehicle.setPlaca(dto.getPlaca());
        vehicle.setYear(dto.getYear());
        vehicle.setMarca(dto.getMarca());
        vehicle.setModelo(dto.getModelo());
        vehicle.setEnabled(dto.getEnabled());
        return vehicle;
    }

    private VehicleDTO convertToDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setPlaca(vehicle.getPlaca());
        dto.setYear(vehicle.getYear());
        dto.setMarca(vehicle.getMarca());
        dto.setModelo(vehicle.getModelo());
        dto.setEnabled(vehicle.getEnabled());
        return dto;
    }

    @Override
    public ResponseEntity<Map<String, Object>> saveVehicle(VehicleDTO vehicleDTO) {
        Map<String, Object> respuesta = new HashMap<>();
        String placa = vehicleDTO.getPlaca().trim().toUpperCase();

        if (vehicleRepository.findByPlaca(placa) != null) {
            respuesta.put("mensaje", "Ya existe un vehículo con esa placa");
            respuesta.put("status", HttpStatus.BAD_REQUEST);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }

        Vehicle guardado = vehicleRepository.save(convertToEntity(vehicleDTO));
        respuesta.put("mensaje", "Vehículo guardado correctamente");
        respuesta.put("data", convertToDTO(guardado));
        respuesta.put("status", HttpStatus.CREATED);
        respuesta.put("fecha", new Date());

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllVehicles() {
        Map<String, Object> respuesta = new HashMap<>();
        List<Vehicle> lista = vehicleRepository.findByEnabledTrue();
        if (!lista.isEmpty()) {
            respuesta.put("mensaje", "Lista de vehículos (SP)");
            respuesta.put("data", lista);
            respuesta.put("status", HttpStatus.OK);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.OK).body(respuesta);
        } else {
            respuesta.put("mensaje", "No hay vehículos habilitados");
            respuesta.put("status", HttpStatus.NOT_FOUND);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getVehicleById(Long id) {
        Map<String, Object> respuesta = new HashMap<>();
        Optional<Vehicle> opt = vehicleRepository.findById(id);

        if (opt.isPresent()) {
            respuesta.put("mensaje", "Vehículo encontrado");
            respuesta.put("data", convertToDTO(opt.get()));
            respuesta.put("status", HttpStatus.OK);
            respuesta.put("fecha", new Date());
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("mensaje", "Vehículo no encontrado con ID: " + id);
            respuesta.put("status", HttpStatus.NOT_FOUND);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateVehicle(VehicleDTO vehicleDTO) {
        Map<String, Object> respuesta = new HashMap<>();

        if (vehicleDTO.getId() == null) {
            respuesta.put("mensaje", "El ID del vehículo es requerido");
            respuesta.put("status", HttpStatus.BAD_REQUEST);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }

        Optional<Vehicle> opt = vehicleRepository.findById(vehicleDTO.getId());

        if (opt.isEmpty()) {
            respuesta.put("mensaje", "Vehículo no encontrado");
            respuesta.put("status", HttpStatus.NOT_FOUND);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }

        Vehicle duplicado = vehicleRepository.findByPlaca(vehicleDTO.getPlaca().trim().toUpperCase());
        if (duplicado != null && !duplicado.getId().equals(vehicleDTO.getId())) {
            respuesta.put("mensaje", "Ya existe otro vehículo con esa placa");
            respuesta.put("status", HttpStatus.CONFLICT);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
        }

        Vehicle existente = opt.get();
        existente.setPlaca(vehicleDTO.getPlaca().trim().toUpperCase());
        existente.setMarca(vehicleDTO.getMarca());
        existente.setModelo(vehicleDTO.getModelo());
        existente.setYear(vehicleDTO.getYear());

        vehicleRepository.save(existente);

        respuesta.put("mensaje", "Vehículo actualizado correctamente");
        respuesta.put("data", convertToDTO(existente));
        respuesta.put("status", HttpStatus.OK);
        respuesta.put("fecha", new Date());
        return ResponseEntity.ok(respuesta);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getByPlaca(String placa) {
        Map<String, Object> res = new HashMap<>();
        Vehicle v = vehicleRepository.findByPlaca(placa);
        if (v == null || !Boolean.TRUE.equals(v.getEnabled())) {
            res.put("mensaje", "No encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
        res.put("data", convertToDTO(v));
        return ResponseEntity.ok(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteVehicle(Long id) {
        Map<String, Object> respuesta = new HashMap<>();
        Optional<Vehicle> opt = vehicleRepository.findById(id);

        if (opt.isPresent()) {
            Vehicle vehicle = opt.get();
            vehicle.setEnabled(false);
            vehicleRepository.save(vehicle);

            respuesta.put("mensaje", "Vehículo deshabilitado correctamente");
            respuesta.put("status", HttpStatus.NO_CONTENT);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        } else {
            respuesta.put("mensaje", "Vehículo no encontrado");
            respuesta.put("status", HttpStatus.NOT_FOUND);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> buscarPorPlacaMarcaModelo(String termino) {
        Map<String, Object> res = new HashMap<>();
        List<VehicleDTO> lista = vehicleRepository.buscarPorPlacaMarcaModelo(termino)
                .stream()
                .map(this::convertToDTO)
                .toList();

        if (lista.isEmpty()) {
            res.put("mensaje", "No se encontraron coincidencias");
            res.put("status", HttpStatus.NOT_FOUND);
        } else {
            res.put("mensaje", "Vehículos encontrados");
            res.put("data", lista);
            res.put("status", HttpStatus.OK);
        }

        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

}
