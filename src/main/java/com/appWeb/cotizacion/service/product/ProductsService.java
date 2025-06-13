package com.appWeb.cotizacion.service.product;

import com.appWeb.cotizacion.dto.products.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductsService {
    ResponseEntity<Map<String, Object>> saveProduct(ProductDTO dto, MultipartFile imageFile);
    ResponseEntity<Map<String, Object>> getAllProducts();
    ResponseEntity<Map<String, Object>> getProductById(Long id);
    ResponseEntity<Map<String, Object>> updateProduct(ProductDTO dto, MultipartFile imageFile);
    ResponseEntity<Map<String, Object>> deleteProduct(Long id);
    ResponseEntity<Map<String, Object>> buscarPorNombreOCodigo(String termino);
    ResponseEntity<Map<String, Object>> getProductsEnabled();
}
