package org.uam.ecoparqueouam_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.uam.ecoparqueouam_service.model.Parqueo;

import java.util.UUID;

@Repository
public interface RepositoryParqueo extends JpaRepository<Parqueo, UUID> {

}
