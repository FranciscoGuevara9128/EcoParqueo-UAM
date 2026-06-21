package org.uam.ecoparqueouam_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    // Valores esperados: "Estudiante" | "Guarda"
    @Column(name = "tipo_usuario", nullable = false)
    private String tipoUsuario;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Un usuario puede tener múltiples vehículos registrados
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehiculo> vehiculos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }

    // ── Getters y Setters ──────────────────────────────────────────────

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public List<Vehiculo> getVehiculos() { return vehiculos; }
    public void setVehiculos(List<Vehiculo> vehiculos) { this.vehiculos = vehiculos; }
}
