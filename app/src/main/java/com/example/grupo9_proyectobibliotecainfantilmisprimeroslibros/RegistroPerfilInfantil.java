package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
public class RegistroPerfilInfantil {
    private String id;
    private String nombre;
    private int edad;
    private String avatar;
    private List<String> intereses;

    // Constructor vacío requerido por Firebase
    public RegistroPerfilInfantil() {
        // Valores predeterminados
        this.intereses = new ArrayList<>();
        this.avatar = "avatar1";
    }

    public RegistroPerfilInfantil(String nombre, int edad, String avatar, List<String> intereses) {
        this.nombre = nombre;
        this.edad = edad;
        this.avatar = avatar == null ? "avatar1" : avatar;
        this.intereses = intereses == null ? new ArrayList<>() : intereses;
    }
    public RegistroPerfilInfantil(String id, String nombre, int edad, String avatar, List<String> intereses) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.avatar = avatar == null ? "avatar1" : avatar;
        this.intereses = intereses == null ? new ArrayList<>() : intereses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        this.avatar = avatar == null ? "avatar1" : avatar;
    }

    public List<String> getIntereses() {
        if (intereses == null) {
            intereses = new ArrayList<>();
        }
        return intereses;
    }

    public void setIntereses(List<String> intereses) {
        this.intereses = intereses == null ? new ArrayList<>() : intereses;
    }
}
