package com.appWeb.cotizacion.dto.vehicle;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleDTO {

    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer year;
    private Boolean enabled = true;

    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

}
