package org.uam.ecoparqueouam_service.service;

import org.springframework.stereotype.Service;
import org.uam.ecoparqueouam_service.model.Vehiculo;
import org.uam.ecoparqueouam_service.repository.RepositoryVehiculo;

import java.util.List;
import java.util.UUID;

@Service
public class ServiceVehiculo {

    private final RepositoryVehiculo repo;

    public ServiceVehiculo(RepositoryVehiculo repo) {
        this.repo = repo;
    }

    public List<Vehiculo> findAll() {
        return repo.findAll();
    }

    public Vehiculo findById(UUID id) {
        return repo.findById(id).orElseThrow(
                () -> new RuntimeException("No se encontró el vehículo con id: " + id)
        );
    }

    public Vehiculo findByPlaca(String placa) {
        return repo.findByNumeroPlaca(placa.trim().toUpperCase()).orElseThrow(
                () -> new RuntimeException("No se encontró el vehículo con placa: " + placa)
        );
    }

    public Vehiculo save(Vehiculo vehiculo) {
        // Normalizar la placa a mayúsculas antes de guardar
        if (vehiculo.getNumeroPlaca() != null) {
            vehiculo.setNumeroPlaca(vehiculo.getNumeroPlaca().trim().toUpperCase());
        }
        return repo.save(vehiculo);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
