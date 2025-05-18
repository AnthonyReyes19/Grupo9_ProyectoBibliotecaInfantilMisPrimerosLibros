package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

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

    private static final String TAG = "AdministracionLibros";

    RecyclerView mRecycler;
    LibrosAdapter mAdapter;
    FirebaseFirestore mFirestore;
    ImageButton btnAgregar;

    // Para evitar problemas de concurrencia
    private boolean isInitialSetup = true;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administracion_libros);

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.ViewLibros);

        // Configurar el RecyclerView con un tamaño fijo para mayor estabilidad
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setItemAnimator(null);

        setupRecyclerView();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupRecyclerView() {
        try {
            Query query = mFirestore.collection("libros");

            FirestoreRecyclerOptions<Libros> firestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Libros>()
                            .setQuery(query, Libros.class)
                            .build();

            mAdapter = new LibrosAdapter(firestoreRecyclerOptions, this);
            mRecycler.setAdapter(mAdapter);
            mRecycler.setItemAnimator(null);

            Log.d(TAG, "RecyclerView setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            try {
                mAdapter.startListening();
            } catch (Exception e) {
                Log.e(TAG, "Error starting adapter", e);
                // Intentar reiniciar el adaptador si hay un error
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recreateAdapter();
                    }
                }, 200);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            try {
                mAdapter.stopListening();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping adapter", e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Solo recrear el adaptador si estamos volviendo de otra actividad
        if (!isInitialSetup) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recreateAdapter();
                }
            }, 300); // Pequeño retraso para asegurar que la actividad esté completamente reanudada
        }
        isInitialSetup = false;
    }

    // Método para reiniciar el adaptador cuando sea necesario
    private void recreateAdapter() {
        try {
            // Detener y liberar el adaptador actual
            if (mAdapter != null) {
                mAdapter.stopListening();
            }

            // Crear un nuevo adaptador
            setupRecyclerView();

            // Iniciar la escucha
            if (mAdapter != null) {
                mAdapter.startListening();
            }

            Log.d(TAG, "Adapter recreated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error recreating adapter", e);
        }
    }
}