package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LibrosCatalogoModelo;
import com.google.firebase.firestore.DocumentId;

public class LibrosCatalogoModelo {
    @DocumentId
    private String id; // ID del documento en Firestore

    private String titulo;
    private String categoria;
    private String edad;
    private String portadaBase64;
    private String pdfBase64;    // PDF en Base64
    private String pdfUrl;       // URL de Firebase Storage
    private String descripcion;
    private String autor;
    private long fechaCreacion;  // Timestamp

    // Constructor vacío requerido por Firestore
    public LibrosCatalogoModelo() {
        // Constructor vacío
    }


    public LibrosCatalogoModelo(String titulo, String categoria, String edad) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.edad = edad;
        this.fechaCreacion = System.currentTimeMillis();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPortadaBase64() {
        return portadaBase64;
    }

    public void setPortadaBase64(String portadaBase64) {
        this.portadaBase64 = portadaBase64;
    }

    public String getPdfBase64() {
        return pdfBase64;
    }

    public void setPdfBase64(String pdfBase64) {
        this.pdfBase64 = pdfBase64;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean tieneContenidoDisponible() {
        return (pdfUrl != null && !pdfUrl.isEmpty()) ||
                (pdfBase64 != null && !pdfBase64.isEmpty());
    }
}