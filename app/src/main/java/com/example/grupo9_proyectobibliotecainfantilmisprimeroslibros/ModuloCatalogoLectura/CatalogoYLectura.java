package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LecturaLibros.LecturaLibroActivity;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LibrosCatalogoAdapter.LibrosCatalogoAdapter;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LibrosCatalogoModelo.LibrosCatalogoModelo;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CatalogoYLectura extends AppCompatActivity {
    private static final String TAG = "CatalogoYLectura";

    private Spinner spinnerEdad, spinnerCategoria;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private LibrosCatalogoAdapter librosAdapter;
    private ProgressBar progressBar;
    private TextView emptyTextView;

    private String filtroEdad = "Seleccionar edad...";
    private String filtroCategoria = "Seleccionar categoría...";
    private String filtroBusqueda = "";

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private boolean isUpdatingQuery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo_y_lectura);

        // Vinculación de vistas
        inicializarVistas();

        // Configurar RecyclerView
        configurarRecyclerView();

        // Configuración de Spinners
        configurarSpinners();

        // Configurar SearchView
        configurarSearchView();

        // Carga inicial
        actualizarConsulta();
    }

    private void inicializarVistas() {
        spinnerEdad = findViewById(R.id.spinnerEdad);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        searchView = findViewById(R.id.SearchView);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar); // Añadir a tu layout si no existe
        emptyTextView = findViewById(R.id.emptyTextView); // Añadir a tu layout si no existe

        // Mostrar progress bar inicialmente
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void configurarRecyclerView() {
        // Configurar RecyclerView ANTES de establecer el adapter
        recyclerView.setHasFixedSize(true);

        // Configurar LayoutManager con optimizaciones
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setInitialPrefetchItemCount(5);
        recyclerView.setLayoutManager(layoutManager);

        // Desactivar animaciones para prevenir inconsistencias
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        // Pool de RecyclerView para mejor rendimiento
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);

        recyclerView.setItemAnimator(null);

    }

    private void configurarSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtroBusqueda = query.trim();
                actualizarConsultaConRetraso();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtroBusqueda = newText.trim();
                actualizarConsultaConRetraso();
                return true;
            }
        });
    }

    private void actualizarConsultaConRetraso() {
        // Cancelar búsqueda anterior
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        // Crear nueva búsqueda con retraso
        searchRunnable = new Runnable() {
            @Override
            public void run() {
                actualizarConsulta();
            }
        };

        // Ejecutar después de 300ms para evitar múltiples actualizaciones
        searchHandler.postDelayed(searchRunnable, 300);
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
        categoriasSpinner.add("Colores");

        ArrayAdapter<String> adapterEdad = new ArrayAdapter<>(this, R.layout.control_spinner, edadesSpinner);
        adapterEdad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdad.setAdapter(adapterEdad);

        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this, R.layout.control_spinner, categoriasSpinner);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategoria);

        spinnerEdad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nuevaEdad = edadesSpinner.get(position);
                if (!nuevaEdad.equals(filtroEdad)) {
                    filtroEdad = nuevaEdad;
                    actualizarConsulta();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nuevaCategoria = categoriasSpinner.get(position);
                if (!nuevaCategoria.equals(filtroCategoria)) {
                    filtroCategoria = nuevaCategoria;
                    actualizarConsulta();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void actualizarConsulta() {
        // Prevenir múltiples actualizaciones simultáneas
        if (isUpdatingQuery) {
            return;
        }

        isUpdatingQuery = true;

        runOnUiThread(() -> {
            try {
                // Mostrar progress bar
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                // Construir query
                Query query = FirebaseFirestore.getInstance().collection("libros");

                // Aplicar filtros
                if (!filtroEdad.equals("Seleccionar edad...") && !filtroEdad.isEmpty()) {
                    query = query.whereEqualTo("edad", filtroEdad);
                }

                if (!filtroCategoria.equals("Seleccionar categoría...") && !filtroCategoria.isEmpty()) {
                    query = query.whereEqualTo("categoria", filtroCategoria);
                }

                // Nota: Firestore no permite búsqueda parcial con whereEqualTo
                // Para búsqueda por título, considera usar algolia o implementar búsqueda local
                if (!filtroBusqueda.isEmpty()) {
                    // Opción 1: Búsqueda exacta
                    query = query.whereEqualTo("titulo", filtroBusqueda);

                    // Opción 2: Implementar búsqueda en el adapter (ver abajo)
                }

                // Ordenar y limitar resultados
                query = query.orderBy("titulo", Query.Direction.ASCENDING)
                        .limit(50); // Limitar para mejor rendimiento

                FirestoreRecyclerOptions<LibrosCatalogoModelo> options =
                        new FirestoreRecyclerOptions.Builder<LibrosCatalogoModelo>()
                                .setQuery(query, LibrosCatalogoModelo.class)
                                .setLifecycleOwner(this) // Importante para manejo automático
                                .build();

                // Detener adapter anterior
                if (librosAdapter != null) {
                    librosAdapter.stopListening();
                    recyclerView.setAdapter(null);
                }

                // Crear nuevo adapter
                librosAdapter = new LibrosCatalogoAdapter(options) {
                    @Override
                    public void onDataChanged() {
                        super.onDataChanged();

                        // Ocultar progress bar
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        // Mostrar/ocultar mensaje vacío
                        if (getItemCount() == 0) {
                            if (emptyTextView != null) {
                                emptyTextView.setVisibility(View.VISIBLE);
                                emptyTextView.setText("No se encontraron libros para los filtros aplicados.");
                            }
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            if (emptyTextView != null) {
                                emptyTextView.setVisibility(View.GONE);
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        super.onError(e);
                        Log.e(TAG, "Error en FirestoreRecyclerAdapter", e);
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(CatalogoYLectura.this,
                                "Error al cargar los libros: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                };

                // Configurar click listener
                librosAdapter.setOnLibroClickListener(libro -> {
                    // Deshabilitar RecyclerView temporalmente para evitar múltiples clics
                    recyclerView.setEnabled(false);

                    // Primero verificar si tiene URL de PDF
                    if (libro.getPdfUrl() != null && !libro.getPdfUrl().isEmpty()) {
                        // Usar URL directamente
                        Intent intent = new Intent(CatalogoYLectura.this, LecturaLibroActivity.class);
                        intent.putExtra("pdfUrl", libro.getPdfUrl());
                        intent.putExtra("titulo", libro.getTitulo());
                        startActivity(intent);
                    }
                    // Si no, verificar si tiene PDF en Base64
                    else if (libro.getPdfBase64() != null && !libro.getPdfBase64().isEmpty()) {
                        procesarPdfBase64(libro);
                    }
                    else {
                        Toast.makeText(CatalogoYLectura.this,
                                "El libro no está disponible.",
                                Toast.LENGTH_SHORT).show();
                    }

                    // Re-habilitar RecyclerView después de un segundo
                    recyclerView.postDelayed(() -> recyclerView.setEnabled(true), 1000);
                });

                // Establecer adapter
                recyclerView.setAdapter(librosAdapter);
                librosAdapter.startListening();

            } catch (Exception e) {
                Log.e(TAG, "Error al actualizar consulta", e);
                Toast.makeText(this, "Error al actualizar la lista", Toast.LENGTH_SHORT).show();
            } finally {
                isUpdatingQuery = false;
            }
        });
    }

    private void procesarPdfBase64(LibrosCatalogoModelo libro) {
        try {
            // Mostrar indicador de carga
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }

            // Procesar en background
            new Thread(() -> {
                try {
                    // Decodificar el PDF de Base64
                    byte[] pdfData = Base64.decode(libro.getPdfBase64(), Base64.DEFAULT);

                    // Guardar el archivo PDF en almacenamiento interno
                    String fileName = "libro_" + libro.getTitulo().replaceAll("\\s+", "_") + ".pdf";
                    File pdfFile = new File(getFilesDir(), fileName);

                    FileOutputStream fos = new FileOutputStream(pdfFile);
                    fos.write(pdfData);
                    fos.close();

                    // Volver al UI thread para abrir la actividad
                    runOnUiThread(() -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        Intent intent = new Intent(CatalogoYLectura.this, LecturaLibroActivity.class);
                        intent.putExtra("pdfPath", pdfFile.getAbsolutePath());
                        intent.putExtra("titulo", libro.getTitulo());
                        startActivity(intent);
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error al procesar PDF Base64", e);
                    runOnUiThread(() -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(CatalogoYLectura.this,
                                "Error al procesar el libro: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "Error al iniciar procesamiento de PDF", e);
            Toast.makeText(this, "Error al procesar el libro.", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar handlers
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        // Limpiar adapter
        if (librosAdapter != null) {
            librosAdapter.setOnLibroClickListener(null);
        }
    }

}