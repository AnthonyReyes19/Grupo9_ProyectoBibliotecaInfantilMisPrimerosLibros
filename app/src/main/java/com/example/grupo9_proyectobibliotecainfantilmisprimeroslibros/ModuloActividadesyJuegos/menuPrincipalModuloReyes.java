package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.MainActivity;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

public class menuPrincipalModuloReyes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal_modulo_reyes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void registrarCuento(View v){
        Intent ventanaRegistrarCuento = new Intent(this, formularioCuento.class);
        startActivity(ventanaRegistrarCuento);
    }
    public void verCuento(View v){
        Intent intent = new Intent(this, VerCuento.class);
        startActivity(intent);
    }

    public void verJuegos(View v){
        Intent intent = new Intent(this, juegoValores.class);
        startActivity(intent);
    }

    public void verDibujo(View v){
        Intent intent = new Intent(this, dibujo.class);
        startActivity(intent);
    }

    public void regresar(View v){
        Intent ventanaPrincipal = new Intent(this, MainActivity.class);
        startActivity(ventanaPrincipal);
    }
}