package com.appWeb.cotizacion.controller;

import com.appWeb.cotizacion.dto.products.ProductDTO;
import com.appWeb.cotizacion.service.product.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductsController {

    @Autowired
    private ProductsService productsService;


    @GetMapping
    public ResponseEntity<Map<String, Object>> listarProductos() {
        return productsService.getAllProducts();
    }


    @GetMapping("/habilitados")
    public ResponseEntity<Map<String, Object>> listarProductosHabilitados() {
        return productsService.getProductsEnabled();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerProducto(@PathVariable Long id) {

        return productsService.getProductByIdEnabledTrue(id);
    }


    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(@RequestParam String termino) {

        return productsService.buscarPorNombreOCodigoEnabledTrue(termino);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> guardarProducto(
            @ModelAttribute ProductDTO producto,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        return productsService.saveProduct(producto, imageFile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarProducto(
            @PathVariable Long id,
            @ModelAttribute ProductDTO producto,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        producto.setId(id);
        return productsService.updateProduct(producto, imageFile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarProducto(@PathVariable Long id) {
        return productsService.deleteProduct(id);
    }


    @GetMapping("/mas-vendidos")
    public ResponseEntity<?> productosMasVendidos(
            @RequestParam(defaultValue = "5") Integer top
    ) {

        List<Object[]> lista = productsService.productosMasVendidos(top);

        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : lista) {
            Map<String, Object> item = new HashMap<>();
            item.put("productoId", row[0]);
            item.put("nombreProducto", row[1]);
            item.put("cantidadVendida", row[2]);
            item.put("montoGenerado", row[3]);
            data.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Productos más vendidos");
        response.put("data", data);
        response.put("status", HttpStatus.OK);
        response.put("fecha", new Date());

        return ResponseEntity.ok(response);
    }
}
