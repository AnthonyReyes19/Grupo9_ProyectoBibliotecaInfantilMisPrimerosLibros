package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPerfilInfantilActivity extends AppCompatActivity {

    private CardView cardRegistrarPerff, cardVerPerff, cardEditarPerff, cardEliminarPerff;
    private String usuarioId;
    private String perfilSeleccionadoId;
    private DatabaseReference databaseRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_perfilinfantil);

        // Obtener el ID del usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            usuarioId = user.getUid();
            databaseRef = FirebaseDatabase.getInstance().getReference("perfiles").child(usuarioId);
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obtener preferencias para saber qué perfil infantil está seleccionado
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        perfilSeleccionadoId = sharedPreferences.getString("perfil_infantil_id", null);

        if (perfilSeleccionadoId == null) {
            Toast.makeText(this, "No hay un perfil infantil seleccionado", Toast.LENGTH_SHORT).show();
            // Redirigir a la selección de perfil si no hay uno seleccionado
            Intent intent = new Intent(MenuPerfilInfantilActivity.this, SeleccionarPerfil.class);
            startActivity(intent);
            finish();
            return;
        }

        // Inicializar vistas
        cardRegistrarPerff = findViewById(R.id.cardRegistrarPerfil);
        cardVerPerff = findViewById(R.id.cardVerPerfil);
        cardEditarPerff = findViewById(R.id.cardEditarPerfil);
        cardEliminarPerff = findViewById(R.id.cardEliminarPerfil);

        // Configurar listeners
        cardRegistrarPerff.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPerfilInfantilActivity.this, RegistrarPerfilInfantilActivity.class);
            startActivity(intent);
        });

        cardVerPerff.setOnClickListener(v -> cargarPerfil());
        cardEditarPerff.setOnClickListener(v -> editarPerfil());
        cardEliminarPerff.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    private void cargarPerfil() {
        // Usar el ID del perfil seleccionado para cargar específicamente ese perfil
        databaseRef.child(perfilSeleccionadoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        // Extraer datos manualmente para evitar errores de conversión
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        Long edadLong = snapshot.child("edad").getValue(Long.class);
                        int edad = edadLong != null ? edadLong.intValue() : 0;
                        String avatar = snapshot.child("avatar").getValue(String.class);

                        // Extraer intereses
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

                        // Iniciar actividad para mostrar los detalles del perfil
                        Intent intent = new Intent(MenuPerfilInfantilActivity.this, ConsultarPerfilActivity.class);
                        intent.putExtra("perfilId", perfilSeleccionadoId);
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("edad", edad);
                        intent.putExtra("avatar", avatar);
                        intent.putStringArrayListExtra("intereses", intereses);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MenuPerfilInfantilActivity.this,
                                "Error al procesar datos: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MenuPerfilInfantilActivity.this,
                            "Perfil no encontrado. Puede haber sido eliminado.",
                            Toast.LENGTH_SHORT).show();

                    // Redirigir a selección de perfil si el perfil ya no existe
                    Intent intent = new Intent(MenuPerfilInfantilActivity.this, SeleccionarPerfil.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuPerfilInfantilActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editarPerfil() {
        // Pasar el ID del perfil seleccionado a la actividad de edición
        Intent intent = new Intent(MenuPerfilInfantilActivity.this, EditarPerfilInfantilActivity.class);
        intent.putExtra("perfilId", perfilSeleccionadoId);
        intent.putExtra("usuarioId", usuarioId);
        startActivity(intent);
    }

    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Perfil")
                .setMessage("¿Estás seguro de eliminar este perfil? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> eliminarPerfil())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarPerfil() {
        // Eliminar el perfil seleccionado específicamente
        databaseRef.child(perfilSeleccionadoId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MenuPerfilInfantilActivity.this,
                            "Perfil eliminado correctamente",
                            Toast.LENGTH_SHORT).show();

                    // Limpiar las preferencias ya que el perfil fue eliminado
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("perfil_infantil_id");
                    editor.remove("perfil_infantil_nombre");
                    editor.remove("perfil_infantil_edad");
                    editor.remove("perfil_infantil_avatar");
                    // Mantener el tipo de usuario como infantil para que vaya a la pantalla de selección
                    editor.apply();

                    // Redirigir a la selección de perfil para elegir otro o crear uno nuevo
                    Intent intent = new Intent(MenuPerfilInfantilActivity.this, SeleccionarPerfil.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MenuPerfilInfantilActivity.this,
                            "Error al eliminar: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verificar si todavía hay un perfil seleccionado al volver a esta actividad
        if (perfilSeleccionadoId == null) {
            // Redirigir a la selección de perfil
            Intent intent = new Intent(MenuPerfilInfantilActivity.this, SeleccionarPerfil.class);
            startActivity(intent);
            finish();
        }
    }
}