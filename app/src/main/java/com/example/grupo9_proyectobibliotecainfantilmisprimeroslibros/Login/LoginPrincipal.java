package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.RegistroPerfilInfantil;

public class LoginPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(v -> MostrarDialogo());

    }

    public void MostrarDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_profile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnMaestro = dialogView.findViewById(R.id.btnPerfilMaestro);
        Button btnInfantil = dialogView.findViewById(R.id.btnPerfilInfantil);

        //btnMaestro.setOnClickListener(v -> {
            // Cambia "RegistroMaestroActivity.class" por tu actividad
            //Intent intent = new Intent(this, RegistroMaestroActivity.class);
            //startActivity(intent);
            //dialog.dismiss();
        //});

        btnInfantil.setOnClickListener(v -> {

            Intent intent = new Intent(this, RegistroPerfilInfantil.class);
            startActivity(intent);
            dialog.dismiss();
        });
    }


}