package com.appWeb.cotizacion.repository.productos;

import com.appWeb.cotizacion.model.productos.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Buscar por RUC
    Optional<Supplier> findByRuc(String ruc);
    boolean existsByRuc(String ruc);
    List<Supplier> findByEnabledTrue();
    @Query("SELECT s FROM Supplier s WHERE s.enabled = true AND (LOWER(s.ruc) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(s.name) LIKE LOWER(CONCAT('%', :term, '%')))")
    List<Supplier> buscarPorRucORazonSocial(@Param("term") String term);


}
