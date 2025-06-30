package com.appWeb.cotizacion.service.product.implement;

import com.appWeb.cotizacion.dto.products.SupplierDTO;
import com.appWeb.cotizacion.model.productos.Supplier;
import com.appWeb.cotizacion.repository.productos.SupplierRepository;
import com.appWeb.cotizacion.service.product.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public ResponseEntity<Map<String, Object>> getAllSuppliers() {
        Map<String, Object> res = new HashMap<>();
        List<Supplier> list = supplierRepository.findAll();
        List<SupplierDTO> dtos = list.stream().map(this::convertToDTO).toList();

        res.put("data", dtos);
        res.put("mensaje", dtos.isEmpty() ? "No hay proveedores" : "Lista de proveedores");
        res.put("status", dtos.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
        res.put("fecha", new Date());

        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getSupplierById(Long id) {
        Map<String, Object> res = new HashMap<>();
        Optional<Supplier> supplier = supplierRepository.findById(id);
        if (supplier.isPresent()) {
            res.put("data", convertToDTO(supplier.get()));
            res.put("mensaje", "Proveedor encontrado");
            res.put("status", HttpStatus.OK);
        } else {
            res.put("mensaje", "No encontrado");
            res.put("status", HttpStatus.NOT_FOUND);
        }
        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getSupplierByRuc(String ruc) {
        Map<String, Object> res = new HashMap<>();
        Optional<Supplier> supplier = supplierRepository.findByRuc(ruc);
        if (supplier.isPresent()) {
            res.put("data", convertToDTO(supplier.get()));
            res.put("mensaje", "Proveedor encontrado");
            res.put("status", HttpStatus.OK);
        } else {
            res.put("mensaje", "No encontrado");
            res.put("status", HttpStatus.NOT_FOUND);
        }
        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> saveSupplier(SupplierDTO dto) {
        Map<String, Object> res = new HashMap<>();
        if (supplierRepository.existsByRuc(dto.getRuc())) {
            res.put("mensaje", "Ya existe un proveedor con ese RUC");
            res.put("status", HttpStatus.CONFLICT);
        } else {
            Supplier saved = supplierRepository.save(convertToEntity(dto));
            res.put("data", convertToDTO(saved));
            res.put("mensaje", "Proveedor registrado");
            res.put("status", HttpStatus.CREATED);
        }
        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateSupplier(Long id, SupplierDTO dto) {
        Map<String, Object> res = new HashMap<>();
        Optional<Supplier> opt = supplierRepository.findById(id);

        if (opt.isEmpty()) {
            res.put("mensaje", "Proveedor no encontrado");
            res.put("status", HttpStatus.NOT_FOUND);
        } else {
            Supplier s = opt.get();

            if (!s.getRuc().equals(dto.getRuc()) && supplierRepository.existsByRuc(dto.getRuc())) {
                res.put("mensaje", "Ya existe un proveedor con ese RUC");
                res.put("status", HttpStatus.CONFLICT);
            } else {
                s.setName(dto.getName());
                s.setRuc(dto.getRuc());
                s.setPhone(dto.getPhone());
                s.setEmail(dto.getEmail());

                res.put("data", convertToDTO(supplierRepository.save(s)));
                res.put("mensaje", "Proveedor actualizado");
                res.put("status", HttpStatus.OK);
            }
        }

        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteSupplier(Long id) {
        Map<String, Object> res = new HashMap<>();

        Optional<Supplier> optionalSupplier = supplierRepository.findById(id);

        if (optionalSupplier.isEmpty()) {
            res.put("mensaje", "Proveedor no encontrado");
            res.put("status", HttpStatus.NOT_FOUND);
        } else {
            Supplier supplier = optionalSupplier.get();
            supplier.setEnabled(false);  // Solo lo deshabilita
            supplierRepository.save(supplier);

            res.put("mensaje", "Proveedor deshabilitado correctamente");
            res.put("status", HttpStatus.OK);
        }
        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getSuppliersEnabled() {
        Map<String, Object> respuesta = new HashMap<>();

        List<SupplierDTO> lista = supplierRepository.findByEnabledTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (!lista.isEmpty()) {
            respuesta.put("mensaje", "Lista de proveedores habilitados");
            respuesta.put("data", lista);
            respuesta.put("status", HttpStatus.OK);
            respuesta.put("fecha", new Date());
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("mensaje", "No hay proveedores registrados");
            respuesta.put("status", HttpStatus.NOT_FOUND);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> buscarPorRucORazonSocialEnabledTrue(String termino) {
        Map<String, Object> response = new HashMap<>();
        List<SupplierDTO> lista = supplierRepository
                .buscarPorRucORazonSocial(termino)
                .stream().map(this::convertToDTO)
                .collect(Collectors.toList());
        if (!lista.isEmpty()) {
            response.put("mensaje", "Resultados encontrados");
            response.put("data", lista);
            response.put("status", HttpStatus.OK);
        } else {
            response.put("mensaje", "No se encontraron coincidencias");
            response.put("status", HttpStatus.NOT_FOUND);
        }
        response.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) response.get("status")).body(response);
    }


    @Override
    public SupplierDTO convertToDTO(Supplier supplier) {
        SupplierDTO dto = new SupplierDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setRuc(supplier.getRuc());
        dto.setEmail(supplier.getEmail());
        dto.setPhone(supplier.getPhone());
        dto.setEnabled(supplier.getEnabled());
        return dto;
    }

    @Override
    public Supplier convertToEntity(SupplierDTO supplierDTO) {
        Supplier supplier = new Supplier();
        supplier.setId(supplierDTO.getId());
        supplier.setName(supplierDTO.getName());
        supplier.setRuc(supplierDTO.getRuc());
        supplier.setEmail(supplierDTO.getEmail());
        supplier.setPhone(supplierDTO.getPhone());
        supplier.setEnabled(supplierDTO.getEnabled());
        return supplier;
    }
}
