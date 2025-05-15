package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.MainActivity;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos.loginPrincipalModuloReyes;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void RegresarAdminAMain(View v){
        Intent ventana = new Intent(this, MainActivity.class);
        startActivity(ventana);
    }

}