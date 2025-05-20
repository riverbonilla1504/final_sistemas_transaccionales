package com.example.gestionvehiculos.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngresoMensualDto {
    private Integer anio;
    private Integer mes;
    private String tipoVehiculo;
    private BigDecimal ingresoTotal;
}