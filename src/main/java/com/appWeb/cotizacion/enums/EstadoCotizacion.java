package com.appWeb.cotizacion.enums;

public enum EstadoCotizacion {
    MODIFICADA("Modificada"),
    ENVIADA("Enviada"),
    CREADA("Creada"),
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