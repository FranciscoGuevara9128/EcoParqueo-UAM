package org.uam.ecoparqueouam_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uam.ecoparqueouam_service.model.Parqueo;
import org.uam.ecoparqueouam_service.service.ServiceParqueo;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/parqueo")
public class ControllerParqueo {

    private final ServiceParqueo service;

    public ControllerParqueo(ServiceParqueo service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Parqueo>> findAll(){
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Parqueo> findById(@PathVariable UUID id){
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/update")
    public ResponseEntity<Parqueo> update(@RequestBody Parqueo parqueo){
        Parqueo p = service.findById(parqueo.getId());
        p.setName(parqueo.getName());
        p.setCapacidadTotal(parqueo.getCapacidadTotal());
        p.setDisponibles(parqueo.getDisponibles());
        p.setDireccion(parqueo.getDireccion());
        return ResponseEntity.ok(service.save(p));
    }

    @PostMapping("/save")
    public ResponseEntity<Parqueo> save(@RequestBody Parqueo parqueo){
        return ResponseEntity.ok(service.save(parqueo));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}