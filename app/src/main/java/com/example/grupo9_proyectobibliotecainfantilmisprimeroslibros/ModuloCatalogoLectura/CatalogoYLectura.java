package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Adapter.LibrosAdapter;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Modelo.Libros;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LecturaLibros.LecturaLibroActivity;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LibrosCatalogoAdapter.LibrosCatalogoAdapter;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LibrosCatalogoModelo.LibrosCatalogoModelo;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CatalogoYLectura extends AppCompatActivity {
    private Spinner spinnerEdad, spinnerCategoria;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private LibrosCatalogoAdapter librosAdapter;

    private String filtroEdad = "Seleccionar edad...";
    private String filtroCategoria = "Seleccionar categoría...";
    private String filtroBusqueda = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo_y_lectura);

        // Vinculación de vistas
        spinnerEdad = findViewById(R.id.spinnerEdad);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        searchView = findViewById(R.id.SearchView);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Configuración de Spinners
        configurarSpinners();

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

    private void configurarSpinners() {
        List<String> edadesSpinner = new ArrayList<>();
        edadesSpinner.add("Seleccionar edad...");
        edadesSpinner.add("3");
        edadesSpinner.add("4");
        edadesSpinner.add("5");
        edadesSpinner.add("6");
        edadesSpinner.add("7");

        List<String> categoriasSpinner = new ArrayList<>();
        categoriasSpinner.add("Seleccionar categoría...");
        categoriasSpinner.add("Fantasía");
        categoriasSpinner.add("Poesía");
        categoriasSpinner.add("Cuentos");
        categoriasSpinner.add("Aventura");
        categoriasSpinner.add("Valores");
        categoriasSpinner.add("colores");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.control_spinner, edadesSpinner);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdad.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.control_spinner, categoriasSpinner);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter2);

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
    }

    private void actualizarConsulta() {
        Query query = FirebaseFirestore.getInstance().collection("libros");

        if (!filtroEdad.equals("Seleccionar edad...")) {
            query = query.whereEqualTo("edad", filtroEdad);
        }

        if (!filtroCategoria.equals("Seleccionar categoría...")) {
            query = query.whereEqualTo("categoria", filtroCategoria);
        }

        if (!filtroBusqueda.isEmpty()) {
            query = query.whereEqualTo("titulo", filtroBusqueda);
        }

        FirestoreRecyclerOptions<LibrosCatalogoModelo> options = new FirestoreRecyclerOptions.Builder<LibrosCatalogoModelo>()
                .setQuery(query, LibrosCatalogoModelo.class)
                .build();

        if (librosAdapter != null) {
            librosAdapter.stopListening();
        }

        librosAdapter = new LibrosCatalogoAdapter(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0) {
                    Toast.makeText(CatalogoYLectura.this, "No se encontraron libros para los filtros aplicados.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Configurar listener para clics en los libros
        librosAdapter.setOnLibroClickListener(libro -> {
            String pdfBase64 = libro.getPdfBase64(); // Reemplaza esto si tienes un URL directo
            if (pdfBase64 != null && !pdfBase64.isEmpty()) {
                try {
                    // Decodificar el PDF de Base64
                    byte[] pdfData = android.util.Base64.decode(pdfBase64, android.util.Base64.DEFAULT);

                    // Guardar el archivo PDF en almacenamiento interno
                    String fileName = "libro_" + libro.getTitulo().replaceAll("\\s+", "_") + ".pdf";
                    File pdfFile = new File(getFilesDir(), fileName);
                    FileOutputStream fos = new FileOutputStream(pdfFile);
                    fos.write(pdfData);
                    fos.close();

                    // Abrir la actividad LecturaLibroActivity con la ruta del archivo
                    Intent intent = new Intent(CatalogoYLectura.this, LecturaLibroActivity.class);
                    intent.putExtra("pdfPath", pdfFile.getAbsolutePath());
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al procesar el libro.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "El libro no está disponible.", Toast.LENGTH_SHORT).show();
            }
        });

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
