package org.uam.ecoparqueouam_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.uam.ecoparqueouam_service.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepositoryUsuario extends JpaRepository<Usuario, UUID> {

    // Búsqueda por nombre (usada en el login del cliente)
    Optional<Usuario> findByNombre(String nombre);

    // Filtro por tipo de usuario: "Estudiante" o "Guarda"
    List<Usuario> findByTipoUsuario(String tipoUsuario);
}
