package com.appWeb.cotizacion.dto.client;

import lombok.*;

//@Data // opcional si ya tienes @Getter y @Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String typeDocument;

    private String documentNumber;

    private String businessName;

    private String phoneNumber;

    private String email;

    private Boolean enabled = true;

}
