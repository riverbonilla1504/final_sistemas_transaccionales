package com.example.gestionvehiculos.infraestructure.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "alquileres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alquiler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    @NotNull(message = "El vehículo es obligatorio")
    private Vehiculo vehiculo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    @Column
    private LocalDate fechaDevolucion;

    @NotNull(message = "El costo total es obligatorio")
    @Column(nullable = false)
    private BigDecimal costoTotal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoAlquiler estado = EstadoAlquiler.PENDIENTE;

    @Size(max = 500, message = "Las observaciones no deben exceder los 500 caracteres")
    @Column(length = 500)
    private String observaciones;

    // Estado del alquiler
    public enum EstadoAlquiler {
        PENDIENTE, ACTIVO, COMPLETADO, CANCELADO
    }

    // Calcula los días de alquiler
    public long calcularDiasAlquiler() {
        return ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
    }

    // Calcula el costo total basado en los días y el precio del vehículo
    @PrePersist
    @PreUpdate
    public void calcularCostoTotal() {
        if (fechaInicio != null && fechaFin != null && vehiculo != null) {
            long dias = calcularDiasAlquiler();
            this.costoTotal = vehiculo.getPrecioPorDia().multiply(BigDecimal.valueOf(dias));
        }
    }
}