package com.appWeb.cotizacion.repository.productos;

import com.appWeb.cotizacion.model.productos.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Buscar por RUC
    Optional<Supplier> findByRuc(String ruc);
    boolean existsByRuc(String ruc);
    List<Supplier> findByEnabledTrue();

}
