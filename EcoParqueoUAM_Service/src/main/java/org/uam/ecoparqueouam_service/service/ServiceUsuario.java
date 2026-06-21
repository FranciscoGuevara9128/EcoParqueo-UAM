package org.uam.ecoparqueouam_service.service;

import org.springframework.stereotype.Service;
import org.uam.ecoparqueouam_service.model.Usuario;
import org.uam.ecoparqueouam_service.repository.RepositoryUsuario;

import java.util.List;
import java.util.UUID;

@Service
public class ServiceUsuario {

    private final RepositoryUsuario repo;

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

    public Usuario save(Usuario usuario) {
        return repo.save(usuario);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
