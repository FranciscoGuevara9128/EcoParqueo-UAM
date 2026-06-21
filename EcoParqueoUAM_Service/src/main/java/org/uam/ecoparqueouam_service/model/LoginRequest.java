package org.uam.ecoparqueouam_service.model;

/**
 * DTO que recibe el cliente Android en el endpoint POST /usuario/login.
 * Contiene solo nombre y contraseña en texto plano (el canal debe ser HTTPS en producción).
 */
public class LoginRequest {

    private String nombre;
    private String contrasena;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
