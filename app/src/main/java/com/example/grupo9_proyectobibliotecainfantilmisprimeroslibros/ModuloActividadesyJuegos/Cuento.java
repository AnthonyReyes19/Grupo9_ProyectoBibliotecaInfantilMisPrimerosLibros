package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos;

public class Cuento {
    private String id;
    private String titulo;
    private String autor;
    private float rating;

    public Cuento() {

    }

    public Cuento(String id, String titulo, String autor, float rating) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.rating = rating;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
}

