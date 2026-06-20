package org.uam.ecoparqueouam_service.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@MappedSuperclass
public class BaseEntity {
    @Id
    @UuidGenerator
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}