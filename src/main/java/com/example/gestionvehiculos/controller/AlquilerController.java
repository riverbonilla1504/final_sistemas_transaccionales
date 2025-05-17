package com.example.gestionvehiculos.controller;

import com.example.gestionvehiculos.domain.dto.AlquilerDto;
import com.example.gestionvehiculos.domain.dto.IngresoMensualDto;
import com.example.gestionvehiculos.domain.service.AlquilerService;
import com.example.gestionvehiculos.infraestructure.entity.Alquiler.EstadoAlquiler;
import com.example.gestionvehiculos.util.exception.BadRequestException;
import com.example.gestionvehiculos.util.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {

    private final AlquilerService alquilerService;

    public AlquilerController(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
    }

    @GetMapping
    public ResponseEntity<List<AlquilerDto>> getAllAlquileres() {
        return ResponseEntity.ok(alquilerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlquilerDto> getAlquilerById(@PathVariable Long id) {
        return alquilerService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Alquiler", "id", id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<AlquilerDto>> getAlquileresByClienteId(@PathVariable Long clienteId) {
        List<AlquilerDto> alquileres = alquilerService.findByClienteId(clienteId);
        return ResponseEntity.ok(alquileres);
    }

    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<AlquilerDto>> getAlquileresByVehiculoId(@PathVariable Long vehiculoId) {
        List<AlquilerDto> alquileres = alquilerService.findByVehiculoId(vehiculoId);
        return ResponseEntity.ok(alquileres);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<AlquilerDto>> getAlquileresByEstado(@PathVariable EstadoAlquiler estado) {
        List<AlquilerDto> alquileres = alquilerService.findByEstado(estado);
        return ResponseEntity.ok(alquileres);
    }

    @GetMapping("/vencidos")
    public ResponseEntity<List<AlquilerDto>> getAlquileresVencidos() {
        List<AlquilerDto> alquileres = alquilerService.findAlquileresVencidos();
        return ResponseEntity.ok(alquileres);
    }

    @GetMapping("/historial-cliente/{clienteId}")
    public ResponseEntity<List<AlquilerDto>> getHistorialAlquileresCliente(@PathVariable Long clienteId) {
        List<AlquilerDto> alquileres = alquilerService.findHistorialAlquileresCliente(clienteId);
        return ResponseEntity.ok(alquileres);
    }

    @PostMapping
    public ResponseEntity<AlquilerDto> createAlquiler(@Valid @RequestBody AlquilerDto alquilerDto) {
        // Validación adicional para fecha de fin posterior a fecha de inicio
        if (!alquilerDto.esFechaFinValida()) {
            throw new BadRequestException("La fecha de fin debe ser posterior o igual a la fecha de inicio");
        }

        AlquilerDto savedAlquiler = alquilerService.save(alquilerDto);
        return new ResponseEntity<>(savedAlquiler, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlquilerDto> updateAlquiler(
            @PathVariable Long id,
            @Valid @RequestBody AlquilerDto alquilerDto) {

        // Validación adicional para fecha de fin posterior a fecha de inicio
        if (!alquilerDto.esFechaFinValida()) {
            throw new BadRequestException("La fecha de fin debe ser posterior o igual a la fecha de inicio");
        }

        return alquilerService.update(id, alquilerDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Alquiler", "id", id));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<AlquilerDto> completarAlquiler(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDevolucion) {

        return alquilerService.completarAlquiler(id, fechaDevolucion)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Alquiler", "id", id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AlquilerDto> cancelarAlquiler(@PathVariable Long id) {
        return alquilerService.cancelarAlquiler(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Alquiler", "id", id));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<AlquilerDto> activarAlquiler(@PathVariable Long id) {
        return alquilerService.activarAlquiler(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Alquiler", "id", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlquiler(@PathVariable Long id) {
        if (!alquilerService.delete(id)) {
            throw new ResourceNotFoundException("Alquiler", "id", id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ingresos-por-mes-y-tipo-vehiculo")
    public ResponseEntity<List<IngresoMensualDto>> getIngresosTotalesPorMesYTipoVehiculo() {
        List<IngresoMensualDto> ingresos = alquilerService.findIngresosTotalesPorMesYTipoVehiculo();
        return ResponseEntity.ok(ingresos);
    }
}