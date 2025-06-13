package com.appWeb.cotizacion.service.product;
import com.appWeb.cotizacion.dto.products.CategoryDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    ResponseEntity<Map<String, Object>> saveCategory(CategoryDTO categoryDTO);
    ResponseEntity<Map<String, Object>> getCategoryById(Long id);
    ResponseEntity<Map<String, Object>> updateCategory(CategoryDTO categoryDTO);
    ResponseEntity<Map<String, Object>> deleteCategory(Long id);

    ResponseEntity<Map<String, Object>> getByName(String name);
    ResponseEntity<Map<String, Object>> getAllCategory();

}
