package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Modelo;

public class Libros {
    String titulo, categoria, edad;
    public Libros(){}

    public Libros(String titulo, String categoria, String edad) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.edad = edad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }
}
