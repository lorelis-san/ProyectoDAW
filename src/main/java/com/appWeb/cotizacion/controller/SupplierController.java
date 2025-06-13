package com.appWeb.cotizacion.controller;

import com.appWeb.cotizacion.dto.products.SupplierDTO;
import com.appWeb.cotizacion.service.product.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // Listar todos los proveedores
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTodos() {
        return supplierService.getSuppliersEnabled();
    }

    // Obtener proveedor por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        return supplierService.getSupplierById(id);
    }

    @GetMapping("/ruc")
    public ResponseEntity<Map<String, Object>> obtenerPorRuc(@RequestParam String ruc) {
        return supplierService.getSupplierByRuc(ruc);
    }


    // Registrar proveedor
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody SupplierDTO supplierDTO) {
        return supplierService.saveSupplier(supplierDTO);
    }

    // Actualizar proveedor
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @RequestBody SupplierDTO supplierDTO) {
        return supplierService.updateSupplier(id, supplierDTO);
    }

    // Eliminar proveedor
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        return supplierService.deleteSupplier(id);
    }
}
