package com.appWeb.cotizacion.model.vehicle;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle", indexes = {
        //validar existencia findByPlaca
        @Index(name = "idx_vehicle_placa", columnList = "placa", unique = true),
        //listar solo vehículos activos
        @Index(name = "idx_vehicle_enabled", columnList = "enabled")
})

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String placa;
    @Column(nullable = false)
    private String marca;
    @Column(nullable = false)
    private String modelo;
    @Column(nullable = false)
    private Integer year;

    private Boolean enabled = true;
}
