package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Adapter.LibrosAdapter;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Modelo.Libros;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdministracionLibros extends AppCompatActivity {

    RecyclerView mRecycler;
    LibrosAdapter mAdapter;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administracion_libros);

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.ViewLibros);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        Query query = mFirestore.collection("libros");
        FirestoreRecyclerOptions<Libros> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Libros>().setQuery(query, Libros.class).build() ;

        mAdapter = new LibrosAdapter(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}