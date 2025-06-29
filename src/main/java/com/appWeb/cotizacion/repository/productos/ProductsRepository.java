package com.appWeb.cotizacion.repository.productos;

import com.appWeb.cotizacion.model.productos.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository

public interface ProductsRepository extends JpaRepository<Products, Long> {
    boolean existsByCod(String cod);
    Optional<Products> findByCod(String cod);
    List<Products> findByNameContainingIgnoreCaseOrCodContainingIgnoreCase(String name, String cod);
    List<Products> findByEnabledTrue();
    Optional<Products> findByIdAndEnabledTrue(Long id);
    boolean existsByCodAndEnabledTrue(String cod);
    @Query("SELECT p FROM Products p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :termino, '%')) " +
            "OR LOWER(p.cod) LIKE LOWER(CONCAT('%', :termino, '%'))) AND p.enabled = true")
    List<Products> buscarPorNombreOCodigo(@Param("termino") String termino);

}
