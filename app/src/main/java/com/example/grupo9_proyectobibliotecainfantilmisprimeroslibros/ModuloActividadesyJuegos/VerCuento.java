package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.*;



public class VerCuento extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Cuento> listaCuentos = new ArrayList<>();
    private CuentoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_cuento);

        recyclerView = findViewById(R.id.recyclerViewCuentos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CuentoAdapter(listaCuentos, this);
        recyclerView.setAdapter(adapter);

        cargarCuentosDesdeFirebase();
    }

    private void cargarCuentosDesdeFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                .child("actividades")
                .child("cuentos_registrados");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listaCuentos.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Cuento cuento = ds.getValue(Cuento.class);
                    cuento.setId(ds.getKey());
                    listaCuentos.add(cuento);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
