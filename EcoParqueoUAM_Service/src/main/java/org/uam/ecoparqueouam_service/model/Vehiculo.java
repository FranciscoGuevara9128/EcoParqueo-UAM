package org.uam.ecoparqueouam_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "vehiculo")
public class Vehiculo extends BaseEntity {

    private String marca;

    @Column(name = "numero_placa", unique = true)
    @JsonProperty("numero_placa")
    private String numeroPlaca;

    private String modelo;

    private String anio;

    @Column(name = "color_vehiculo")
    @JsonProperty("color_vehiculo")
    private String colorVehiculo;

    @Column(name = "tipo_vehiculo")
    @JsonProperty("tipo_vehiculo")
    private String tipoVehiculo;

    @Column(name = "notas_adicionales")
    @JsonProperty("notas_adicionales")
    private String notasAdicionales;

    // Relación con Usuario (nullable mientras el login no esté implementado)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true)
    @JsonProperty("usuario")
    private Usuario usuario;

    // ── Getters y Setters ──────────────────────────────────────────────

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getNumeroPlaca() { return numeroPlaca; }
    public void setNumeroPlaca(String numeroPlaca) { this.numeroPlaca = numeroPlaca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getAnio() { return anio; }
    public void setAnio(String anio) { this.anio = anio; }

    public String getColorVehiculo() { return colorVehiculo; }
    public void setColorVehiculo(String colorVehiculo) { this.colorVehiculo = colorVehiculo; }

    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public String getNotasAdicionales() { return notasAdicionales; }
    public void setNotasAdicionales(String notasAdicionales) { this.notasAdicionales = notasAdicionales; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
