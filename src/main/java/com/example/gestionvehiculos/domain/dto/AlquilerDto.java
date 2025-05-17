package com.example.gestionvehiculos.domain.dto;

import com.example.gestionvehiculos.infraestructure.entity.Alquiler.EstadoAlquiler;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlquilerDto {
    private Long id;

    @NotNull(message = "El ID de cliente es obligatorio")
    private Long clienteId;

    private String nombreCliente; // Para mostrar información adicional del cliente

    @NotNull(message = "El ID de vehículo es obligatorio")
    private Long vehiculoId;

    private String detalleVehiculo; // Para mostrar información adicional del vehículo (Marca, Modelo, etc.)

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser en el futuro")
    private LocalDate fechaFin;

    private LocalDate fechaCreacion;

    private LocalDate fechaDevolucion;

    private BigDecimal costoTotal;

    private EstadoAlquiler estado;

    @Size(max = 500, message = "Las observaciones no deben exceder los 500 caracteres")
    private String observaciones;

    // Método para validar que la fecha de fin sea posterior a la fecha de inicio
    public boolean esFechaFinValida() {
        if (fechaInicio != null && fechaFin != null) {
            return fechaFin.isAfter(fechaInicio) || fechaFin.isEqual(fechaInicio);
        }
        return true; // Si alguna fecha es nula, la validación se dejará a otras anotaciones
    }
}