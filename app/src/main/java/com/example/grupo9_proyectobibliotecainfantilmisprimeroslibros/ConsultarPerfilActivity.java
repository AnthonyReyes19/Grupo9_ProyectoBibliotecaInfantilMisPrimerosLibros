package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ConsultarPerfilActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView txtNombre, txtEdad, txtIntereses;
    private Button btnEditar, btnEliminar, btnVolver;
    private String perfilId;
    private String usuarioId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_perfil);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // Inicializar vistas
        imgAvatar = findViewById(R.id.imgAvatarConsulta);
        txtNombre = findViewById(R.id.txtNombreConsulta);
        txtEdad = findViewById(R.id.txtEdadConsulta);
        txtIntereses = findViewById(R.id.txtInteresesConsulta);

        // Inicializar botones
        btnVolver = findViewById(R.id.btnVolverConsulta);
        btnEditar = findViewById(R.id.btnEditarConsulta);
        btnEliminar = findViewById(R.id.btnEliminarConsulta);

        // Configurar listeners de botones
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        if (btnEditar != null) {
            btnEditar.setOnClickListener(v -> editarPerfil());
        }

        if (btnEliminar != null) {
            btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());
        }

        // Obtener usuario actual primero
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            usuarioId = user.getUid();
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Opción 1: Obtener datos desde Intent (si vienen de MenuPerfilInfantilActivity)
        if (getIntent().hasExtra("perfilId")) {
            perfilId = getIntent().getStringExtra("perfilId");
            String nombre = getIntent().getStringExtra("nombre");
            int edad = getIntent().getIntExtra("edad", 0);
            String avatar = getIntent().getStringExtra("avatar");
            ArrayList<String> intereses = getIntent().getStringArrayListExtra("intereses");

            // Mostrar datos directamente si vienen del Intent
            if (nombre != null && avatar != null) {
                mostrarDatos(nombre, edad, avatar, intereses);
                return;
            }
        }

        // Opción 2: Cargar desde SharedPreferences
        perfilId = sharedPreferences.getString("perfil_infantil_id", null);

        if (perfilId == null) {
            Toast.makeText(this, "No se encontró el perfil seleccionado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar datos desde Firebase
        cargarPerfilDesdeFirebase();
    }

    private void cargarPerfilDesdeFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("perfiles")
                .child(usuarioId)
                .child(perfilId);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    Long edadLong = snapshot.child("edad").getValue(Long.class);
                    int edad = edadLong != null ? edadLong.intValue() : 0;
                    String avatar = snapshot.child("avatar").getValue(String.class);

                    // Obtener intereses
                    ArrayList<String> intereses = new ArrayList<>();
                    DataSnapshot interesesSnapshot = snapshot.child("intereses");
                    if (interesesSnapshot.exists()) {
                        for (DataSnapshot interesSnapshot : interesesSnapshot.getChildren()) {
                            String interes = interesSnapshot.getValue(String.class);
                            if (interes != null) {
                                intereses.add(interes);
                            }
                        }
                    }

                    mostrarDatos(nombre, edad, avatar, intereses);
                } else {
                    Toast.makeText(ConsultarPerfilActivity.this,
                            "No se encontraron datos del perfil",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ConsultarPerfilActivity.this,
                        "Error al cargar datos: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatos(String nombre, int edad, String avatar, ArrayList<String> intereses) {
        // Mostrar nombre
        txtNombre.setText("Nombre: " + nombre);

        // Mostrar edad
        txtEdad.setText("Edad: " + edad + " años");

        // Mostrar avatar
        int avatarResId = obtenerResourceAvatar(avatar);
        imgAvatar.setImageResource(avatarResId);

        // Mostrar intereses
        if (intereses != null && !intereses.isEmpty()) {
            StringBuilder interesesStr = new StringBuilder("Intereses: ");
            for (int i = 0; i < intereses.size(); i++) {
                interesesStr.append(intereses.get(i));
                if (i < intereses.size() - 1) {
                    interesesStr.append(", ");
                }
            }
            txtIntereses.setText(interesesStr.toString());
        } else {
            txtIntereses.setText("Intereses: No especificados");
        }
    }

    private int obtenerResourceAvatar(String avatarNombre) {
        if (avatarNombre == null) return R.drawable.avatar_nino1;

        switch (avatarNombre) {
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

    // Método público para el onClick del XML (si prefieres usar android:onClick)
    public void regresarMenuPerfilInfantil(View view) {
        finish();
    }

    private void editarPerfil() {
        if (perfilId == null || usuarioId == null) {
            Toast.makeText(this, "Error: Datos del perfil no disponibles", Toast.LENGTH_SHORT).show();
            android.util.Log.e("ConsultarPerfil", "editarPerfil - usuarioId: " + usuarioId + ", perfilId: " + perfilId);
            return;
        }

        android.util.Log.d("ConsultarPerfil", "Iniciando edición - usuarioId: " + usuarioId + ", perfilId: " + perfilId);

        Intent intent = new Intent(ConsultarPerfilActivity.this, EditarPerfilInfantilActivity.class);
        intent.putExtra("usuarioId", usuarioId);
        intent.putExtra("perfilId", perfilId);
        startActivity(intent);
    }

    private void mostrarDialogoEliminar() {
        if (perfilId == null || usuarioId == null) {
            Toast.makeText(this, "Error: Datos del perfil no disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar Perfil")
                .setMessage("¿Estás seguro de eliminar este perfil? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> eliminarPerfil())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarPerfil() {
        // Crear la referencia correcta usando los IDs disponibles
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("perfiles")
                .child(usuarioId)
                .child(perfilId);

        dbRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ConsultarPerfilActivity.this,
                            "Perfil eliminado correctamente",
                            Toast.LENGTH_SHORT).show();

                    // Limpiar las preferencias ya que el perfil fue eliminado
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("perfil_infantil_id");
                    editor.remove("perfil_infantil_nombre");
                    editor.remove("perfil_infantil_edad");
                    editor.remove("perfil_infantil_avatar");
                    editor.apply();

                    // Redirigir a la selección de perfil
                    Intent intent = new Intent(ConsultarPerfilActivity.this, SeleccionarPerfil.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ConsultarPerfilActivity.this,
                            "Error al eliminar: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos del perfil por si se editó
        if (perfilId != null && usuarioId != null) {
            cargarPerfilDesdeFirebase();
        }
    }
}