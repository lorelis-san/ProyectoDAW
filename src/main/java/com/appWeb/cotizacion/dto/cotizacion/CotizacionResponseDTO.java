package com.appWeb.cotizacion.dto.cotizacion;

import com.appWeb.cotizacion.dto.client.ClientDTO;
import com.appWeb.cotizacion.dto.vehicle.VehicleDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CotizacionResponseDTO {
    private Long id;
    private String numeroCotizacion;
    private String userNombre;
    private String userApellido;
    private String usuarioModificadorNombre;
    private String usuarioModificadorApellido;
    private String estado;
    private LocalDate fecha;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private String observaciones;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private ClientDTO cliente;
    private VehicleDTO vehiculo;
    private List<DetalleCotizacionDTO> detalles;
}
