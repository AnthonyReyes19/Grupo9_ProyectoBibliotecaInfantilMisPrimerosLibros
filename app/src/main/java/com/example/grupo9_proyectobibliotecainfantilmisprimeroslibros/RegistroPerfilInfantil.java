package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import java.util.List;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
public class RegistroPerfilInfantil {

    private String id;
    private String nombre;
    private int edad;
    private String avatar;
    private List<String> intereses;

    public RegistroPerfilInfantil() {
    }

    public RegistroPerfilInfantil(String id,String nombre, int edad, String avatar, List<String> intereses) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.avatar = avatar;
        this.intereses = intereses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
