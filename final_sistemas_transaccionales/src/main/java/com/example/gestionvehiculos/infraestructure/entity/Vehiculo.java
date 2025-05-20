package com.example.gestionvehiculos.infraestructure.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehiculos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La marca es obligatoria")
    @Size(min = 2, max = 50, message = "La marca debe tener entre 2 y 50 caracteres")
    @Column(nullable = false)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(min = 2, max = 50, message = "El modelo debe tener entre 2 y 50 caracteres")
    @Column(nullable = false)
    private String modelo;

    @NotBlank(message = "La matrícula es obligatoria")
    @Pattern(regexp = "^[A-Z0-9]{5,10}$", message = "La matrícula debe tener entre 5 y 10 caracteres alfanuméricos")
    @Column(nullable = false, unique = true)
    private String matricula;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1950, message = "El año debe ser posterior a 1950")
    @Max(value = 2100, message = "El año debe ser anterior a 2100")
    @Column(nullable = false)
    private Integer anio;

    @NotBlank(message = "El tipo de vehículo es obligatorio")
    @Column(nullable = false)
    private String tipo; // SEDAN, SUV, PICKUP, DEPORTIVO, etc.

    @NotBlank(message = "El color es obligatorio")
    @Size(min = 2, max = 30, message = "El color debe tener entre 2 y 30 caracteres")
    @Column(nullable = false)
    private String color;

    @NotNull(message = "El precio por día es obligatorio")
    @DecimalMin(value = "1.0", message = "El precio por día debe ser mayor a 1")
    @Column(nullable = false)
    private BigDecimal precioPorDia;

    @NotNull(message = "La disponibilidad es obligatoria")
    @Column(nullable = false)
    private Boolean disponible = true;

    @Size(max = 500, message = "La descripción no debe exceder los 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    private List<Alquiler> alquileres = new ArrayList<>();
}