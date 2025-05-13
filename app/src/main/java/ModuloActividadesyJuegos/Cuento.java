package ModuloActividadesyJuegos;

public class Cuento {
    private int id;
    private String titulo;
    private String autor;
    private float rating;

    public Cuento(int id, String titulo, String autor, float rating) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.rating = rating;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public float getRating() { return rating; }
}

