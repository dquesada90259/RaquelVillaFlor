package com.ProyectoFlor.model; 

public class Rol {

    private String rol;  // Nombre del rol (por ejemplo, "ADMIN", "USER", etc.)

    // Constructor
    public Rol(String rol) {
        this.rol = rol;
    }

    // Getter y Setter
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
