package com.example.gestionvehiculos.domain.service;

import com.example.gestionvehiculos.domain.dto.PromedioTipoVehiculoDto;
import com.example.gestionvehiculos.domain.dto.VehiculoDto;
import com.example.gestionvehiculos.domain.repository.VehiculoRepository;
import com.example.gestionvehiculos.infraestructure.entity.Vehiculo;
import com.example.gestionvehiculos.infraestructure.mapper.VehiculoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final VehiculoMapper vehiculoMapper;

    public VehiculoService(VehiculoRepository vehiculoRepository, VehiculoMapper vehiculoMapper) {
        this.vehiculoRepository = vehiculoRepository;
        this.vehiculoMapper = vehiculoMapper;
    }

    @Transactional(readOnly = true)
    public List<VehiculoDto> findAll() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        vehiculoRepository.findAll().forEach(vehiculos::add);
        return vehiculoMapper.toDtoList(vehiculos);
    }

    @Transactional(readOnly = true)
    public Optional<VehiculoDto> findById(Long id) {
        return vehiculoRepository.findById(id)
                .map(vehiculoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<VehiculoDto> findByMatricula(String matricula) {
        return vehiculoRepository.findByMatricula(matricula)
                .map(vehiculoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<VehiculoDto> findByMarcaContaining(String marca) {
        return vehiculoMapper.toDtoList(
                vehiculoRepository.findByMarcaContainingIgnoreCase(marca));
    }

    @Transactional(readOnly = true)
    public List<VehiculoDto> findByTipoContaining(String tipo) {
        return vehiculoMapper.toDtoList(
                vehiculoRepository.findByTipoContainingIgnoreCase(tipo));
    }

    @Transactional(readOnly = true)
    public List<VehiculoDto> findByDisponible(Boolean disponible) {
        return vehiculoMapper.toDtoList(
                vehiculoRepository.findByDisponible(disponible));
    }

    @Transactional
    public VehiculoDto save(VehiculoDto vehiculoDto) {
        // Verificamos si existe otro vehículo con la misma matrícula
        if (vehiculoDto.getId() == null && vehiculoRepository.findByMatricula(vehiculoDto.getMatricula()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un vehículo con esta matrícula");
        }

        Vehiculo vehiculo = vehiculoMapper.toEntity(vehiculoDto);
        vehiculo = vehiculoRepository.save(vehiculo);
        return vehiculoMapper.toDto(vehiculo);
    }

    @Transactional
    public Optional<VehiculoDto> update(Long id, VehiculoDto vehiculoDto) {
        if (!vehiculoRepository.existsById(id)) {
            return Optional.empty();
        }

        // Verificamos si existe otro vehículo con la misma matrícula
        Optional<Vehiculo> vehiculoByMatricula = vehiculoRepository.findByMatricula(vehiculoDto.getMatricula());
        if (vehiculoByMatricula.isPresent() && !vehiculoByMatricula.get().getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe otro vehículo con esta matrícula");
        }

        return vehiculoRepository.findById(id)
                .map(vehiculo -> {
                    vehiculoMapper.updateEntityFromDto(vehiculoDto, vehiculo);
                    Vehiculo updatedVehiculo = vehiculoRepository.save(vehiculo);
                    return vehiculoMapper.toDto(updatedVehiculo);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        if (!vehiculoRepository.existsById(id)) {
            return false;
        }
        vehiculoRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<VehiculoDto> findVehiculosSinAlquileresUltimos30Dias() {
        // Obtener la fecha de hace 30 días
        LocalDate fechaHace30Dias = LocalDate.now().minusDays(30);

        return vehiculoMapper.toDtoList(
                vehiculoRepository.findVehiculosSinAlquileresEnUltimos30Dias(fechaHace30Dias));
    }

    @Transactional(readOnly = true)
    public List<PromedioTipoVehiculoDto> findPromedioDuracionPorTipoVehiculo() {
        return vehiculoMapper.toPromedioTipoVehiculoDtoList(
                vehiculoRepository.findPromedioDuracionPorTipoVehiculo());
    }
}