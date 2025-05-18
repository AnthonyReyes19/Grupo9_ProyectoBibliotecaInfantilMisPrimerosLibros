package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        initViews();
        initFirebase();
        cargarPerfiles();
    }

    private void initViews() {
        containerPerfiles = findViewById(R.id.containerPerfiles);
        btnCrearNuevoPerfil = findViewById(R.id.btnCrearNuevoPerfil);
        btnContinuarComoAdulto = findViewById(R.id.btnContinuarComoAdulto);

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        btnCrearNuevoPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistrarPerfilInfantilActivity.class);
            startActivity(intent);
        });

        btnContinuarComoAdulto.setOnClickListener(v -> {
            // Guardar que continúa como adulto
            guardarTipoUsuario("adulto", null);
            irAMainActivity();
        });
    }

    private void initFirebase() {
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual != null) {
            String userId = usuarioActual.getUid();
            dbRef = FirebaseDatabase.getInstance().getReference("perfiles").child(userId);
        }
        perfilesInfantiles = new ArrayList<>();
    }

    private void cargarPerfiles() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                perfilesInfantiles.clear();
                containerPerfiles.removeAllViews();

                for (DataSnapshot perfilSnapshot : dataSnapshot.getChildren()) {
                    try {
                        // Verificar si el hijo es un objeto (perfil) o un valor primitivo
                        if (perfilSnapshot.hasChildren()) {
                            RegistroPerfilInfantil perfil = perfilSnapshot.getValue(RegistroPerfilInfantil.class);
                            if (perfil != null) {
                                perfil.setId(perfilSnapshot.getKey());
                                perfilesInfantiles.add(perfil);
                                agregarVistaPerfilInfantil(perfil);
                            }
                        } else {
                            // Si es un valor primitivo, crear un perfil básico
                            String nombrePerfil = perfilSnapshot.getValue(String.class);
                            if (nombrePerfil != null) {
                                RegistroPerfilInfantil perfil = new RegistroPerfilInfantil();
                                perfil.setId(perfilSnapshot.getKey());
                                perfil.setNombre(nombrePerfil);
                                perfil.setEdad(5); // Edad por defecto
                                perfil.setAvatar("avatar1"); // Avatar por defecto
                                perfil.setIntereses(new ArrayList<>());
                                perfilesInfantiles.add(perfil);
                                agregarVistaPerfilInfantil(perfil);
                            }
                        }
                    } catch (Exception e) {
                        // Log del error pero continúa procesando otros perfiles
                        e.printStackTrace();
                        Toast.makeText(SeleccionarPerfil.this,
                                "Error al cargar un perfil: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                // Si no hay perfiles, mostrar mensaje
                if (perfilesInfantiles.isEmpty()) {
                    mostrarMensajeSinPerfiles();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SeleccionarPerfil.this,
                        "Error al cargar perfiles: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarVistaPerfilInfantil(RegistroPerfilInfantil perfil) {
        View perfilView = getLayoutInflater().inflate(R.layout.item_perfil_infantil, null);

        ImageView imageAvatar = perfilView.findViewById(R.id.imageAvatar);
        TextView textNombre = perfilView.findViewById(R.id.textNombre);
        TextView textEdad = perfilView.findViewById(R.id.textEdad);
        Button btnSeleccionar = perfilView.findViewById(R.id.btnSeleccionar);

        // Configurar avatar
        int avatarResId = getAvatarResource(perfil.getAvatar());
        imageAvatar.setImageResource(avatarResId);

        textNombre.setText(perfil.getNombre());
        textEdad.setText(perfil.getEdad() + " años");

        btnSeleccionar.setOnClickListener(v -> {
            // Guardar perfil seleccionado y tipo de usuario
            guardarTipoUsuario("infantil", perfil);
            irAMainActivity();
        });

        containerPerfiles.addView(perfilView);
    }

    private void mostrarMensajeSinPerfiles() {
        TextView mensaje = new TextView(this);
        mensaje.setText("No hay perfiles infantiles creados.\n¡Crea uno para comenzar!");
        mensaje.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mensaje.setPadding(20, 40, 20, 40);
        containerPerfiles.addView(mensaje);
    }

    private int getAvatarResource(String avatarName) {
        switch (avatarName) {
            case "avatar1":
                return R.drawable.avatar_nino1;
            case "avatar2":
                return R.drawable.avatar_nino2;
            case "avatar3":
                return R.drawable.avatar_nino3;
            default:
                return R.drawable.avatar_nino1;
        }
    }

    private void guardarTipoUsuario(String tipoUsuario, RegistroPerfilInfantil perfil) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tipo_usuario", tipoUsuario);

        if (perfil != null) {
            editor.putString("perfil_infantil_id", perfil.getId());
            editor.putString("perfil_infantil_nombre", perfil.getNombre());
            editor.putInt("perfil_infantil_edad", perfil.getEdad());
            editor.putString("perfil_infantil_avatar", perfil.getAvatar());
        }

        editor.apply();
    }

    private void irAMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar perfiles cuando se regrese de crear un nuevo perfil
        if (dbRef != null) {
            cargarPerfiles();
        }
    }
}