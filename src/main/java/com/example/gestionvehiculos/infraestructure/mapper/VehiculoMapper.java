package com.example.gestionvehiculos.infraestructure.mapper;

import com.example.gestionvehiculos.domain.dto.PromedioTipoVehiculoDto;
import com.example.gestionvehiculos.domain.dto.VehiculoDto;
import com.example.gestionvehiculos.infraestructure.entity.Vehiculo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VehiculoMapper {

    public VehiculoDto toDto(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return null;
        }

        return new VehiculoDto(
                vehiculo.getId(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getMatricula(),
                vehiculo.getAnio(),
                vehiculo.getTipo(),
                vehiculo.getColor(),
                vehiculo.getPrecioPorDia(),
                vehiculo.getDisponible(),
                vehiculo.getDescripcion());
    }

    public List<VehiculoDto> toDtoList(List<Vehiculo> vehiculos) {
        if (vehiculos == null) {
            return new ArrayList<>();
        }

        return vehiculos.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Vehiculo toEntity(VehiculoDto dto) {
        if (dto == null) {
            return null;
        }

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(dto.getId());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setMatricula(dto.getMatricula());
        vehiculo.setAnio(dto.getAnio());
        vehiculo.setTipo(dto.getTipo());
        vehiculo.setColor(dto.getColor());
        vehiculo.setPrecioPorDia(dto.getPrecioPorDia());

        // Solo establecemos la disponibilidad si no es nula en el DTO
        if (dto.getDisponible() != null) {
            vehiculo.setDisponible(dto.getDisponible());
        }

        vehiculo.setDescripcion(dto.getDescripcion());

        return vehiculo;
    }

    public void updateEntityFromDto(VehiculoDto dto, Vehiculo vehiculo) {
        if (dto == null || vehiculo == null) {
            return;
        }

        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setMatricula(dto.getMatricula());
        vehiculo.setAnio(dto.getAnio());
        vehiculo.setTipo(dto.getTipo());
        vehiculo.setColor(dto.getColor());
        vehiculo.setPrecioPorDia(dto.getPrecioPorDia());

        if (dto.getDisponible() != null) {
            vehiculo.setDisponible(dto.getDisponible());
        }

        vehiculo.setDescripcion(dto.getDescripcion());
    }

    public PromedioTipoVehiculoDto toPromedioTipoVehiculoDto(Object[] data) {
        if (data == null || data.length < 2) {
            return null;
        }

        String tipoVehiculo = (String) data[0];
        BigDecimal promedioDias;

        // Convertir el promedio a BigDecimal con 2 decimales
        if (data[1] instanceof Double) {
            promedioDias = BigDecimal.valueOf((Double) data[1])
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (data[1] instanceof BigDecimal) {
            promedioDias = ((BigDecimal) data[1]).setScale(2, RoundingMode.HALF_UP);
        } else {
            promedioDias = BigDecimal.ZERO;
        }

        return new PromedioTipoVehiculoDto(tipoVehiculo, promedioDias);
    }

    public List<PromedioTipoVehiculoDto> toPromedioTipoVehiculoDtoList(List<Object[]> dataList) {
        if (dataList == null) {
            return new ArrayList<>();
        }

        return dataList.stream()
                .map(this::toPromedioTipoVehiculoDto)
                .collect(Collectors.toList());
    }
}