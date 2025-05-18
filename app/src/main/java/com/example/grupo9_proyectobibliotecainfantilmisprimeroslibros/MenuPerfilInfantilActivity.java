package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;
import java.util.ArrayList;
import android.content.Intent;
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
    private String perfilId;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_perfilinfantil);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            perfilId = user.getUid();
            databaseRef = FirebaseDatabase.getInstance().getReference("perfiles").child(perfilId);
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cardRegistrarPerff = findViewById(R.id.cardRegistrarPerfil);
        cardVerPerff = findViewById(R.id.cardVerPerfil);
        cardEditarPerff = findViewById(R.id.cardEditarPerfil);
        cardEliminarPerff = findViewById(R.id.cardEliminarPerfil);

        cardRegistrarPerff.setOnClickListener(v -> {
            Intent intent = new Intent(MenuPerfilInfantilActivity.this, RegistrarPerfilInfantilActivity.class);
            startActivity(intent);
        });

        cardVerPerff.setOnClickListener(v -> cargarPerfil());
        cardEditarPerff.setOnClickListener(v -> editarPerfil());
        cardEliminarPerff.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    /*private void cargarPerfil() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RegistroPerfilInfantil perfil = snapshot.getValue(RegistroPerfilInfantil.class);
                    if (perfil != null) {
                        Intent intent = new Intent(MenuPerfilInfantilActivity.this, ConsultarPerfilActivity.class);
                        intent.putExtra("nombre", perfil.getNombre());
                        intent.putExtra("edad", perfil.getEdad());
                        intent.putExtra("avatar", perfil.getAvatar());
                        intent.putStringArrayListExtra("intereses", new ArrayList<>(perfil.getIntereses()));
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(MenuPerfilInfantilActivity.this, "Perfil no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuPerfilInfantilActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/
    private void cargarPerfil() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RegistroPerfilInfantil perfil = snapshot.getValue(RegistroPerfilInfantil.class);
                    if (perfil != null) {
                        ArrayList<String> intereses = perfil.getIntereses() != null ?
                                new ArrayList<>(perfil.getIntereses()) : new ArrayList<>();

                        Intent intent = new Intent(MenuPerfilInfantilActivity.this, ConsultarPerfilActivity.class);
                        intent.putExtra("nombre", perfil.getNombre());
                        intent.putExtra("edad", perfil.getEdad());
                        intent.putExtra("avatar", perfil.getAvatar());
                        intent.putStringArrayListExtra("intereses", intereses);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(MenuPerfilInfantilActivity.this, "Perfil no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuPerfilInfantilActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void editarPerfil() {
        Intent intent = new Intent(MenuPerfilInfantilActivity.this, EditarPerfilInfantilActivity.class);
        intent.putExtra("perfilId", perfilId);
        startActivity(intent);
    }

    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Perfil")
                .setMessage("¿Estás seguro de eliminar este perfil?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarPerfil())
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarPerfil() {
        databaseRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Perfil eliminado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
