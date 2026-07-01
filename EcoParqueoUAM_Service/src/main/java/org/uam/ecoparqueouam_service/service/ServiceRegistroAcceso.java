package org.uam.ecoparqueouam_service.service;

import org.springframework.stereotype.Service;
import org.uam.ecoparqueouam_service.model.RegistroAcceso;
import org.uam.ecoparqueouam_service.repository.RepositoryRegistroAcceso;

import java.util.List;

@Service
public class ServiceRegistroAcceso {

    private final RepositoryRegistroAcceso repo;

    public ServiceRegistroAcceso(RepositoryRegistroAcceso repo) {
        this.repo = repo;
    }

    public List<RegistroAcceso> findAll() {
        return repo.findAll();
    }

    public RegistroAcceso registrarAcceso(String placa, String parqueoNombre) {
        RegistroAcceso nuevo = new RegistroAcceso();
        nuevo.setPlaca(placa.trim().toUpperCase());
        nuevo.setParqueoNombre(parqueoNombre);
        nuevo.setFechaHora(System.currentTimeMillis());
        return repo.save(nuevo);
    }
}
