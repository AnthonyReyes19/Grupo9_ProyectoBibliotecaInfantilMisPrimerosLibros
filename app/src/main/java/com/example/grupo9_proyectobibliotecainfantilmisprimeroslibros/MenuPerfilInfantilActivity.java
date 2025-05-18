package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPerfilInfantilActivity extends AppCompatActivity {

    private CardView cardVerPerff, cardEditarPerff, cardEliminarPerff;
    private String perfilId = "perfil_infantil_001";
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_perfilinfantil);

        databaseRef = FirebaseDatabase.getInstance().getReference("perfiles").child(perfilId);


        cardVerPerff = findViewById(R.id.cardVerPerfil);
        cardEditarPerff = findViewById(R.id.cardEditarPerfil);
        cardEliminarPerff = findViewById(R.id.cardEliminarPerfil);


        cardVerPerff.setOnClickListener(v -> cargarPerfil());
        cardEditarPerff.setOnClickListener(v -> editarPerfil());
        cardEliminarPerff.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    private void cargarPerfil() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    int edad = snapshot.child("edad").getValue(Integer.class);
                    String genero = snapshot.child("genero").getValue(String.class);

                    Intent intent = new Intent(MenuPerfilInfantilActivity.this, ConsultarPerfilActivity.class);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("edad", edad);
                    intent.putExtra("genero", genero);
                    startActivity(intent);
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
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombreActual = snapshot.child("nombre").getValue(String.class);
                    int edadActual = snapshot.child("edad").getValue(Integer.class);
                    String generoActual = snapshot.child("genero").getValue(String.class);

                    Intent intent = new Intent(MenuPerfilInfantilActivity.this, EditarPerfilInfantilActivity.class);
                    intent.putExtra("nombre", nombreActual);
                    intent.putExtra("edad", edadActual);
                    intent.putExtra("genero", generoActual);
                    startActivityForResult(intent, 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuPerfilInfantilActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
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
                    finish(); // Cierra la actividad actual
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String nuevoNombre = data.getStringExtra("nuevoNombre");
            int nuevaEdad = data.getIntExtra("nuevaEdad", 0);
            String nuevoGenero = data.getStringExtra("nuevoGenero");

            databaseRef.child("nombre").setValue(nuevoNombre);
            databaseRef.child("edad").setValue(nuevaEdad);
            databaseRef.child("genero").setValue(nuevoGenero);

            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
        }
    }
}