package com.ProyectoFlor.model;

public class Ruta {

    private String ruta;
    private boolean requiereRol;
    private Rol rol;

    public Ruta(String ruta, boolean requiereRol, Rol rol) {
        this.ruta = ruta;
        this.requiereRol = requiereRol;
        this.rol = rol;
    }

    public String getRuta() {
        return ruta;
    }

    public boolean isRequiereRol() {
        return requiereRol;
    }

    public Rol getRol() {
        return rol;
    }
}