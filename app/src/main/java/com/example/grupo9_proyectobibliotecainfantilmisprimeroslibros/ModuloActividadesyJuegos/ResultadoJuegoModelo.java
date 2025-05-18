package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos;

public class ResultadoJuegoModelo {
    private String jugador;
    private int aciertos;
    private int total;

    public ResultadoJuegoModelo() {} // Constructor vacío requerido por Firebase

    public ResultadoJuegoModelo(String jugador, int aciertos, int total) {
        this.jugador = jugador;
        this.aciertos = aciertos;
        this.total = total;
    }

    public String getJugador() { return jugador; }
    public int getAciertos() { return aciertos; }
    public int getTotal() { return total; }
}

