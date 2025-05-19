package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LibrosCatalogoModelo;

public class LibrosCatalogoModelo {
    private String titulo;
    private String categoria;
    private String pdfBase64;
    private String edad;
    private String portadaBase64;

    // Constructor vacío requerido por Firestore
    public LibrosCatalogoModelo() {
    }

    // Getters y setters
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

    public String getPdfBase64() {
        return pdfBase64;
    }

    public void setPdfBase64(String pdfBase64) {
        this.pdfBase64 = pdfBase64;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getPortadaBase64() {
        return portadaBase64;
    }

    public void setPortadaBase64(String portadaBase64) {
        this.portadaBase64 = portadaBase64;
    }
}