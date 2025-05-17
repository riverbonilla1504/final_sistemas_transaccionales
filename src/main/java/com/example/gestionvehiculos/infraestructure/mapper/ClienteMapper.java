package com.example.gestionvehiculos.infraestructure.mapper;

import com.example.gestionvehiculos.domain.dto.ClienteAlquileresDto;
import com.example.gestionvehiculos.domain.dto.ClienteDto;
import com.example.gestionvehiculos.infraestructure.entity.Cliente;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClienteMapper {

    public ClienteDto toDto(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        return new ClienteDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getDocumento(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getFechaNacimiento(),
                cliente.getDireccion(),
                cliente.getFechaRegistro());
    }

    public List<ClienteDto> toDtoList(List<Cliente> clientes) {
        if (clientes == null) {
            return new ArrayList<>();
        }

        return clientes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Cliente toEntity(ClienteDto dto) {
        if (dto == null) {
            return null;
        }

        Cliente cliente = new Cliente();
        cliente.setId(dto.getId());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setDocumento(dto.getDocumento());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());
        cliente.setDireccion(dto.getDireccion());

        // Solo establecemos la fecha de registro si no es nula en el DTO
        if (dto.getFechaRegistro() != null) {
            cliente.setFechaRegistro(dto.getFechaRegistro());
        }

        return cliente;
    }

    public void updateEntityFromDto(ClienteDto dto, Cliente cliente) {
        if (dto == null || cliente == null) {
            return;
        }

        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setDocumento(dto.getDocumento());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());
        cliente.setDireccion(dto.getDireccion());
    }

    public ClienteAlquileresDto toClienteAlquileresDto(Cliente cliente, Long cantidadAlquileres) {
        if (cliente == null) {
            return null;
        }

        return new ClienteAlquileresDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getDocumento(),
                cliente.getEmail(),
                cantidadAlquileres);
    }
}