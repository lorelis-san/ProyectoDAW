package com.appWeb.cotizacion.dto.products;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class CategoryDTO {
    private Long id;

    @NotNull
    private String name;

    private String description;

    private Boolean enabled = true;
}
