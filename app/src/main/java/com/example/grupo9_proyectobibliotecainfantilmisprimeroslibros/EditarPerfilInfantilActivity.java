package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class EditarPerfilInfantilActivity extends AppCompatActivity {

    private EditText editNombre, editEdad;
    private CheckBox cbCuentos, cbAventura, cbCiencia, cbAnimales;
    private ImageButton avatarOption1, avatarOption2, avatarOption3;
    private Button btnActualizar;
    private String selectedAvatar;
    private String perfilId;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_infantil);
        initViews();

        perfilId = getIntent().getStringExtra("perfilId");
        dbRef = FirebaseDatabase.getInstance().getReference("perfiles").child(perfilId);
        cargarDatosPerfil();
        configurarListeners();
    }

    private void initViews() {
        editNombre = findViewById(R.id.editNombreEditar);
        editEdad = findViewById(R.id.editEdadEditar);
        cbCuentos = findViewById(R.id.cbCuentosEditar);
        cbAventura = findViewById(R.id.cbAventuraEditar);
        cbCiencia = findViewById(R.id.cbCienciaEditar);
        cbAnimales = findViewById(R.id.cbAnimalesEditar);
        avatarOption1 = findViewById(R.id.avatarOption1Editar);
        avatarOption2 = findViewById(R.id.avatarOption2Editar);
        avatarOption3 = findViewById(R.id.avatarOption3Editar);
        btnActualizar = findViewById(R.id.btnActualizarPerfil);
    }

    private void cargarDatosPerfil() {
        dbRef.get().addOnSuccessListener(snapshot -> {
            RegistroPerfilInfantil perfil = snapshot.getValue(RegistroPerfilInfantil.class);
            if (perfil != null) {
                editNombre.setText(perfil.getNombre());
                editEdad.setText(String.valueOf(perfil.getEdad()));

                // Verifica si el avatar es nulo antes de asignarlo
                selectedAvatar = perfil.getAvatar();
                if (selectedAvatar == null) {
                    selectedAvatar = "avatar1"; // Valor predeterminado
                }

                resaltarAvatarSeleccionado();

                if (perfil.getIntereses() != null) {
                    for (String interes : perfil.getIntereses()) {
                        switch (interes) {
                            case "Cuentos": cbCuentos.setChecked(true); break;
                            case "Aventura": cbAventura.setChecked(true); break;
                            case "Ciencia": cbCiencia.setChecked(true); break;
                            case "Animales": cbAnimales.setChecked(true); break;
                        }
                    }
                }
            }
        });
    }

    private void configurarListeners() {
        avatarOption1.setOnClickListener(v -> {
            selectedAvatar = "avatar1";
            resaltarAvatarSeleccionado();
        });

        avatarOption2.setOnClickListener(v -> {
            selectedAvatar = "avatar2";
            resaltarAvatarSeleccionado();
        });

        avatarOption3.setOnClickListener(v -> {
            selectedAvatar = "avatar3";
            resaltarAvatarSeleccionado();
        });
        btnActualizar.setOnClickListener(v -> actualizarPerfil());
    }

    private void resaltarAvatarSeleccionado() {
        avatarOption1.setAlpha(0.5f);
        avatarOption2.setAlpha(0.5f);
        avatarOption3.setAlpha(0.5f);

        // Verifica si selectedAvatar es nulo antes de usarlo
        if (selectedAvatar == null) {
            // Establece un valor predeterminado o simplemente retorna
            selectedAvatar = "avatar1"; // valor predeterminado
            avatarOption1.setAlpha(1.0f);
            return;
        }

        switch (selectedAvatar) {
            case "avatar1":
                avatarOption1.setAlpha(1.0f);
                break;
            case "avatar2":
                avatarOption2.setAlpha(1.0f);
                break;
            case "avatar3":
                avatarOption3.setAlpha(1.0f);
                break;
        }
    }

    private void actualizarPerfil() {
        String nombre = editNombre.getText().toString().trim();
        String edadStr = editEdad.getText().toString().trim();

        if (nombre.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Nombre y edad son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedAvatar == null || selectedAvatar.isEmpty()) {
            Toast.makeText(this, "Selecciona un avatar", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int edad = Integer.parseInt(edadStr);
            List<String> intereses = obtenerInteresesSeleccionados();

            RegistroPerfilInfantil perfilActualizado = new RegistroPerfilInfantil(
                    nombre,
                    edad,
                    selectedAvatar,
                    intereses
            );

            dbRef.setValue(perfilActualizado)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Edad debe ser un número válido", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> obtenerInteresesSeleccionados() {
        List<String> intereses = new ArrayList<>();
        if (cbCuentos.isChecked()) intereses.add("Cuentos");
        if (cbAventura.isChecked()) intereses.add("Aventura");
        if (cbCiencia.isChecked()) intereses.add("Ciencia");
        if (cbAnimales.isChecked()) intereses.add("Animales");
        return intereses;
    }
}