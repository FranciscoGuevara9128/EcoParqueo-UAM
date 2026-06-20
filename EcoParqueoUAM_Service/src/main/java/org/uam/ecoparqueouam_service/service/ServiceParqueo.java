package org.uam.ecoparqueouam_service.service;

import org.springframework.stereotype.Service;
import org.uam.ecoparqueouam_service.model.Parqueo;
import org.uam.ecoparqueouam_service.repository.RepositoryParqueo;

import java.util.List;
import java.util.UUID;

@Service
public class ServiceParqueo {
    private final RepositoryParqueo repo;

    public ServiceParqueo(RepositoryParqueo repo) {
        this.repo = repo;
    }

    public List<Parqueo> findAll(){
        return repo.findAll();
    }

    public Parqueo findById(UUID id){
        return repo.findById(id).orElseThrow(
                ()->new RuntimeException("No se encontro la tarea, con el id:"+id));
    }

    public Parqueo save(Parqueo parqueo){
        return repo.save(parqueo);
    }


    public void delete(UUID id) {
        repo.deleteById(id);
    }
}

