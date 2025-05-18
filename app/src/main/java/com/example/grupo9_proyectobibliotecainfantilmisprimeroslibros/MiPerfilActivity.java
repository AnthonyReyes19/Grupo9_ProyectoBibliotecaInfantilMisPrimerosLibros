package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MiPerfilActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPerfiles;
    private PerfilAdapter adapter;
    private List<PerfilItem> listaPerfilItems;
    private DatabaseReference perfilesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);

        recyclerViewPerfiles = findViewById(R.id.recyclerViewPerfiles);
        recyclerViewPerfiles.setLayoutManager(new LinearLayoutManager(this));

        listaPerfilItems = new ArrayList<>();
        adapter = new PerfilAdapter(listaPerfilItems, this);
        recyclerViewPerfiles.setAdapter(adapter);

        perfilesRef = FirebaseDatabase.getInstance().getReference("perfiles");

        cargarPerfilesDesdeFirebase();
    }

    private void cargarPerfilesDesdeFirebase() {
        perfilesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPerfilItems.clear();
                for (DataSnapshot perfilSnapshot : snapshot.getChildren()) {
                    RegistroPerfilInfantil perfil = perfilSnapshot.getValue(RegistroPerfilInfantil.class);
                    String key = perfilSnapshot.getKey();

                    if (perfil != null && key != null) {
                        listaPerfilItems.add(new PerfilItem(key, perfil));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MiPerfilActivity.this, "Error al cargar perfiles: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
