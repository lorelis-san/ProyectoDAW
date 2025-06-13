package com.appWeb.cotizacion.service.product;

import com.appWeb.cotizacion.dto.products.SupplierDTO;
import com.appWeb.cotizacion.model.productos.Supplier;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SupplierService {

    ResponseEntity<Map<String, Object>> getAllSuppliers();
    ResponseEntity<Map<String, Object>> getSupplierById(Long id);
    ResponseEntity<Map<String, Object>> getSupplierByRuc(String ruc);
    ResponseEntity<Map<String, Object>> saveSupplier(SupplierDTO supplierDTO);
    ResponseEntity<Map<String, Object>> updateSupplier(Long id, SupplierDTO supplierDTO);
    ResponseEntity<Map<String, Object>> deleteSupplier(Long id);
    ResponseEntity<Map<String, Object>> getSuppliersEnabled();

    // Métodos de conversión
    SupplierDTO convertToDTO(Supplier supplier);
    Supplier convertToEntity(SupplierDTO supplierDTO);
}
