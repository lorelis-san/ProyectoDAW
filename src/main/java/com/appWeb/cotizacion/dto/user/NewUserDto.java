package com.appWeb.cotizacion.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    public String nombre;
    public String apellido;
    public String username;
    public String email;
    public String password;

}
