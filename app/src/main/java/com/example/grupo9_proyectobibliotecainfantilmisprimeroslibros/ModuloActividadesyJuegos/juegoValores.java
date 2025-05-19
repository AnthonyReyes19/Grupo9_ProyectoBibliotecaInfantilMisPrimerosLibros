package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

public class juegoValores extends AppCompatActivity {

    ImageView imgSituacion;
    RadioGroup opcionValores;
    Button btnSiguiente;
    TextView txtPregunta;

    // Preguntas
    int preguntaActual = 0;
    int respuestasCorrectas = 0;

    int[] imagenes = {R.drawable.respeto, R.drawable.solidaridad, R.drawable.empatia};
    String[] preguntas = {
            "¿Qué valor representa esta imagen?",
            "¿Qué valor representa esta situación?",
            "¿Cuál es el valor más adecuado aquí?"
    };
    String[][] opciones = {
            {"Respeto", "Solidaridad", "Empatía"},
            {"Perseverancia", "Solidaridad", "Amistad"},
            {"Empatía", "Honestidad", "Respeto"}
    };
    int[] respuestas = {0, 0, 0}; // índice de la respuesta correcta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_valores);

        imgSituacion = findViewById(R.id.imgSituacion);
        opcionValores = findViewById(R.id.opcionesValores);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        txtPregunta = findViewById(R.id.txtPreguntas);

        cargarPregunta();

        btnSiguiente.setOnClickListener(v -> {
            int seleccion = opcionValores.getCheckedRadioButtonId();
            if (seleccion == -1) {
                Toast.makeText(this, "Selecciona una opción", Toast.LENGTH_SHORT).show();
                return;
            }

            int respuestaSeleccionada = opcionValores.indexOfChild(findViewById(seleccion));
            if (respuestaSeleccionada == respuestas[preguntaActual]) {
                respuestasCorrectas++;
            }

            preguntaActual++;
            if (preguntaActual < preguntas.length) {
                cargarPregunta();
            } else {
                mostrarResultado();
            }
        });
    }

    public void cargarPregunta() {
        txtPregunta.setText(preguntas[preguntaActual]);
        imgSituacion.setImageResource(imagenes[preguntaActual]);

        for (int i = 0; i < opciones[preguntaActual].length; i++) {
            ((RadioButton) opcionValores.getChildAt(i)).setText(opciones[preguntaActual][i]);
        }

        opcionValores.clearCheck();
    }

    public void mostrarResultado() {
        Intent intent = new Intent(this, resultadoJuego.class);
        intent.putExtra("aciertos", respuestasCorrectas);
        intent.putExtra("total", preguntas.length);
        startActivity(intent);
        finish();
    }
}
