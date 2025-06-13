package com.appWeb.cotizacion.repository.productos;

import com.appWeb.cotizacion.model.productos.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
    List<Category> findByEnabledTrue();
}
