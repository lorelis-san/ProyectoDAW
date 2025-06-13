package com.appWeb.cotizacion.model.cotizacion;


import com.appWeb.cotizacion.enums.EstadoCotizacion;
import com.appWeb.cotizacion.model.client.Client;
import com.appWeb.cotizacion.model.usuario.User;
import com.appWeb.cotizacion.model.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cotizaciones", indexes = {
        @Index(name = "idx_numero_cotizacion", columnList = "numero_cotizacion", unique = true),
        @Index(name = "idx_estado", columnList = "estado"),
        @Index(name = "idx_fecha", columnList = "fecha"),
        @Index(name = "idx_cliente_id", columnList = "cliente_id"),
        @Index(name = "idx_vehiculo_id", columnList = "vehiculo_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_user_modificador_id", columnList = "user_modificador_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cotizacion", unique = true, nullable = false, length = 20)
    private String numeroCotizacion;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Client cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehicle vehiculo;

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetalleCotizacion> detalles = new ArrayList<>();

    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "igv", precision = 10, scale = 2, nullable = false)
    private BigDecimal igv;

    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCotizacion estado;

    @Column(name = "observaciones", length = 500)
    private String observaciones;


    @ManyToOne
    private User user;

    @ManyToOne
    private User userModificador;

    // Método para calcular totales
    public void calcularTotales() {
        this.subtotal = detalles.stream()
                .map(DetalleCotizacion::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.igv = subtotal.multiply(new BigDecimal("0"));
        this.total = subtotal.add(igv);
    }

    // Agregar detalle
    public void agregarDetalle(DetalleCotizacion detalle) {
        detalles.add(detalle);
        detalle.setCotizacion(this);
    }

    // Asignar fecha automáticamente antes de persistir
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();

        }
        if (fecha == null) {
            fecha = LocalDate.now();
        }
    }
}
