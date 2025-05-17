package com.example.gestionvehiculos.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteAlquileresDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String documento;
    private String email;
    private Long cantidadAlquileres;
}