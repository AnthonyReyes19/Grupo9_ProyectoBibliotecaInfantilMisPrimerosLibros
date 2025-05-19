package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LecturaLibros;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Button;
import android.widget.ImageView;
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
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor fileDescriptor;

    private int currentPageIndex = 0;
    private File pdfFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura_libro);

        pageNumberTextView = findViewById(R.id.pageNumberTextView);
        pdfImageView = findViewById(R.id.pdfImageView);
        btnPrevPage = findViewById(R.id.btnPrevPage);
        btnNextPage = findViewById(R.id.btnNextPage);

        // Obtener la ruta del archivo del intent
        String pdfPath = getIntent().getStringExtra("pdfPath");
        if (pdfPath != null) {
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists()) {
                try {
                    pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
                    openPage(0); // Abre la primera página
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al abrir el libro.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "El archivo no se encuentra.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ruta del libro no válida.", Toast.LENGTH_SHORT).show();
        }

        btnPrevPage.setOnClickListener(v -> {
            if (currentPageIndex != 0) {
                currentPageIndex--;
                mostrarPagina(currentPageIndex);
            } else {
                Toast.makeText(this, "Esta es la primera página", Toast.LENGTH_SHORT).show();
            }
        });
        btnNextPage.setOnClickListener(v -> openPage(currentPageIndex + 1));
    }

    private void descargarPDFDesdeFirebase(String url) {
        pdfFile = new File(getCacheDir(), "temp.pdf");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference pdfRef = storage.getReferenceFromUrl(url);

        pdfRef.getFile(pdfFile).addOnSuccessListener(taskSnapshot -> {
            try {
                abrirPDF(pdfFile);
            } catch (Exception e) {
                Toast.makeText(this, "Error al abrir el PDF.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al descargar el PDF.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }

    private void abrirPDF(File file) throws Exception {
        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        pdfRenderer = new PdfRenderer(fileDescriptor);
        mostrarPagina(0); // Mostrar la primera página
    }

    private void mostrarPagina(int index) {
        if (pdfRenderer == null || index < 0 || index >= pdfRenderer.getPageCount()) {
            return;
        }

        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = pdfRenderer.openPage(index);
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        pdfImageView.setImageBitmap(bitmap);
        currentPageIndex = index;

        btnPrevPage.setEnabled(index > 0);
        btnNextPage.setEnabled(index < pdfRenderer.getPageCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (currentPage != null) {
                currentPage.close();
            }
            if (pdfRenderer != null) {
                pdfRenderer.close();
            }
            if (fileDescriptor != null) {
                fileDescriptor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void openPage(int index) {
        if (pdfRenderer == null || index < 0 || index >= pdfRenderer.getPageCount()) {
            return;
        }

        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = pdfRenderer.openPage(index);
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        pdfImageView.setImageBitmap(bitmap);
        currentPageIndex = index;

        btnPrevPage.setEnabled(index > 0);
        btnNextPage.setEnabled(index < pdfRenderer.getPageCount() - 1);
        }

    }
    