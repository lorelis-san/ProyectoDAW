package com.appWeb.cotizacion.repository.client;

import com.appWeb.cotizacion.model.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByDocumentNumber(String documentNumber);
    List<Client> findByEnabledTrue();
    @Query("""
    SELECT c FROM Client c
    WHERE c.enabled = true
      AND (
        LOWER(c.documentNumber) LIKE LOWER(CONCAT('%', :term, '%')) OR
        LOWER(c.firstName) LIKE LOWER(CONCAT('%', :term, '%')) OR
        LOWER(c.lastName) LIKE LOWER(CONCAT('%', :term, '%')) OR
        LOWER(c.businessName) LIKE LOWER(CONCAT('%', :term, '%'))
      )
    """)
    List<Client> buscarPorDocumentoONombre(@Param("term") String term);


}
