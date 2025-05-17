package com.example.gestionvehiculos.controller;

import com.example.gestionvehiculos.domain.dto.PromedioTipoVehiculoDto;
import com.example.gestionvehiculos.domain.dto.VehiculoDto;
import com.example.gestionvehiculos.domain.service.VehiculoService;
import com.example.gestionvehiculos.util.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public ResponseEntity<List<VehiculoDto>> getAllVehiculos() {
        return ResponseEntity.ok(vehiculoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoDto> getVehiculoById(@PathVariable Long id) {
        return vehiculoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "id", id));
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<VehiculoDto> getVehiculoByMatricula(@PathVariable String matricula) {
        return vehiculoService.findByMatricula(matricula)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "matrícula", matricula));
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<VehiculoDto>> getVehiculosByMarca(@PathVariable String marca) {
        List<VehiculoDto> vehiculos = vehiculoService.findByMarcaContaining(marca);
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<VehiculoDto>> getVehiculosByTipo(@PathVariable String tipo) {
        List<VehiculoDto> vehiculos = vehiculoService.findByTipoContaining(tipo);
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<VehiculoDto>> getVehiculosDisponibles() {
        List<VehiculoDto> vehiculos = vehiculoService.findByDisponible(true);
        return ResponseEntity.ok(vehiculos);
    }

    @PostMapping
    public ResponseEntity<VehiculoDto> createVehiculo(@Valid @RequestBody VehiculoDto vehiculoDto) {
        VehiculoDto savedVehiculo = vehiculoService.save(vehiculoDto);
        return new ResponseEntity<>(savedVehiculo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoDto> updateVehiculo(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoDto vehiculoDto) {

        return vehiculoService.update(id, vehiculoDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "id", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehiculo(@PathVariable Long id) {
        if (!vehiculoService.delete(id)) {
            throw new ResourceNotFoundException("Vehículo", "id", id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sin-alquileres-ultimos-30-dias")
    public ResponseEntity<List<VehiculoDto>> getVehiculosSinAlquileresUltimos30Dias() {
        List<VehiculoDto> vehiculos = vehiculoService.findVehiculosSinAlquileresUltimos30Dias();
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/promedio-duracion-por-tipo")
    public ResponseEntity<List<PromedioTipoVehiculoDto>> getPromedioDuracionPorTipoVehiculo() {
        List<PromedioTipoVehiculoDto> promedios = vehiculoService.findPromedioDuracionPorTipoVehiculo();
        return ResponseEntity.ok(promedios);
    }
}