package org.uam.ecoparqueouam_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uam.ecoparqueouam_service.model.Vehiculo;
import org.uam.ecoparqueouam_service.service.ServiceVehiculo;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vehiculo")
public class ControllerVehiculo {

    private final ServiceVehiculo service;

    public ControllerVehiculo(ServiceVehiculo service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Vehiculo>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Ruta "/getId/{id}" para mantener compatibilidad con VehiculoApiService.kt del cliente Android
    @GetMapping("/getId/{id}")
    public ResponseEntity<Vehiculo> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Búsqueda por placa: usada por el guarda en ControlAccesoVehicularScreen
    @GetMapping("/placa/{placa}")
    public ResponseEntity<Vehiculo> findByPlaca(@PathVariable String placa) {
        return ResponseEntity.ok(service.findByPlaca(placa));
    }

    @PostMapping("/save")
    public ResponseEntity<Vehiculo> save(@RequestBody Vehiculo vehiculo) {
        return ResponseEntity.ok(service.save(vehiculo));
    }

    @PutMapping("/update")
    public ResponseEntity<Vehiculo> update(@RequestBody Vehiculo vehiculo) {
        Vehiculo v = service.findById(vehiculo.getId());
        v.setMarca(vehiculo.getMarca());
        v.setNumeroPlaca(vehiculo.getNumeroPlaca());
        v.setModelo(vehiculo.getModelo());
        v.setAnio(vehiculo.getAnio());
        v.setColorVehiculo(vehiculo.getColorVehiculo());
        v.setTipoVehiculo(vehiculo.getTipoVehiculo());
        v.setNotasAdicionales(vehiculo.getNotasAdicionales());
        v.setUsuario(vehiculo.getUsuario());
        return ResponseEntity.ok(service.save(v));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
