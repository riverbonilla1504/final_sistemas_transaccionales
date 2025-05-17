package com.example.gestionvehiculos.domain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoDto {
    private Long id;

    @NotBlank(message = "La marca es obligatoria")
    @Size(min = 2, max = 50, message = "La marca debe tener entre 2 y 50 caracteres")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(min = 2, max = 50, message = "El modelo debe tener entre 2 y 50 caracteres")
    private String modelo;

    @NotBlank(message = "La matrícula es obligatoria")
    @Pattern(regexp = "^[A-Z0-9]{5,10}$", message = "La matrícula debe tener entre 5 y 10 caracteres alfanuméricos")
    private String matricula;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1950, message = "El año debe ser posterior a 1950")
    @Max(value = 2100, message = "El año debe ser anterior a 2100")
    private Integer anio;

    @NotBlank(message = "El tipo de vehículo es obligatorio")
    private String tipo; // SEDAN, SUV, PICKUP, DEPORTIVO, etc.

    @NotBlank(message = "El color es obligatorio")
    @Size(min = 2, max = 30, message = "El color debe tener entre 2 y 30 caracteres")
    private String color;

    @NotNull(message = "El precio por día es obligatorio")
    @DecimalMin(value = "1.0", message = "El precio por día debe ser mayor a 1")
    private BigDecimal precioPorDia;

    private Boolean disponible = true;

    @Size(max = 500, message = "La descripción no debe exceder los 500 caracteres")
    private String descripcion;
}