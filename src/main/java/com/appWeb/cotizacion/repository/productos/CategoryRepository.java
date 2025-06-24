package com.appWeb.cotizacion.repository.productos;

import com.appWeb.cotizacion.model.productos.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
    List<Category> findByEnabledTrue();
    @Query("SELECT c FROM Category c WHERE c.enabled = true AND LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Category> buscarPorNombre(@Param("term") String term);

}
