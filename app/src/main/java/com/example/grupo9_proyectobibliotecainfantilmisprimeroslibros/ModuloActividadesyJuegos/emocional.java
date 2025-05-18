package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

public class emocional extends AppCompatActivity {

    ImageView imgFeliz, imgTriste, imgEnojado;
    Button btnRepetir, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emocional);


        imgFeliz = findViewById(R.id.imgFeliz);
        imgTriste = findViewById(R.id.imgTriste);
        imgEnojado = findViewById(R.id.imgEnojado);
        btnRepetir = findViewById(R.id.btnRepetir);
        btnVolver = findViewById(R.id.btnVolver);

        imgFeliz.setOnClickListener(v ->
                Toast.makeText(this, "¡Qué bueno que te sientas feliz!", Toast.LENGTH_SHORT).show());

        imgTriste.setOnClickListener(v ->
                Toast.makeText(this, "Está bien sentirse triste a veces.", Toast.LENGTH_SHORT).show());

        imgEnojado.setOnClickListener(v ->
                Toast.makeText(this, "Está bien estar enojado. Respira profundo.", Toast.LENGTH_SHORT).show());

        btnRepetir.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(this, loginPrincipalModuloReyes.class);
            startActivity(intent);
            finish();
        });
    }

}
