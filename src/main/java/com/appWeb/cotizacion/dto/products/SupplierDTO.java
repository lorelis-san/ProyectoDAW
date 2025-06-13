package com.appWeb.cotizacion.dto.products;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @NotBlank(message = "El RUC es obligatorio")
    @Size(max = 11, message = "El RUC no puede exceder 11 caracteres")
    private String ruc;

    @Email(message = "Email debe tener formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 9, message = "El teléfono no puede exceder 9 caracteres")
    private String phone;

    private Boolean enabled = true;
}
