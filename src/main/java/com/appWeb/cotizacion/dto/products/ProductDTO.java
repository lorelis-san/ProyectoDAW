package com.appWeb.cotizacion.dto.products;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;

    @NotNull
    private String name;

    private String cod;

    private String description;

    private String brand; //marca

    private String model;

    private String year;

    private BigDecimal costPrice;

    private BigDecimal dealerPrice;

    private BigDecimal salePrice;

    private Integer stock;

    private String imageUrl;

    @NotNull
    private Long categoryProductId;

    @NotNull
    private Long supplierProductId;

    private String categoryName;

    private String supplierName;

    @NotNull
    private String sede;

    private Boolean enabled = true;
}
