package com.appWeb.cotizacion.model.productos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_cod", columnList = "cod", unique = true),
        @Index(name = "idx_products_enabled", columnList = "enabled"),
        @Index(name = "idx_products_name", columnList = "name")
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product")
    private Long id;
    @Column(name = "cod", nullable = false, unique = true)
    private String cod; // como 00FRR23
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "brand") //marca
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "year")
    private String year;

    @Column(name = "sede", nullable = false, length = 50)
    private String sede;
    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "cost_dealer", precision = 10, scale = 2)
    private BigDecimal costDealer;

    @Column(name = "sale_price", precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private Category categoryproduct;

    @ManyToOne
    @JoinColumn(name = "id_supplier", nullable = false)
    private Supplier supplierProduct;

    private Boolean enabled = true;
}
