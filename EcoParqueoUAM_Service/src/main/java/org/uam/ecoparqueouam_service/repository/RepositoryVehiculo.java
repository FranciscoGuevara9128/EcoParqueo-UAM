package org.uam.ecoparqueouam_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.uam.ecoparqueouam_service.model.Vehiculo;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepositoryVehiculo extends JpaRepository<Vehiculo, UUID> {

    // Búsqueda por número de placa (útil para el control de acceso del guarda)
    Optional<Vehiculo> findByNumeroPlaca(String numeroPlaca);
}
