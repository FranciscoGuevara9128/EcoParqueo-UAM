package org.uam.ecoparqueouam_service.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.uam.ecoparqueouam_service.model.LoginRequest;
import org.uam.ecoparqueouam_service.model.Usuario;
import org.uam.ecoparqueouam_service.repository.RepositoryUsuario;

import java.util.List;
import java.util.UUID;

@Service
public class ServiceUsuario {

    private final RepositoryUsuario repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ServiceUsuario(RepositoryUsuario repo) {
        this.repo = repo;
    }

    public List<Usuario> findAll() {
        return repo.findAll();
    }

    public Usuario findById(UUID id) {
        return repo.findById(id).orElseThrow(
                () -> new RuntimeException("No se encontró el usuario con id: " + id)
        );
    }

    public Usuario findByNombre(String nombre) {
        return repo.findByNombre(nombre).orElseThrow(
                () -> new RuntimeException("No se encontró el usuario con nombre: " + nombre)
        );
    }

    public List<Usuario> findByTipo(String tipo) {
        return repo.findByTipoUsuario(tipo);
    }

    /**
     * Hashea la contraseña con BCrypt antes de persistir.
     * Si el usuario ya tiene un hash (actualización sin cambiar contraseña),
     * se detecta por el prefijo "$2a$" y no se vuelve a hashear.
     */
    public Usuario save(Usuario usuario) {
        // Si es una creación (id nulo), verificar que el nombre no esté duplicado
        if (usuario.getId() == null && repo.findByNombre(usuario.getNombre()).isPresent()) {
            throw new RuntimeException("El nombre de usuario '" + usuario.getNombre() + "' ya está registrado");
        }
        
        String raw = usuario.getContrasena();
        if (raw != null && !raw.startsWith("$2a$")) {
            usuario.setContrasena(encoder.encode(raw));
        }
        return repo.save(usuario);
    }

    /**
     * Verifica nombre + contraseña.
     * Retorna el Usuario sin el campo contraseña (@JsonProperty WRITE_ONLY lo omite en la respuesta).
     * Lanza RuntimeException si las credenciales son incorrectas.
     */
    public Usuario login(LoginRequest request) {
        Usuario usuario = repo.findByNombre(request.getNombre())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!encoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        return usuario;
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
