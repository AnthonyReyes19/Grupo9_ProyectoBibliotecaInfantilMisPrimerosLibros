package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrarPerfilInfantilActivity extends AppCompatActivity {

    private EditText editNombre, editEdad;
    private CheckBox cbCuentos, cbAventura, cbCiencia, cbAnimales;
    private ImageButton avatarOption1, avatarOption2, avatarOption3;
    private Button btnGuardar;
    private int selectedAvatarResId = -1;
    private String selectedAvatar = "";

    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_perfil_infantil);

        //dbRef = FirebaseDatabase.getInstance().getReference("perfiles");
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        String userId = usuarioActual.getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("perfiles").child(userId);


        editNombre = findViewById(R.id.editNombre);
        editEdad = findViewById(R.id.editEdad);
        cbCuentos = findViewById(R.id.cbCuentos);
        cbAventura = findViewById(R.id.cbAventura);
        cbCiencia = findViewById(R.id.cbCiencia);
        cbAnimales = findViewById(R.id.cbAnimales);

        avatarOption1 = findViewById(R.id.avatarOption1);
        avatarOption2 = findViewById(R.id.avatarOption2);
        avatarOption3 = findViewById(R.id.avatarOption3);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);

        avatarOption1.setOnClickListener(v -> seleccionarAvatar(R.drawable.avatar_nino1, "avatar1"));
        avatarOption2.setOnClickListener(v -> seleccionarAvatar(R.drawable.avatar_nino2, "avatar2"));
        avatarOption3.setOnClickListener(v -> seleccionarAvatar(R.drawable.avatar_nino3, "avatar3"));

        btnGuardar.setOnClickListener(v -> guardarPerfil());
    }

    private void seleccionarAvatar(int resId, String nombreAvatar) {
        selectedAvatarResId = resId;
        selectedAvatar = nombreAvatar;

        avatarOption1.setAlpha(0.5f);
        avatarOption2.setAlpha(0.5f);
        avatarOption3.setAlpha(0.5f);

        if (resId == R.drawable.avatar_nino1) avatarOption1.setAlpha(1.0f);
        else if (resId == R.drawable.avatar_nino2) avatarOption2.setAlpha(1.0f);
        else if (resId == R.drawable.avatar_nino3) avatarOption3.setAlpha(1.0f);
    }

    private void guardarPerfil() {
        String nombre = editNombre.getText().toString().trim();
        String edadStr = editEdad.getText().toString().trim();

        if (nombre.isEmpty() || edadStr.isEmpty() || selectedAvatar.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos y selecciona un avatar", Toast.LENGTH_SHORT).show();
            return;
        }

        int edad = Integer.parseInt(edadStr);
        List<String> intereses = new ArrayList<>();
        if (cbCuentos.isChecked()) intereses.add("Cuentos");
        if (cbAventura.isChecked()) intereses.add("Aventura");
        if (cbCiencia.isChecked()) intereses.add("Ciencia");
        if (cbAnimales.isChecked()) intereses.add("Animales");

        RegistroPerfilInfantil perfil = new RegistroPerfilInfantil(nombre, edad, selectedAvatar, intereses);

        dbRef.setValue(perfil)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Perfil guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
