package com.appWeb.cotizacion.service.product.implement;

import com.appWeb.cotizacion.dto.products.ProductDTO;
import com.appWeb.cotizacion.firebaseStorage.FirebaseStorageService;
import com.appWeb.cotizacion.model.productos.Category;
import com.appWeb.cotizacion.model.productos.Products;
import com.appWeb.cotizacion.model.productos.Supplier;
import com.appWeb.cotizacion.repository.productos.CategoryRepository;
import com.appWeb.cotizacion.repository.productos.ProductsRepository;
import com.appWeb.cotizacion.repository.productos.SupplierRepository;
import com.appWeb.cotizacion.service.product.ProductsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductsServiceImpl implements ProductsService {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    private Products convertToEntity(ProductDTO dto) {
        Products product = new Products();
        product.setId(dto.getId());
        product.setCod(dto.getCod());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setBrand(dto.getBrand());
        product.setModel(dto.getModel());
        product.setYear(dto.getYear());
        product.setCostPrice(dto.getCostPrice());
        product.setCostDealer(dto.getDealerPrice());
        product.setSalePrice(dto.getSalePrice());
        product.setImageUrl(dto.getImageUrl());
        product.setSede(dto.getSede());
        product.setEnabled(dto.getEnabled());
        if (dto.getCategoryProductId() != null) {
            Optional<Category> categoryOptional = categoryRepository.findById(dto.getCategoryProductId());
            categoryOptional.ifPresent(product::setCategoryproduct);
        }
        if (dto.getSupplierProductId() != null && dto.getSupplierProductId() > 0) {
            Optional<Supplier> supplierOptional = supplierRepository.findById(dto.getSupplierProductId());
            if (supplierOptional.isPresent()) {
                product.setSupplierProduct(supplierOptional.get());
            } else {
                throw new jakarta.persistence.EntityNotFoundException("Proveedor no encontrado con ID: " + dto.getSupplierProductId());
            }
        }
        return product;
    }

    private ProductDTO convertToDTO(Products product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setCod(product.getCod());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setBrand(product.getBrand());
        dto.setModel(product.getModel());
        dto.setYear(product.getYear());
        dto.setCostPrice(product.getCostPrice());
        dto.setDealerPrice(product.getCostDealer());
        dto.setSalePrice(product.getSalePrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setSede(product.getSede());
        dto.setEnabled(product.getEnabled());
        if (product.getCategoryproduct() != null) {
            dto.setCategoryProductId(product.getCategoryproduct().getId());
        }
        if (product.getSupplierProduct() != null) {
            dto.setSupplierProductId(product.getSupplierProduct().getId());
        }
        return dto;
    }

    @Override
    public ResponseEntity<Map<String, Object>> saveProduct(ProductDTO dto, MultipartFile imageFile) {
        Map<String, Object> response = new HashMap<>();
        try {
            validarProductoDTO(dto);
            Products product = convertToEntity(dto);
            if (dto.getId() == null && productsRepository.existsByCodAndEnabledTrue(dto.getCod())) {
                throw new IllegalArgumentException("El código ya está registrado");
            }
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = firebaseStorageService.uploadFile(imageFile);
                product.setImageUrl(imageUrl);
            } else if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) {
                product.setImageUrl(firebaseStorageService.uploadFileFromUrl(dto.getImageUrl()));
            }

            productsRepository.save(product);
            response.put("mensaje", "Producto guardado exitosamente");
            response.put("producto", convertToDTO(product));
            response.put("status", HttpStatus.CREATED);
            response.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("mensaje", "Error al guardar producto: " + e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST);
            response.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        Map<String, Object> response = new HashMap<>();
        List<ProductDTO> lista = productsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        response.put("mensaje", lista.isEmpty() ? "No hay productos" : "Lista de productos");
        response.put("data", lista);
        response.put("status", lista.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
        response.put("fecha", new Date());
        return ResponseEntity.status(lista.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllProductsEnabledTrue() {
        Map<String, Object> response = new HashMap<>();
        List<ProductDTO> lista = productsRepository.findByEnabledTrue()
                .stream().map(this::convertToDTO)
                .collect(Collectors.toList());
        response.put("mensaje", lista.isEmpty() ? "No hay productos activos" : "Lista de productos");
        response.put("data", lista);
        response.put("status", lista.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
        response.put("fecha", new Date());
        return ResponseEntity.status(lista.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getProductByIdEnabledTrue(Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Products> optional = productsRepository.findByIdAndEnabledTrue(id);

        if (optional.isPresent()) {
            response.put("producto", convertToDTO(optional.get()));
            response.put("mensaje", "Producto encontrado");
            response.put("status", HttpStatus.OK);
        } else {
            response.put("mensaje", "Producto no encontrado con ID: " + id);
            response.put("status", HttpStatus.NOT_FOUND);
        }
        response.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) response.get("status")).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> buscarPorNombreOCodigoEnabledTrue(String termino) {
        Map<String, Object> response = new HashMap<>();
        List<ProductDTO> lista = productsRepository
                .findByNameContainingIgnoreCaseAndEnabledTrueOrCodContainingIgnoreCaseAndEnabledTrue(termino, termino)
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
    public ResponseEntity<Map<String, Object>> getProductById(Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Products> optional = productsRepository.findById(id);
        if (optional.isPresent()) {
            response.put("producto", convertToDTO(optional.get()));
            response.put("mensaje", "Producto encontrado");
            response.put("status", HttpStatus.OK);
        } else {
            response.put("mensaje", "Producto no encontrado con ID: " + id);
            response.put("status", HttpStatus.NOT_FOUND);
        }
        response.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) response.get("status")).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateProduct(ProductDTO dto, MultipartFile imageFile) {
        Map<String, Object> response = new HashMap<>();
        Optional<Products> optional = productsRepository.findById(dto.getId());

        if (optional.isEmpty()) {
            response.put("mensaje", "Producto no encontrado");
            response.put("status", HttpStatus.NOT_FOUND);
            response.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            Products product = optional.get();
            product.setCod(dto.getCod());
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setBrand(dto.getBrand());
            product.setModel(dto.getModel());
            product.setYear(dto.getYear());
            product.setCostPrice(dto.getCostPrice());
            product.setCostDealer(dto.getDealerPrice());
            product.setSalePrice(dto.getSalePrice());

            if (dto.getCategoryProductId() != null)
                categoryRepository.findById(dto.getCategoryProductId()).ifPresent(product::setCategoryproduct);

            if (dto.getSupplierProductId() != null)
                supplierRepository.findById(dto.getSupplierProductId()).ifPresent(product::setSupplierProduct);

            if (imageFile != null && !imageFile.isEmpty()) {
                firebaseStorageService.deleteFile(product.getImageUrl());
                String newUrl = firebaseStorageService.uploadFile(imageFile);
                product.setImageUrl(newUrl);
            }

            productsRepository.save(product);
            response.put("mensaje", "Producto actualizado correctamente");
            response.put("producto", convertToDTO(product));
            response.put("status", HttpStatus.OK);
        } catch (Exception e) {
            response.put("mensaje", "Error al actualizar: " + e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST);
        }
        response.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) response.get("status")).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteProduct(Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Products> optional = productsRepository.findById(id);
        if (optional.isPresent()) {
            Products product = optional.get();
            product.setEnabled(false);
            productsRepository.save(product);
            response.put("mensaje", "Producto deshabilitado");
            response.put("status", HttpStatus.OK);
        } else {
            response.put("mensaje", "Producto no encontrado");
            response.put("status", HttpStatus.NOT_FOUND);
        }
        response.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) response.get("status")).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> buscarPorNombreOCodigo(String termino) {
        Map<String, Object> response = new HashMap<>();
        List<ProductDTO> lista = productsRepository.findByNameContainingIgnoreCaseOrCodContainingIgnoreCase(termino, termino)
                .stream().map(this::convertToDTO).collect(Collectors.toList());

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
    public ResponseEntity<Map<String, Object>> getProductsEnabled() {
        Map<String, Object> response = new HashMap<>();
        List<ProductDTO> lista = productsRepository.findAll().stream()
                .filter(p -> p.getEnabled() != null && p.getEnabled())
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        response.put("mensaje", lista.isEmpty() ? "No hay productos habilitados" : "Productos habilitados");
        response.put("data", lista);
        response.put("status", lista.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
        response.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) response.get("status")).body(response);
    }

    private void validarProductoDTO(ProductDTO dto) {
        validarCod(dto);
        validarYear(dto);
        validarPrice(dto);
        validarSede(dto);
    }

    private void validarCod(ProductDTO dto) {
        if (dto.getCod() == null || dto.getCod().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del producto es obligatorio");
        }
//        if (dto.getId() == null && productsRepository.existsByCod(dto.getCod())) {
//            throw new IllegalArgumentException("El código ya esta registrado");
//        }
        if (dto.getId() == null && productsRepository.existsByCodAndEnabledTrue(dto.getCod())) {
            throw new IllegalArgumentException("El código ya está registrado");
        }

    }

    private void validarYear(ProductDTO dto) {
        if (dto.getYear() != null && !dto.getYear().trim().isEmpty()) {
            try {
                int anio = Integer.parseInt(dto.getYear());
                int anioActual = java.time.LocalDate.now().getYear();
                if (anio < 1900 || anio > anioActual) {
                    throw new IllegalArgumentException("El año debe estar entre 1900 y " + anioActual);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El año debe ser un número válido.");
            }
        }
    }

    private void validarPrice(ProductDTO dto) {
        if (dto.getCostPrice() != null && dto.getSalePrice() != null) {
            if (dto.getSalePrice().compareTo(dto.getCostPrice()) < 0) {
                throw new IllegalArgumentException("El precio de venta no puede ser menor que el costo.");
            }
        }
    }

    private void validarSede(ProductDTO dto) {
        if (dto.getSede() == null || dto.getSede().trim().isEmpty()) {
            throw new IllegalArgumentException("La sede es obligatoria");
        }
        List<String> sedesValidas = Arrays.asList("Lima", "Chiclayo", "Trujillo", "Piura", "Arequipa");
        if (!sedesValidas.contains(dto.getSede())) {
            throw new IllegalArgumentException("La sede seleccionada no es válida");
        }
    }
}
