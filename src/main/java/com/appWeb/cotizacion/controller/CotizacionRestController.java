package com.appWeb.cotizacion.controller;

import com.appWeb.cotizacion.dto.client.ClientDTO;
import com.appWeb.cotizacion.dto.cotizacion.CotizacionDTO;
import com.appWeb.cotizacion.dto.cotizacion.CotizacionResponseDTO;
import com.appWeb.cotizacion.dto.products.ProductDTO;
import com.appWeb.cotizacion.dto.vehicle.VehicleDTO;
import com.appWeb.cotizacion.service.client.ClientService;
import com.appWeb.cotizacion.service.cotizacion.CotizacionService;
import com.appWeb.cotizacion.service.product.ProductsService;
import com.appWeb.cotizacion.service.vehicle.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cotizaciones")
@CrossOrigin(origins = "*")
public class CotizacionRestController {

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ProductsService productService;

    // Crear nueva cotización
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCotizacion(@RequestBody CotizacionDTO dto) {
        return cotizacionService.crearCotizacion(dto);
    }

    // Listar cotizaciones
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarCotizaciones() {
        return cotizacionService.listarCotizaciones();
    }

    // Obtener cotización por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerCotizacionPorId(@PathVariable Long id) {
        return cotizacionService.obtenerCotizacionPorId(id);
    }

    // Actualizar cotización
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarCotizacion(@PathVariable Long id, @RequestBody CotizacionResponseDTO dto) {
        dto.setId(id);
        return cotizacionService.actualizarCotizacion(dto);
    }

    // Buscar vehículo por placa
    @GetMapping("/vehiculo/placa/{placa}")
    public ResponseEntity<Map<String, Object>> buscarVehiculoPorPlaca(@PathVariable String placa) {
        return vehicleService.getByPlaca(placa.trim().toUpperCase());
    }

    // Guardar vehículo desde API
    @PostMapping("/vehiculo")
    public ResponseEntity<Map<String, Object>> guardarVehiculo(@RequestBody VehicleDTO vehicleDTO) {
        return vehicleService.saveVehicle(vehicleDTO);
    }

    // Buscar cliente por documento
    @GetMapping("/cliente/documento/{numero}")
    public ResponseEntity<Map<String, Object>> buscarClientePorDocumento(@PathVariable String numero) {
        return clientService.getClientByDocumentNumber(numero.trim());
    }

    // Guardar cliente desde API
    @PostMapping("/cliente")
    public ResponseEntity<Map<String, Object>> guardarCliente(@RequestBody ClientDTO clientDTO) {
        return clientService.saveClient(clientDTO);
    }

    // Buscar productos por nombre o código
    @GetMapping("/productos/buscar/{termino}")
    public ResponseEntity<Map<String, Object>> buscarProductos(@PathVariable String termino) {
        return productService.buscarPorNombreOCodigoEnabledTrue(termino);
    }

    // Eliminar cotización
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarCotizacion(@PathVariable Long id) {
        return cotizacionService.eliminarCotizacion(id);
    }

}
