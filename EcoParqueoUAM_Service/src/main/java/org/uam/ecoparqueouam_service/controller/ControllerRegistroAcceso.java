package org.uam.ecoparqueouam_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uam.ecoparqueouam_service.model.RegistroAcceso;
import org.uam.ecoparqueouam_service.service.ServiceRegistroAcceso;

import java.util.List;

@RestController
@RequestMapping("/registro-acceso")
public class ControllerRegistroAcceso {

    private final ServiceRegistroAcceso service;

    public ControllerRegistroAcceso(ServiceRegistroAcceso service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<List<RegistroAcceso>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping("/save")
    public ResponseEntity<RegistroAcceso> registrarAcceso(@RequestBody RegistroAcceso request) {
        return ResponseEntity.ok(service.registrarAcceso(request.getPlaca(), request.getParqueoNombre()));
    }
}
