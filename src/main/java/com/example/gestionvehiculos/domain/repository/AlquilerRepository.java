package com.example.gestionvehiculos.domain.repository;

import com.example.gestionvehiculos.infraestructure.entity.Alquiler;
import com.example.gestionvehiculos.infraestructure.entity.Alquiler.EstadoAlquiler;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlquilerRepository extends CrudRepository<Alquiler, Long> {

    @Query(value = "SELECT * FROM alquileres WHERE cliente_id = ?1", nativeQuery = true)
    List<Alquiler> findByClienteId(Long clienteId);

    @Query(value = "SELECT * FROM alquileres WHERE vehiculo_id = ?1", nativeQuery = true)
    List<Alquiler> findByVehiculoId(Long vehiculoId);

    @Query(value = "SELECT * FROM alquileres WHERE estado = ?1", nativeQuery = true)
    List<Alquiler> findByEstado(EstadoAlquiler estado);

    @Query(value = "SELECT * FROM alquileres WHERE fecha_inicio BETWEEN ?1 AND ?2", nativeQuery = true)
    List<Alquiler> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);

    @Query(value = """
            SELECT * FROM alquileres
            WHERE fecha_fin < CURRENT_DATE
            AND estado = 'ACTIVO'
            """, nativeQuery = true)
    List<Alquiler> findAlquileresVencidos();

    @Query(value = """
            SELECT * FROM alquileres
            WHERE cliente_id = :clienteId
            ORDER BY fecha_inicio DESC
            """, nativeQuery = true)
    List<Alquiler> findHistorialAlquileresCliente(Long clienteId);

    @Query(value = """
            SELECT COUNT(*) FROM alquileres
            WHERE vehiculo_id = :vehiculoId
            AND (
                (fecha_inicio BETWEEN :fechaInicio AND :fechaFin)
                OR (fecha_fin BETWEEN :fechaInicio AND :fechaFin)
            )
            """, nativeQuery = true)
    Long countAlquileresActivosEnFechas(Long vehiculoId, LocalDate fechaInicio, LocalDate fechaFin);
}