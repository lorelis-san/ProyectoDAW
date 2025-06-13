package com.appWeb.cotizacion.model.client;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "client", indexes = {
        //buscar por documentNumber
        @Index(name = "idx_document_number", columnList = "documentNumber"),
        //listar solo activos
        @Index(name = "idx_client_enabled", columnList = "enabled")
})
@Data
@NoArgsConstructor
@AllArgsConstructor


public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @Column(nullable = false)
    private String typeDocument;

    @Column(nullable = false, unique = true)
    private String documentNumber;  // Unique field for document (DNI or RUC)

    @Column(nullable = true)
    private String businessName;  // Only applies if it's a RUC

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    private Boolean enabled = true;

}
