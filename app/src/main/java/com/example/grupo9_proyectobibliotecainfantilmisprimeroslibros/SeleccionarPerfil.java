package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SeleccionarPerfil extends AppCompatActivity {

    private LinearLayout containerPerfiles;
    private Button btnCrearNuevoPerfil, btnContinuarComoAdulto;
    private DatabaseReference dbRef;
    private List<RegistroPerfilInfantil> perfilesInfantiles;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_perfil);

        // Inicializar Firebase
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = usuarioActual.getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("perfiles").child(userId);

        // Inicializar vistas
        containerPerfiles = findViewById(R.id.containerPerfiles);
        btnCrearNuevoPerfil = findViewById(R.id.btnCrearNuevoPerfil);
        btnContinuarComoAdulto = findViewById(R.id.btnContinuarComoAdulto);
        perfilesInfantiles = new ArrayList<>();
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // Configurar botones
        btnCrearNuevoPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(SeleccionarPerfil.this, RegistrarPerfilInfantilActivity.class);
            startActivity(intent);
        });

        btnContinuarComoAdulto.setOnClickListener(v -> {
            // Guardar preferencia de sesión como adulto
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("tipo_usuario", "adulto");
            editor.apply();

            // Ir a la actividad principal
            Intent intent = new Intent(SeleccionarPerfil.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Cargar perfiles infantiles
        cargarPerfilesInfantiles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar perfiles cuando se regresa a esta actividad
        cargarPerfilesInfantiles();
    }

    private void cargarPerfilesInfantiles() {
        perfilesInfantiles.clear();
        containerPerfiles.removeAllViews();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot perfilSnapshot : dataSnapshot.getChildren()) {
                    try {
                        // Verificar si es un nodo de perfil completo
                        if (perfilSnapshot.hasChild("nombre") && perfilSnapshot.hasChild("edad") && perfilSnapshot.hasChild("avatar")) {
                            // Extraer los datos manualmente para evitar problemas de conversión
                            String perfilId = perfilSnapshot.getKey();
                            String nombre = perfilSnapshot.child("nombre").getValue(String.class);
                            Long edadLong = perfilSnapshot.child("edad").getValue(Long.class);
                            int edad = edadLong != null ? edadLong.intValue() : 0;
                            String avatar = perfilSnapshot.child("avatar").getValue(String.class);

                            // Crear objeto de perfil
                            RegistroPerfilInfantil perfil = new RegistroPerfilInfantil();
                            perfil.setNombre(nombre);
                            perfil.setEdad(edad);
                            perfil.setAvatar(avatar);
                            perfil.setId(perfilId);

                            // Obtener intereses si existen
                            DataSnapshot interesesSnapshot = perfilSnapshot.child("intereses");
                            if (interesesSnapshot.exists()) {
                                List<String> intereses = new ArrayList<>();
                                for (DataSnapshot interesSnapshot : interesesSnapshot.getChildren()) {
                                    String interes = interesSnapshot.getValue(String.class);
                                    if (interes != null) {
                                        intereses.add(interes);
                                    }
                                }
                                perfil.setIntereses(intereses);
                            }

                            perfilesInfantiles.add(perfil);
                            agregarVistaPerfilInfantil(perfil);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Mostrar mensaje si no hay perfiles
                if (perfilesInfantiles.isEmpty()) {
                    mostrarMensajeNoPerfiles();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SeleccionarPerfil.this,
                        "Error al cargar perfiles: " + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void agregarVistaPerfilInfantil(RegistroPerfilInfantil perfil) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_perfil_infantil, containerPerfiles, false);

        ImageView imgAvatar = itemView.findViewById(R.id.imageAvatar);
        TextView txtNombre = itemView.findViewById(R.id.textNombre);
        TextView txtEdad = itemView.findViewById(R.id.textEdad);
        Button btnSeleccionar = itemView.findViewById(R.id.btnSeleccionar);

        // Configurar vista con datos del perfil
        switch (perfil.getAvatar()) {
            case "avatar1":
                imgAvatar.setImageResource(R.drawable.avatar_nino1);
                break;
            case "avatar2":
                imgAvatar.setImageResource(R.drawable.avatar_nino2);
                break;
            case "avatar3":
                imgAvatar.setImageResource(R.drawable.avatar_nino3);
                break;
            default:
                imgAvatar.setImageResource(R.drawable.avatar_nino1);
                break;
        }

        txtNombre.setText(perfil.getNombre());
        txtEdad.setText(perfil.getEdad() + " años");

        btnSeleccionar.setOnClickListener(v -> {
            // Guardar selección de perfil en SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("tipo_usuario", "infantil");
            editor.putString("perfil_infantil_id", perfil.getId());
            editor.putString("perfil_infantil_nombre", perfil.getNombre());
            editor.putInt("perfil_infantil_edad", perfil.getEdad());
            editor.putString("perfil_infantil_avatar", perfil.getAvatar());
            editor.apply();

            // Ir a la actividad principal
            Intent intent = new Intent(SeleccionarPerfil.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        containerPerfiles.addView(itemView);
    }

    private void mostrarMensajeNoPerfiles() {
        TextView txtMensaje = new TextView(this);
        txtMensaje.setText("No hay perfiles infantiles. ¡Crea uno nuevo!");
        txtMensaje.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtMensaje.setPadding(16, 32, 16, 32);
        containerPerfiles.addView(txtMensaje);
    }
}