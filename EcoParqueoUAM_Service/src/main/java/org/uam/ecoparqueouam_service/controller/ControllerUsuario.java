package org.uam.ecoparqueouam_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uam.ecoparqueouam_service.model.LoginRequest;
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

    // Endpoint de login: POST con nombre + contraseña en el body
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(service.login(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // Búsqueda por nombre para uso interno (sin validar contraseña)
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Usuario> findByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(service.findByNombre(nombre));
    }

    // Filtrar por tipo: GET /api/usuario/tipo?tipo=Estudiante
    @GetMapping("/tipo")
    public ResponseEntity<List<Usuario>> findByTipo(@RequestParam String tipo) {
        return ResponseEntity.ok(service.findByTipo(tipo));
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(service.save(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
