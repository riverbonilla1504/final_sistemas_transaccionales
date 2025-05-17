package com.example.gestionvehiculos.domain.service;

import com.example.gestionvehiculos.domain.dto.ClienteAlquileresDto;
import com.example.gestionvehiculos.domain.dto.ClienteDto;
import com.example.gestionvehiculos.domain.repository.ClienteRepository;
import com.example.gestionvehiculos.infraestructure.entity.Cliente;
import com.example.gestionvehiculos.infraestructure.mapper.ClienteMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Transactional(readOnly = true)
    public List<ClienteDto> findAll() {
        List<Cliente> clientes = new ArrayList<>();
        clienteRepository.findAll().forEach(clientes::add);
        return clienteMapper.toDtoList(clientes);
    }

    @Transactional(readOnly = true)
    public Optional<ClienteDto> findById(Long id) {
        return clienteRepository.findById(id)
                .map(clienteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ClienteDto> findByDocumento(String documento) {
        return clienteRepository.findByDocumento(documento)
                .map(clienteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ClienteDto> findByEmail(String email) {
        return clienteRepository.findByEmail(email)
                .map(clienteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ClienteDto> findByNombre(String nombre) {
        return clienteMapper.toDtoList(
                clienteRepository.findByNombreContainingIgnoreCase(nombre));
    }

    @Transactional
    public ClienteDto save(ClienteDto clienteDto) {
        // Verificamos si existe otro cliente con el mismo documento
        if (clienteDto.getId() == null && clienteRepository.findByDocumento(clienteDto.getDocumento()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con este documento");
        }

        // Verificamos si existe otro cliente con el mismo email
        if (clienteDto.getId() == null && clienteRepository.findByEmail(clienteDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con este email");
        }

        Cliente cliente = clienteMapper.toEntity(clienteDto);
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toDto(cliente);
    }

    @Transactional
    public Optional<ClienteDto> update(Long id, ClienteDto clienteDto) {
        if (!clienteRepository.existsById(id)) {
            return Optional.empty();
        }

        // Verificamos si existe otro cliente con el mismo documento
        Optional<Cliente> clienteByDocumento = clienteRepository.findByDocumento(clienteDto.getDocumento());
        if (clienteByDocumento.isPresent() && !clienteByDocumento.get().getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe otro cliente con este documento");
        }

        // Verificamos si existe otro cliente con el mismo email
        Optional<Cliente> clienteByEmail = clienteRepository.findByEmail(clienteDto.getEmail());
        if (clienteByEmail.isPresent() && !clienteByEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe otro cliente con este email");
        }

        return clienteRepository.findById(id)
                .map(cliente -> {
                    clienteMapper.updateEntityFromDto(clienteDto, cliente);
                    Cliente updatedCliente = clienteRepository.save(cliente);
                    return clienteMapper.toDto(updatedCliente);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            return false;
        }
        clienteRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<ClienteAlquileresDto> findClientesConMasAlquileresUltimoAnio() {
        // Obtener la fecha de hace un año
        LocalDate fechaHaceUnAnio = LocalDate.now().minusYears(1);

        // Consulta JPQL para obtener clientes con más alquileres en el último año
        String jpql = "SELECT c, COUNT(a) as total FROM Cliente c " +
                "JOIN c.alquileres a " +
                "WHERE a.fechaInicio >= :fechaInicio " +
                "GROUP BY c.id " +
                "ORDER BY total DESC";

        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("fechaInicio", fechaHaceUnAnio);

        return query.getResultList().stream()
                .map(result -> {
                    Cliente cliente = (Cliente) result[0];
                    Long cantidadAlquileres = (Long) result[1];
                    return clienteMapper.toClienteAlquileresDto(cliente, cantidadAlquileres);
                })
                .collect(Collectors.toList());
    }
}