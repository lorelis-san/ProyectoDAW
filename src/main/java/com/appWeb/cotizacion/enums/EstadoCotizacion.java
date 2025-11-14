package com.appWeb.cotizacion.enums;

public enum EstadoCotizacion {
    PENDIENTE("Pendiente"),
    MODIFICADA("Modificada"),
    ENVIADA("Enviada"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    ELIMINADA("Eliminada"),
    VENCIDA("Vencida");

    private final String descripcion;

    EstadoCotizacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}