package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            dbRef = FirebaseDatabase.getInstance().getReference("perfiles").child(userId);

            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listaPerfiles.clear();
                    RegistroPerfilInfantil perfil = snapshot.getValue(RegistroPerfilInfantil.class);
                    if (perfil != null) {
                        listaPerfiles.add(new PerfilItem(userId, perfil));
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
