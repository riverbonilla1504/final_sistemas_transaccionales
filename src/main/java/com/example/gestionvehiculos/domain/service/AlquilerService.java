package com.example.gestionvehiculos.domain.service;

import com.example.gestionvehiculos.domain.dto.AlquilerDto;
import com.example.gestionvehiculos.domain.dto.IngresoMensualDto;
import com.example.gestionvehiculos.domain.repository.AlquilerRepository;
import com.example.gestionvehiculos.domain.repository.ClienteRepository;
import com.example.gestionvehiculos.domain.repository.VehiculoRepository;
import com.example.gestionvehiculos.infraestructure.entity.Alquiler;
import com.example.gestionvehiculos.infraestructure.entity.Alquiler.EstadoAlquiler;
import com.example.gestionvehiculos.infraestructure.entity.Cliente;
import com.example.gestionvehiculos.infraestructure.entity.Vehiculo;
import com.example.gestionvehiculos.infraestructure.mapper.AlquilerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlquilerService {

    private final AlquilerRepository alquilerRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final AlquilerMapper alquilerMapper;

    public AlquilerService(AlquilerRepository alquilerRepository,
            ClienteRepository clienteRepository,
            VehiculoRepository vehiculoRepository,
            AlquilerMapper alquilerMapper) {
        this.alquilerRepository = alquilerRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.alquilerMapper = alquilerMapper;
    }

    @Transactional(readOnly = true)
    public List<AlquilerDto> findAll() {
        List<Alquiler> alquileres = new ArrayList<>();
        alquilerRepository.findAll().forEach(alquileres::add);
        return alquilerMapper.toDtoList(alquileres);
    }

    @Transactional(readOnly = true)
    public Optional<AlquilerDto> findById(Long id) {
        return alquilerRepository.findById(id)
                .map(alquilerMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<AlquilerDto> findByClienteId(Long clienteId) {
        return alquilerMapper.toDtoList(
                alquilerRepository.findByClienteId(clienteId));
    }

    @Transactional(readOnly = true)
    public List<AlquilerDto> findByVehiculoId(Long vehiculoId) {
        return alquilerMapper.toDtoList(
                alquilerRepository.findByVehiculoId(vehiculoId));
    }

    @Transactional(readOnly = true)
    public List<AlquilerDto> findByEstado(EstadoAlquiler estado) {
        return alquilerMapper.toDtoList(
                alquilerRepository.findByEstado(estado));
    }

    @Transactional(readOnly = true)
    public List<AlquilerDto> findHistorialAlquileresCliente(Long clienteId) {
        return alquilerMapper.toDtoList(
                alquilerRepository.findHistorialAlquileresCliente(clienteId));
    }

    @Transactional(readOnly = true)
    public List<AlquilerDto> findAlquileresVencidos() {
        return alquilerMapper.toDtoList(
                alquilerRepository.findAlquileresVencidos());
    }

    @Transactional
    public AlquilerDto save(AlquilerDto alquilerDto) {
        // Validamos que el cliente exista
        Cliente cliente = clienteRepository.findById(alquilerDto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        // Validamos que el vehículo exista
        Vehiculo vehiculo = vehiculoRepository.findById(alquilerDto.getVehiculoId())
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        // Validamos que el vehículo esté disponible
        if (!vehiculo.getDisponible()) {
            throw new IllegalArgumentException("El vehículo no está disponible para alquiler");
        }

        // Validamos que la fecha de fin sea posterior a la fecha de inicio
        if (alquilerDto.getFechaInicio().isAfter(alquilerDto.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // Validamos que el vehículo no esté alquilado en ese período
        Long alquileresActivos = alquilerRepository.countAlquileresActivosEnFechas(
                alquilerDto.getVehiculoId(),
                alquilerDto.getFechaInicio(),
                alquilerDto.getFechaFin());

        if (alquileresActivos > 0) {
            throw new IllegalArgumentException("El vehículo ya está alquilado durante el período seleccionado");
        }

        // Establecemos el estado del alquiler
        alquilerDto.setEstado(EstadoAlquiler.PENDIENTE);

        // Creamos el alquiler
        Alquiler alquiler = alquilerMapper.toEntity(alquilerDto, cliente, vehiculo);

        // Calculamos el costo total
        alquiler.calcularCostoTotal();

        // Actualizamos la disponibilidad del vehículo
        vehiculo.setDisponible(false);
        vehiculoRepository.save(vehiculo);

        // Guardamos el alquiler
        alquiler = alquilerRepository.save(alquiler);

        return alquilerMapper.toDto(alquiler);
    }

    @Transactional
    public Optional<AlquilerDto> update(Long id, AlquilerDto alquilerDto) {
        if (!alquilerRepository.existsById(id)) {
            return Optional.empty();
        }

        return alquilerRepository.findById(id)
                .map(alquiler -> {
                    // Obtenemos el cliente y vehículo actuales
                    Cliente cliente = alquiler.getCliente();
                    Vehiculo vehiculo = alquiler.getVehiculo();

                    // Si se cambia de cliente, verificamos que exista
                    if (!cliente.getId().equals(alquilerDto.getClienteId())) {
                        cliente = clienteRepository.findById(alquilerDto.getClienteId())
                                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
                    }

                    // Si se cambia de vehículo, verificamos que exista y esté disponible
                    if (!vehiculo.getId().equals(alquilerDto.getVehiculoId())) {
                        vehiculo = vehiculoRepository.findById(alquilerDto.getVehiculoId())
                                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

                        if (!vehiculo.getDisponible() && !vehiculo.getId().equals(alquiler.getVehiculo().getId())) {
                            throw new IllegalArgumentException("El nuevo vehículo no está disponible para alquiler");
                        }
                    }

                    // Validamos que la fecha de fin sea posterior a la fecha de inicio
                    if (alquilerDto.getFechaInicio().isAfter(alquilerDto.getFechaFin())) {
                        throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
                    }

                    // Si se cambia de vehículo o de fechas, validamos que no haya conflictos
                    if (!vehiculo.getId().equals(alquiler.getVehiculo().getId()) ||
                            !alquilerDto.getFechaInicio().equals(alquiler.getFechaInicio()) ||
                            !alquilerDto.getFechaFin().equals(alquiler.getFechaFin())) {

                        Long alquileresActivos = alquilerRepository.countAlquileresActivosEnFechas(
                                vehiculo.getId(),
                                alquilerDto.getFechaInicio(),
                                alquilerDto.getFechaFin());

                        // Restamos 1 si es el mismo ID para no contar el propio alquiler
                        if (vehiculo.getId().equals(alquiler.getVehiculo().getId())) {
                            alquileresActivos--;
                        }

                        if (alquileresActivos > 0) {
                            throw new IllegalArgumentException(
                                    "El vehículo ya está alquilado durante el período seleccionado");
                        }
                    }

                    // Actualizamos el alquiler
                    alquilerMapper.updateEntityFromDto(alquilerDto, alquiler, cliente, vehiculo);

                    // Recalculamos el costo total
                    alquiler.calcularCostoTotal();

                    // Actualizamos la disponibilidad de los vehículos
                    if (!vehiculo.getId().equals(alquiler.getVehiculo().getId())) {
                        // Liberamos el vehículo anterior
                        Vehiculo vehículoAnterior = alquiler.getVehiculo();
                        vehículoAnterior.setDisponible(true);
                        vehiculoRepository.save(vehículoAnterior);

                        // Bloqueamos el nuevo vehículo
                        vehiculo.setDisponible(false);
                        vehiculoRepository.save(vehiculo);
                    }

                    Alquiler updatedAlquiler = alquilerRepository.save(alquiler);
                    return alquilerMapper.toDto(updatedAlquiler);
                });
    }

    @Transactional
    public Optional<AlquilerDto> completarAlquiler(Long id, LocalDate fechaDevolucion) {
        return alquilerRepository.findById(id)
                .map(alquiler -> {
                    if (alquiler.getEstado() != EstadoAlquiler.ACTIVO) {
                        throw new IllegalArgumentException("Solo se pueden completar alquileres activos");
                    }

                    // Establecemos la fecha de devolución
                    alquiler.setFechaDevolucion(fechaDevolucion);
                    alquiler.setEstado(EstadoAlquiler.COMPLETADO);

                    // Liberamos el vehículo
                    Vehiculo vehiculo = alquiler.getVehiculo();
                    vehiculo.setDisponible(true);
                    vehiculoRepository.save(vehiculo);

                    Alquiler updatedAlquiler = alquilerRepository.save(alquiler);
                    return alquilerMapper.toDto(updatedAlquiler);
                });
    }

    @Transactional
    public Optional<AlquilerDto> cancelarAlquiler(Long id) {
        return alquilerRepository.findById(id)
                .map(alquiler -> {
                    if (alquiler.getEstado() == EstadoAlquiler.COMPLETADO) {
                        throw new IllegalArgumentException("No se pueden cancelar alquileres completados");
                    }

                    alquiler.setEstado(EstadoAlquiler.CANCELADO);

                    // Liberamos el vehículo
                    Vehiculo vehiculo = alquiler.getVehiculo();
                    vehiculo.setDisponible(true);
                    vehiculoRepository.save(vehiculo);

                    Alquiler updatedAlquiler = alquilerRepository.save(alquiler);
                    return alquilerMapper.toDto(updatedAlquiler);
                });
    }

    @Transactional
    public Optional<AlquilerDto> activarAlquiler(Long id) {
        return alquilerRepository.findById(id)
                .map(alquiler -> {
                    if (alquiler.getEstado() != EstadoAlquiler.PENDIENTE) {
                        throw new IllegalArgumentException("Solo se pueden activar alquileres pendientes");
                    }

                    alquiler.setEstado(EstadoAlquiler.ACTIVO);
                    Alquiler updatedAlquiler = alquilerRepository.save(alquiler);
                    return alquilerMapper.toDto(updatedAlquiler);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        if (!alquilerRepository.existsById(id)) {
            return false;
        }

        // Obtenemos el alquiler para liberar el vehículo si es necesario
        alquilerRepository.findById(id).ifPresent(alquiler -> {
            if (alquiler.getEstado() == EstadoAlquiler.PENDIENTE || alquiler.getEstado() == EstadoAlquiler.ACTIVO) {
                Vehiculo vehiculo = alquiler.getVehiculo();
                vehiculo.setDisponible(true);
                vehiculoRepository.save(vehiculo);
            }
        });

        alquilerRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<IngresoMensualDto> findIngresosTotalesPorMesYTipoVehiculo() {
        return alquilerMapper.toIngresoMensualDtoList(
                vehiculoRepository.findIngresosTotalesPorMesYTipoVehiculo());
    }
}