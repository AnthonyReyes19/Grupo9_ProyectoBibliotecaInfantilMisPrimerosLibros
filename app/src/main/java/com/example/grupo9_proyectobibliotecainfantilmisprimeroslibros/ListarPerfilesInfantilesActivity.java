package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListarPerfilesInfantilesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PerfilAdapter perfilAdapter;
    private List<PerfilItem> listaPerfiles;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_perfiles);

        recyclerView = findViewById(R.id.recyclerViewPerfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaPerfiles = new ArrayList<>();
        perfilAdapter = new PerfilAdapter(listaPerfiles, this);
        recyclerView.setAdapter(perfilAdapter);

        dbRef = FirebaseDatabase.getInstance().getReference("perfiles");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listaPerfiles.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    RegistroPerfilInfantil perfil = data.getValue(RegistroPerfilInfantil.class);
                    listaPerfiles.add(new PerfilItem(data.getKey(), perfil));
                }
                perfilAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ListarPerfilesInfantilesActivity.this, "Error al cargar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

