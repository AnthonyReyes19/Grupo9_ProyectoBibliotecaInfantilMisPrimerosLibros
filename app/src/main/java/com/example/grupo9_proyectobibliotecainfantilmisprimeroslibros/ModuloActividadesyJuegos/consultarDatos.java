package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import java.util.Map;
import java.util.HashMap;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

public class consultarDatos extends AppCompatActivity {
    private String idActual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consultar_datos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void consultarDatosDB(View v){
        EditText tituloBuscar = findViewById(R.id.cons_txtTitulo);
        String tituloInput = tituloBuscar.getText().toString().trim();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                .child("actividades")
                .child("cuentos_registrados");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean encontrado = false;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    idActual = ds.getKey();
                    String titulo = ds.child("titulo").getValue(String.class);
                    if (titulo != null && titulo.equalsIgnoreCase(tituloInput)) {
                        String autor = ds.child("autor").getValue(String.class);

                        ((TextView) findViewById(R.id.cons_lblTitulo)).setText(titulo);
                        ((TextView) findViewById(R.id.cons_lblAutor)).setText(autor);
                        Toast.makeText(consultarDatos.this, "Registro encontrado", Toast.LENGTH_SHORT).show();
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    Toast.makeText(consultarDatos.this, "No se encontró ese cuento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(consultarDatos.this, "Error al leer datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void actualizarCuento(View v) {
        if (idActual == null) {
            Toast.makeText(this, "Primero consulta un cuento", Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevoTitulo = ((EditText) findViewById(R.id.cons_lblTitulo)).getText().toString();
        String nuevoAutor = ((EditText) findViewById(R.id.cons_lblAutor)).getText().toString();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                .child("actividades")
                .child("cuentos_registrados")
                .child(idActual);

        Map<String, Object> nuevosDatos = new HashMap<>();
        nuevosDatos.put("titulo", nuevoTitulo);
        nuevosDatos.put("autor", nuevoAutor);


        dbRef.updateChildren(nuevosDatos)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cuento actualizado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                });
    }




    public void eliminarCuento(View v) {
        if (idActual == null) {
            Toast.makeText(this, "Primero consulta un cuento", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                .child("actividades")
                .child("cuentos_registrados")
                .child(idActual);

        dbRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cuento eliminado", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    idActual = null;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                });
    }

    private void limpiarCampos() {
        ((EditText) findViewById(R.id.cons_lblTitulo)).setText("");
        ((EditText) findViewById(R.id.cons_lblAutor)).setText("");
    }


    public void regresar(View v){
        Intent ventanaRegistro = new Intent(this, formularioCuento.class);
        startActivity(ventanaRegistro);
    }

}