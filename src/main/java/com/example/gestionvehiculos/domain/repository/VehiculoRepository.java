package com.example.gestionvehiculos.domain.repository;

import com.example.gestionvehiculos.infraestructure.entity.Vehiculo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends CrudRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByMatricula(String matricula);

    @Query(value = "SELECT * FROM vehiculos WHERE LOWER(marca) LIKE LOWER(CONCAT('%', ?1, '%'))", nativeQuery = true)
    List<Vehiculo> findByMarcaContainingIgnoreCase(String marca);

    @Query(value = "SELECT * FROM vehiculos WHERE LOWER(tipo) LIKE LOWER(CONCAT('%', ?1, '%'))", nativeQuery = true)
    List<Vehiculo> findByTipoContainingIgnoreCase(String tipo);

    List<Vehiculo> findByDisponible(Boolean disponible);

    @Query(value = """
            SELECT v.* FROM vehiculos v
            WHERE v.id NOT IN (
                SELECT a.vehiculo_id
                FROM alquileres a
                WHERE a.fecha_inicio >= :fecha
            )
            """, nativeQuery = true)
    List<Vehiculo> findVehiculosSinAlquileresEnUltimos30Dias(LocalDate fecha);

    @Query(value = """
            SELECT v.tipo, AVG(DATEDIFF(a.fecha_fin, a.fecha_inicio) + 1) as promedio_dias
            FROM vehiculos v
            INNER JOIN alquileres a ON v.id = a.vehiculo_id
            GROUP BY v.tipo
            """, nativeQuery = true)
    List<Object[]> findPromedioDuracionPorTipoVehiculo();

    @Query(value = """
            SELECT YEAR(a.fecha_inicio) as anio,
                   MONTH(a.fecha_inicio) as mes,
                   v.tipo,
                   SUM(a.costo_total) as ingresos
            FROM vehiculos v
            INNER JOIN alquileres a ON v.id = a.vehiculo_id
            GROUP BY anio, mes, v.tipo
            ORDER BY anio, mes
            """, nativeQuery = true)
    List<Object[]> findIngresosTotalesPorMesYTipoVehiculo();
}