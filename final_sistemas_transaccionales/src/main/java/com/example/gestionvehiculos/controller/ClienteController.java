package com.example.gestionvehiculos.controller;

import com.example.gestionvehiculos.domain.dto.ClienteAlquileresDto;
import com.example.gestionvehiculos.domain.dto.ClienteDto;
import com.example.gestionvehiculos.domain.service.ClienteService;
import com.example.gestionvehiculos.util.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteDto>> getAllClientes() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> getClienteById(@PathVariable Long id) {
        return clienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
    }

    @GetMapping("/documento/{documento}")
    public ResponseEntity<ClienteDto> getClienteByDocumento(@PathVariable String documento) {
        return clienteService.findByDocumento(documento)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "documento", documento));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteDto> getClienteByEmail(@PathVariable String email) {
        return clienteService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "email", email));
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<ClienteDto>> getClientesByNombre(@PathVariable String nombre) {
        List<ClienteDto> clientes = clienteService.findByNombre(nombre);
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<ClienteDto> createCliente(@Valid @RequestBody ClienteDto clienteDto) {
        ClienteDto savedCliente = clienteService.save(clienteDto);
        return new ResponseEntity<>(savedCliente, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto> updateCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDto clienteDto) {

        return clienteService.update(id, clienteDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        if (!clienteService.delete(id)) {
            throw new ResourceNotFoundException("Cliente", "id", id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mas-alquileres")
    public ResponseEntity<List<ClienteAlquileresDto>> getClientesConMasAlquileres() {
        List<ClienteAlquileresDto> clientesConMasAlquileres = clienteService.findClientesConMasAlquileresUltimoAnio();
        return ResponseEntity.ok(clientesConMasAlquileres);
    }
}