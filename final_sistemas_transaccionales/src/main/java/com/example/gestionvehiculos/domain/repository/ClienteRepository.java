package com.example.gestionvehiculos.domain.repository;

import com.example.gestionvehiculos.infraestructure.entity.Cliente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    Optional<Cliente> findByDocumento(String documento);

    Optional<Cliente> findByEmail(String email);

    @Query(value = "SELECT * FROM clientes WHERE LOWER(nombre) LIKE LOWER(CONCAT('%', ?1, '%'))", nativeQuery = true)
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    @Query(value = """
            SELECT c.* FROM clientes c
            INNER JOIN alquileres a ON c.id = a.cliente_id
            WHERE a.fecha_inicio >= :fechaInicio
            GROUP BY c.id
            ORDER BY COUNT(a.id) DESC
            """, nativeQuery = true)
    List<Cliente> findClientesConMasAlquileresUltimoAnio(LocalDate fechaInicio);

    @Query("SELECT c, COUNT(a) as total FROM Cliente c " +
            "JOIN c.alquileres a " +
            "WHERE a.fechaInicio >= :fechaInicio " +
            "GROUP BY c.id " +
            "ORDER BY total DESC")
    List<Object[]> findClientesConMasAlquileresUltimoAnioConConteo(LocalDate fechaInicio);
}