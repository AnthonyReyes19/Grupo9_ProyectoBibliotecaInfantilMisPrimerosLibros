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

public class ConsultarPerfilActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PerfilAdapter adapter;
    private List<PerfilItem> listaPerfiles;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_perfiles);

        recyclerView = findViewById(R.id.recyclerViewPerfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaPerfiles = new ArrayList<>();
        adapter = new PerfilAdapter(listaPerfiles, this);
        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("perfiles");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPerfiles.clear();
                for (DataSnapshot perfilSnapshot : snapshot.getChildren()) {
                    RegistroPerfilInfantil perfil = perfilSnapshot.getValue(RegistroPerfilInfantil.class);
                    String key = perfilSnapshot.getKey();
                    if (perfil != null && key != null) {
                        listaPerfiles.add(new PerfilItem(key, perfil));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConsultarPerfilActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
