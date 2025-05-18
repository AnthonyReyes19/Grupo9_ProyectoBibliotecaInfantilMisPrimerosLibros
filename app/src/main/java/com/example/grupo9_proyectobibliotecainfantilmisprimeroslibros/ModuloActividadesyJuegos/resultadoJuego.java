package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class resultadoJuego extends AppCompatActivity {

    TextView resultadoTexto;
    RatingBar ratingResultado;
    Button btnReintentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_juego);

        resultadoTexto = findViewById(R.id.resultadoTexto);
        ratingResultado = findViewById(R.id.ratingResultado);
        btnReintentar = findViewById(R.id.btnReintentar);

        int aciertos = getIntent().getIntExtra("aciertos", 0);
        int total = getIntent().getIntExtra("total", 3);

        resultadoTexto.setText("Respondiste correctamente " + aciertos + " de " + total + " preguntas.");
        ratingResultado.setRating(aciertos);

        guardarResultado("JugadorAnonimo", aciertos, total);

        btnReintentar.setOnClickListener(v -> {
            Intent intent = new Intent(this, juegoValores.class);
            startActivity(intent);
            finish();
        });
    }

    private void guardarResultado(String jugador, int aciertos, int total) {
        ResultadoJuegoModelo resultado = new ResultadoJuegoModelo(jugador, aciertos, total);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("resultadosValores");
        ref.push().setValue(resultado);
    }
    public void regresar(View v){
        Intent ventanaRegistro = new Intent(this, menuPrincipalModuloReyes.class);
        startActivity(ventanaRegistro);
    }
    public void otroJuego(View v){
        Intent intent = new Intent(this, emocional.class);
        startActivity(intent);
    }
}