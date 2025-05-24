package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LecturaLibros;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class LecturaLibroActivity extends AppCompatActivity {

    private ImageView pdfImageView;
    private Button btnPrevPage, btnNextPage;
    private TextView pageNumberTextView;
    private ProgressBar progressBar;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor fileDescriptor;

    private int currentPageIndex = 0;
    private File pdfFile;
    private String tituloLibro;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura_libro);

        // Initialize views
        initializeViews();

        // Get data from intent
        String pdfPath = getIntent().getStringExtra("pdfPath");
        String pdfUrl = getIntent().getStringExtra("pdfUrl");
        tituloLibro = getIntent().getStringExtra("titulo");

        // Set title if available
        if (tituloLibro != null && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(tituloLibro);
        }

        // Load PDF from path or URL
        if (pdfPath != null) {
            loadPdfFromFile(pdfPath);
        } else if (pdfUrl != null) {
            descargarPDFDesdeFirebase(pdfUrl);
        } else {
            Toast.makeText(this, "No se proporcionó ruta o URL del libro.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set button listeners
        setupButtonListeners();
    }

    private void initializeViews() {
        pdfImageView = findViewById(R.id.pdfImageView);
        btnPrevPage = findViewById(R.id.btnPrevPage);
        btnNextPage = findViewById(R.id.btnNextPage);
        pageNumberTextView = findViewById(R.id.pageNumberTextView);
        progressBar = findViewById(R.id.progressBar); // Add this to your layout if not present

        // Initially disable buttons
        btnPrevPage.setEnabled(false);
        btnNextPage.setEnabled(false);
    }

    private void setupButtonListeners() {
        btnPrevPage.setOnClickListener(v -> {
            if (currentPageIndex > 0) {
                mostrarPagina(currentPageIndex - 1);
            } else {
                Toast.makeText(this, "Esta es la primera página", Toast.LENGTH_SHORT).show();
            }
        });

        btnNextPage.setOnClickListener(v -> {
            if (pdfRenderer != null && currentPageIndex < pdfRenderer.getPageCount() - 1) {
                mostrarPagina(currentPageIndex + 1);
            } else {
                Toast.makeText(this, "Esta es la última página", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPdfFromFile(String pdfPath) {
        showLoading(true);

        File pdfFile = new File(pdfPath);
        if (pdfFile.exists()) {
            try {
                fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
                pdfRenderer = new PdfRenderer(fileDescriptor);

                if (pdfRenderer.getPageCount() > 0) {
                    mostrarPagina(0); // Show first page
                } else {
                    Toast.makeText(this, "El PDF no contiene páginas.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al abrir el libro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            } finally {
                showLoading(false);
            }
        } else {
            Toast.makeText(this, "El archivo no se encuentra.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void descargarPDFDesdeFirebase(String url) {
        showLoading(true);
        Toast.makeText(this, "Descargando libro...", Toast.LENGTH_SHORT).show();

        // Create temporary file
        pdfFile = new File(getCacheDir(), "temp_book_" + System.currentTimeMillis() + ".pdf");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference pdfRef;

        try {
            pdfRef = storage.getReferenceFromUrl(url);
        } catch (IllegalArgumentException e) {
            // If URL is invalid, try as path
            pdfRef = storage.getReference().child(url);
        }

        pdfRef.getFile(pdfFile).addOnSuccessListener(taskSnapshot -> {
            try {
                fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
                pdfRenderer = new PdfRenderer(fileDescriptor);

                if (pdfRenderer.getPageCount() > 0) {
                    runOnUiThread(() -> {
                        mostrarPagina(0); // Show first page
                        Toast.makeText(this, "Libro cargado correctamente", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "El PDF no contiene páginas.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al abrir el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    finish();
                });
            } finally {
                runOnUiThread(() -> showLoading(false));
            }
        }).addOnFailureListener(e -> {
            showLoading(false);
            Toast.makeText(this, "Error al descargar el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        });
    }

    private void mostrarPagina(int index) {
        if (pdfRenderer == null || index < 0 || index >= pdfRenderer.getPageCount()) {
            return;
        }

        showLoading(true);

        // Run in background thread for better performance
        new Thread(() -> {
            try {
                // Close current page if open
                if (currentPage != null) {
                    currentPage.close();
                }

                // Open new page
                currentPage = pdfRenderer.openPage(index);

                // Get original dimensions
                int width = currentPage.getWidth();
                int height = currentPage.getHeight();

                // Scale if needed to avoid memory issues
                float scale = 1f;
                final int maxSize = 2048;

                if (width > maxSize || height > maxSize) {
                    scale = Math.min((float) maxSize / width, (float) maxSize / height);
                    width = (int) (width * scale);
                    height = (int) (height * scale);
                }

                // Create bitmap
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                // Render page
                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                // Update UI on main thread
                runOnUiThread(() -> {
                    // Update image
                    pdfImageView.setImageBitmap(bitmap);
                    currentPageIndex = index;

                    // Update page counter
                    if (pageNumberTextView != null) {
                        pageNumberTextView.setText(String.format("Página %d de %d",
                                currentPageIndex + 1, pdfRenderer.getPageCount()));
                    }

                    // Update button states
                    btnPrevPage.setEnabled(index > 0);
                    btnNextPage.setEnabled(index < pdfRenderer.getPageCount() - 1);

                    showLoading(false);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al mostrar la página: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    showLoading(false);
                });
            }
        }).start();
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        // Disable/enable buttons while loading
        if (show) {
            btnPrevPage.setEnabled(false);
            btnNextPage.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanup();
    }

    private void cleanup() {
        try {
            if (currentPage != null) {
                currentPage.close();
                currentPage = null;
            }
            if (pdfRenderer != null) {
                pdfRenderer.close();
                pdfRenderer = null;
            }
            if (fileDescriptor != null) {
                fileDescriptor.close();
                fileDescriptor = null;
            }
            // Delete temporary file if it exists
            if (pdfFile != null && pdfFile.exists() && pdfFile.getName().startsWith("temp_")) {
                pdfFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // Clean up before going back
        cleanup();
        super.onBackPressed();
    }
}