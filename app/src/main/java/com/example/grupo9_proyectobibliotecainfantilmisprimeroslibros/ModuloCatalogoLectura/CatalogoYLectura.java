package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Adapter.LibrosAdapter;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Modelo.Libros;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class CatalogoYLectura extends AppCompatActivity {
    private Spinner spinnerEdad, spinnerCategoria;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private LibrosAdapter librosAdapter;

    private String filtroEdad = "Seleccionar edad...";
    private String filtroCategoria = "Seleccionar categoría...";
    private String filtroBusqueda = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo_y_lectura);

        Spinner spinnerEdad = findViewById(R.id.spinnerEdad);
        Spinner spinnerCategoria = findViewById(R.id.spinnerCategoria);
        SearchView searchView = findViewById(R.id.SearchView);
        RecyclerView recyclerViewLibros = findViewById(R.id.recyclerView);
        recyclerViewLibros.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLibros.setHasFixedSize(true);


        List<String> edadesSpinner = new ArrayList<>();
        edadesSpinner.add("Seleccionar edad..."); // Mensaje inicial
        edadesSpinner.add("3 años");
        edadesSpinner.add("4 años");
        edadesSpinner.add("5 años");
        edadesSpinner.add("6 años");
        edadesSpinner.add("7 años");

        List<String> categoriasSpinner = new ArrayList<>();
        categoriasSpinner.add("Seleccionar categoría..."); // Mensaje inicial
        categoriasSpinner.add("Fantasía");
        categoriasSpinner.add("Poesía");
        categoriasSpinner.add("Cuentos");
        categoriasSpinner.add("Aventura");


        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.control_spinner, edadesSpinner);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdad.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.control_spinner, categoriasSpinner);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter2);

        // Listeners para spinners
        spinnerEdad.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                filtroEdad = edadesSpinner.get(position);
                actualizarConsulta();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spinnerCategoria.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                filtroCategoria = categoriasSpinner.get(position);
                actualizarConsulta();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Listener para SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtroBusqueda = query.trim();
                actualizarConsulta();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtroBusqueda = newText.trim();
                actualizarConsulta();
                return true;
            }
        });

        // Carga inicial
        actualizarConsulta();

    }

    private void actualizarConsulta() {
        Query query = FirebaseFirestore.getInstance().collection("libros");

        // Aplicar filtro por edad si no es la opción por defecto
        if (!filtroEdad.equals("Seleccionar edad...")) {
            query = query.whereEqualTo("edad", filtroEdad);
        }

        // Aplicar filtro por categoría si no es la opción por defecto
        if (!filtroCategoria.equals("Seleccionar categoría...")) {
            query = query.whereEqualTo("categoria", filtroCategoria);
        }

        // Para búsqueda por título, Firestore no tiene consultas contains, pero se puede hacer una búsqueda simple por igual o con un índice, para fines demo usaremos whereEqualTo:
        if (!filtroBusqueda.isEmpty()) {
            // Si quieres búsqueda más avanzada, debes usar una solución externa o recuperar todos y filtrar en cliente
            query = query.whereEqualTo("titulo", filtroBusqueda);
        }

        FirestoreRecyclerOptions<Libros> options = new FirestoreRecyclerOptions.Builder<Libros>()
                .setQuery(query, Libros.class)
                .build();

        if (librosAdapter != null) {
            librosAdapter.stopListening();
        }

        librosAdapter = new LibrosAdapter(options, this) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0) {
                    Toast.makeText(CatalogoYLectura.this, "No se encontraron libros para los filtros aplicados.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        recyclerView.setAdapter(librosAdapter);
        librosAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (librosAdapter != null) {
            librosAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (librosAdapter != null) {
            librosAdapter.stopListening();
        }
    }
}
