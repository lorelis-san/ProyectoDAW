package com.appWeb.cotizacion.service.product.implement;

import com.appWeb.cotizacion.dto.products.CategoryDTO;
import com.appWeb.cotizacion.model.productos.Category;
import com.appWeb.cotizacion.repository.productos.CategoryRepository;
import com.appWeb.cotizacion.service.product.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category convertToEntity(CategoryDTO dto){
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setEnabled(dto.getEnabled());
        return category;
    }

    private CategoryDTO convertToDTO(Category category){
        CategoryDTO dto= new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setEnabled(category.getEnabled());
        return dto;
    }

    @Override
    public ResponseEntity<Map<String, Object>> saveCategory(CategoryDTO categoryDTO) {
        Map<String, Object> res = new HashMap<>();
        String nombre = categoryDTO.getName().trim().toLowerCase();
        Category existing = categoryRepository.findByName(nombre);

        if (existing != null) {
            res.put("mensaje", "Ya existe una categoría con ese nombre");
            res.put("status", HttpStatus.BAD_REQUEST);
            res.put("fecha", new Date());
            return ResponseEntity.badRequest().body(res);
        }

        categoryRepository.save(convertToEntity(categoryDTO));
        res.put("mensaje", "Categoría guardada correctamente");
        res.put("status", HttpStatus.CREATED);
        res.put("fecha", new Date());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllCategory() {
        Map<String, Object> res = new HashMap<>();
        List<Category> lista = categoryRepository.findByEnabledTrue();

        if (lista.isEmpty()) {
            res.put("mensaje", "No hay categorías habilitadas");
            res.put("status", HttpStatus.NOT_FOUND);
            res.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        List<CategoryDTO> dtos = lista.stream().map(this::convertToDTO).toList();
        res.put("mensaje", "Lista de categorías");
        res.put("data", dtos);
        res.put("status", HttpStatus.OK);
        res.put("fecha", new Date());
        return ResponseEntity.ok(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getCategoryById(Long id) {
        Map<String, Object> res = new HashMap<>();
        Optional<Category> categoryOptional = categoryRepository.findById(id);

        if (categoryOptional.isEmpty()) {
            res.put("mensaje", "Categoría no encontrada");
            res.put("status", HttpStatus.NOT_FOUND);
            res.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        res.put("mensaje", "Categoría encontrada");
        res.put("data", convertToDTO(categoryOptional.get()));
        res.put("status", HttpStatus.OK);
        res.put("fecha", new Date());
        return ResponseEntity.ok(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateCategory(CategoryDTO categoryDTO) {
        Map<String, Object> res = new HashMap<>();

        if (categoryDTO.getId() == null) {
            res.put("mensaje", "ID de categoría es requerido");
            res.put("status", HttpStatus.BAD_REQUEST);
            res.put("fecha", new Date());
            return ResponseEntity.badRequest().body(res);
        }

        Optional<Category> categoryOptional = categoryRepository.findById(categoryDTO.getId());

        if (categoryOptional.isEmpty()) {
            res.put("mensaje", "Categoría no encontrada");
            res.put("status", HttpStatus.NOT_FOUND);
            res.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        Category existingCategory = categoryRepository.findByName(categoryDTO.getName());
        if (existingCategory != null && !existingCategory.getId().equals(categoryDTO.getId())) {
            res.put("mensaje", "Ya existe una categoría con ese nombre");
            res.put("status", HttpStatus.BAD_REQUEST);
            res.put("fecha", new Date());
            return ResponseEntity.badRequest().body(res);
        }

        Category category = categoryOptional.get();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        categoryRepository.save(category);
        res.put("mensaje", "Categoría actualizada correctamente");
        res.put("status", HttpStatus.OK);
        res.put("fecha", new Date());
        return ResponseEntity.ok(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteCategory(Long id) {
        Map<String, Object> res = new HashMap<>();
        Optional<Category> categoryOptional = categoryRepository.findById(id);

        if (categoryOptional.isEmpty()) {
            res.put("mensaje", "Categoría no encontrada");
            res.put("status", HttpStatus.NOT_FOUND);
            res.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        Category category = categoryOptional.get();
        category.setEnabled(false);
        categoryRepository.save(category);

        res.put("mensaje", "Categoría deshabilitada correctamente");
        res.put("status", HttpStatus.NO_CONTENT);
        res.put("fecha", new Date());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getByName(String name) {
        Map<String, Object> res = new HashMap<>();
        Category c = categoryRepository.findByName(name.trim().toLowerCase());
        if (c == null || !Boolean.TRUE.equals(c.getEnabled())) {
            res.put("mensaje", "No encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
        res.put("data", convertToDTO(c));
        return ResponseEntity.ok(res);
    }
}
