package org.uam.ecoparqueouam_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="registro_acceso")
public class RegistroAcceso extends BaseEntity {
    private String placa;
    private String parqueoNombre;
    private Long fechaHora;

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getParqueoNombre() {
        return parqueoNombre;
    }

    public void setParqueoNombre(String parqueoNombre) {
        this.parqueoNombre = parqueoNombre;
    }

    public Long getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Long fechaHora) {
        this.fechaHora = fechaHora;
    }
}
