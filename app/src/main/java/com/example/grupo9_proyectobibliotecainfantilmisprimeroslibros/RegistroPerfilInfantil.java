package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import java.util.List;

public class RegistroPerfilInfantil {

    private String nombre;
    private int edad;
    private String avatar;
    private List<String> intereses;
    public RegistroPerfilInfantil() {
    }

    public RegistroPerfilInfantil(String nombre, int edad, String avatar, List<String> intereses) {
        this.nombre = nombre;
        this.edad = edad;
        this.avatar = avatar;
        this.intereses = intereses;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getIntereses() {
        return intereses;
    }

    public void setIntereses(List<String> intereses) {
        this.intereses = intereses;
    }
}

