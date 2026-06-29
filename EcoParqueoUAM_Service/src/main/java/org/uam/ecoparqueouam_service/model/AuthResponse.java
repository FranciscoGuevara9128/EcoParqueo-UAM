package org.uam.ecoparqueouam_service.model;

public class AuthResponse {
    private String token;
    private Usuario usuario;

    public AuthResponse() {}

    public AuthResponse(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
