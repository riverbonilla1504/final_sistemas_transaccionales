package com.example.gestionvehiculos.infraestructure.mapper;

import com.example.gestionvehiculos.domain.dto.AlquilerDto;
import com.example.gestionvehiculos.domain.dto.IngresoMensualDto;
import com.example.gestionvehiculos.infraestructure.entity.Alquiler;
import com.example.gestionvehiculos.infraestructure.entity.Cliente;
import com.example.gestionvehiculos.infraestructure.entity.Vehiculo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlquilerMapper {

    public AlquilerDto toDto(Alquiler alquiler) {
        if (alquiler == null) {
            return null;
        }

        AlquilerDto dto = new AlquilerDto();
        dto.setId(alquiler.getId());

        if (alquiler.getCliente() != null) {
            dto.setClienteId(alquiler.getCliente().getId());
            dto.setNombreCliente(alquiler.getCliente().getNombre() + " " + alquiler.getCliente().getApellido());
        }

        if (alquiler.getVehiculo() != null) {
            dto.setVehiculoId(alquiler.getVehiculo().getId());
            dto.setDetalleVehiculo(alquiler.getVehiculo().getMarca() + " " + alquiler.getVehiculo().getModelo() + " ("
                    + alquiler.getVehiculo().getMatricula() + ")");
        }

        dto.setFechaInicio(alquiler.getFechaInicio());
        dto.setFechaFin(alquiler.getFechaFin());
        dto.setFechaCreacion(alquiler.getFechaCreacion());
        dto.setFechaDevolucion(alquiler.getFechaDevolucion());
        dto.setCostoTotal(alquiler.getCostoTotal());
        dto.setEstado(alquiler.getEstado());
        dto.setObservaciones(alquiler.getObservaciones());

        return dto;
    }

    public List<AlquilerDto> toDtoList(List<Alquiler> alquileres) {
        if (alquileres == null) {
            return new ArrayList<>();
        }

        return alquileres.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Alquiler toEntity(AlquilerDto dto, Cliente cliente, Vehiculo vehiculo) {
        if (dto == null) {
            return null;
        }

        Alquiler alquiler = new Alquiler();
        alquiler.setId(dto.getId());
        alquiler.setCliente(cliente);
        alquiler.setVehiculo(vehiculo);
        alquiler.setFechaInicio(dto.getFechaInicio());
        alquiler.setFechaFin(dto.getFechaFin());

        if (dto.getFechaCreacion() != null) {
            alquiler.setFechaCreacion(dto.getFechaCreacion());
        }

        alquiler.setFechaDevolucion(dto.getFechaDevolucion());

        // El costo total se calculará automáticamente mediante el método en la entidad
        if (dto.getCostoTotal() != null) {
            alquiler.setCostoTotal(dto.getCostoTotal());
        }

        if (dto.getEstado() != null) {
            alquiler.setEstado(dto.getEstado());
        }

        alquiler.setObservaciones(dto.getObservaciones());

        return alquiler;
    }

    public void updateEntityFromDto(AlquilerDto dto, Alquiler alquiler, Cliente cliente, Vehiculo vehiculo) {
        if (dto == null || alquiler == null) {
            return;
        }

        if (cliente != null) {
            alquiler.setCliente(cliente);
        }

        if (vehiculo != null) {
            alquiler.setVehiculo(vehiculo);
        }

        alquiler.setFechaInicio(dto.getFechaInicio());
        alquiler.setFechaFin(dto.getFechaFin());
        alquiler.setFechaDevolucion(dto.getFechaDevolucion());

        if (dto.getEstado() != null) {
            alquiler.setEstado(dto.getEstado());
        }

        alquiler.setObservaciones(dto.getObservaciones());
    }

    public IngresoMensualDto toIngresoMensualDto(Object[] data) {
        if (data == null || data.length < 4) {
            return null;
        }

        Integer anio = (Integer) data[0];
        Integer mes = (Integer) data[1];
        String tipoVehiculo = (String) data[2];
        BigDecimal ingresoTotal;

        if (data[3] instanceof BigDecimal) {
            ingresoTotal = (BigDecimal) data[3];
        } else if (data[3] instanceof Double) {
            ingresoTotal = BigDecimal.valueOf((Double) data[3]);
        } else {
            ingresoTotal = BigDecimal.ZERO;
        }

        return new IngresoMensualDto(anio, mes, tipoVehiculo, ingresoTotal);
    }

    public List<IngresoMensualDto> toIngresoMensualDtoList(List<Object[]> dataList) {
        if (dataList == null) {
            return new ArrayList<>();
        }

        return dataList.stream()
                .map(this::toIngresoMensualDto)
                .collect(Collectors.toList());
    }
}