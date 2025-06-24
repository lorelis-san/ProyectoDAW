package com.appWeb.cotizacion.controller;

import com.appWeb.cotizacion.dto.products.CategoryDTO;
import com.appWeb.cotizacion.service.product.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Listar todas las categorías habilitadas
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarCategorias() {
        return categoryService.getAllCategory();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarCategoriaPorId(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // Agregar categoría
    @PostMapping
    public ResponseEntity<Map<String, Object>> agregarCategoria(@RequestBody CategoryDTO categoryDTO) {
        return categoryService.saveCategory(categoryDTO);
    }

    // Editar categoría
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> editarCategoria(@RequestBody CategoryDTO categoryDTO, @PathVariable Long id) {
        categoryDTO.setId(id);
        return categoryService.updateCategory(categoryDTO);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(@RequestParam String termino) {
        return categoryService.getByNameSearch(termino);
    }

    //  Eliminar (inhabilitar) categoría
    @PutMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, Object>> eliminarCategoria(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
    // se elimina la categoría, pero no deja que otra categoría lleve el mismo nombre
}
