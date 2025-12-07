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

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public boolean isRequiereRol() {
        return requiereRol;
    }

    public void setRequiereRol(boolean requiereRol) {
        this.requiereRol = requiereRol;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}