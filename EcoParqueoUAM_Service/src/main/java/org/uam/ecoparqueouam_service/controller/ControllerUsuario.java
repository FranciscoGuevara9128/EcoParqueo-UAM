package org.uam.ecoparqueouam_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uam.ecoparqueouam_service.model.Usuario;
import org.uam.ecoparqueouam_service.service.ServiceUsuario;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuario")
public class ControllerUsuario {

    private final ServiceUsuario service;

    public ControllerUsuario(ServiceUsuario service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Usuario>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Endpoint de login: el cliente envía el nombre y obtiene el usuario completo con su tipo
    @GetMapping("/login")
    public ResponseEntity<Usuario> findByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(service.findByNombre(nombre));
    }

    // Filtrar por tipo: GET /api/usuario/tipo?tipo=Estudiante
    @GetMapping("/tipo")
    public ResponseEntity<List<Usuario>> findByTipo(@RequestParam String tipo) {
        return ResponseEntity.ok(service.findByTipo(tipo));
    }

    @PostMapping("/save")
    public ResponseEntity<Usuario> save(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(service.save(usuario));
    }

    @PutMapping("/update")
    public ResponseEntity<Usuario> update(@RequestBody Usuario usuario) {
        Usuario u = service.findById(usuario.getId());
        u.setNombre(usuario.getNombre());
        u.setTipoUsuario(usuario.getTipoUsuario());
        return ResponseEntity.ok(service.save(u));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
