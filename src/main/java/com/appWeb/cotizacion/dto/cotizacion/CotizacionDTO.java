package com.appWeb.cotizacion.dto.cotizacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionDTO {
    private String numeroCotizacion;
    private Long clienteId;
    private Long vehiculoId;
    private List<DetalleCotizacionDTO> detalles;
    private String observaciones;


}
