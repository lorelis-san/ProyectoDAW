package com.appWeb.cotizacion.model.productos;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "category", indexes = {
        //validar duplicados
        @Index(name = "idx_category_name", columnList = "name", unique = true),
        //mostrar solo activos
        @Index(name = "idx_category_enabled", columnList = "enabled")
})

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    private Boolean enabled = true;
}
