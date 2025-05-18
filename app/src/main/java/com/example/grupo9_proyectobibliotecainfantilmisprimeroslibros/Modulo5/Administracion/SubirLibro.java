package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubirLibro extends AppCompatActivity {

    EditText etTitulo, etCategoria, etEdad;
    ImageView imgPortada;
    Button btnSeleccionarImagen, btnSeleccionarPdf, btnSubir;

    private static final int MAX_FILE_SIZE_BYTES = 500 * 1024; // 500 KB

    Uri imagenUri = null;
    Uri pdfUri = null;

    DatabaseReference databaseRef;

    private FirebaseFirestore mFirestore;

    private final int IMG_REQUEST = 1;
    private final int PDF_REQUEST = 2;

    // Bandera para evitar múltiples envíos
    private boolean isUploading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subir_libro);

        String id = getIntent().getStringExtra("id_libro");
        mFirestore = FirebaseFirestore.getInstance();

        etTitulo = findViewById(R.id.etTitulo);
        etCategoria = findViewById(R.id.etCategoria);
        etEdad = findViewById(R.id.etEdad);
        imgPortada = findViewById(R.id.imgPortada);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnSeleccionarPdf = findViewById(R.id.btnSeleccionarPdf);
        btnSubir = findViewById(R.id.btnSubir);

        if(id == null || id.isEmpty()){
            btnSubir.setOnClickListener(v -> {
                // Evita múltiples clics
                if (!isUploading) {
                    subirLibro();
                }
            });
        }else{
            cargarLibroParaEditar(id);
            btnSubir.setText("Actualizar libro");
            btnSubir.setOnClickListener(v -> {
                // Evita múltiples clics
                if (!isUploading) {
                    actualizarLibro(id);
                }
            });
        }

        databaseRef = FirebaseDatabase.getInstance().getReference("Libros");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMG_REQUEST);
        });

        btnSeleccionarPdf.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, PDF_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == IMG_REQUEST) {
                imagenUri = data.getData();
                imgPortada.setImageURI(imagenUri);
            } else if (requestCode == PDF_REQUEST) {
                pdfUri = data.getData();
                Toast.makeText(this, "PDF seleccionado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void subirLibro() {
        String titulo = etTitulo.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();

        if (titulo.isEmpty() || categoria.isEmpty() || edad.isEmpty() || imagenUri == null || pdfUri == null) {
            Toast.makeText(this, "Completa todos los campos y selecciona imagen y PDF", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Activar bandera para evitar múltiples envíos
            isUploading = true;

            // Deshabilitar el botón para evitar múltiples envíos
            btnSubir.setEnabled(false);

            // Mostrar mensaje para indicar que se está procesando
            Toast.makeText(this, "Procesando libro, espere...", Toast.LENGTH_SHORT).show();

            // Validar tamaño de imagen
            Cursor cursorImg = getContentResolver().query(imagenUri, null, null, null, null);
            if (cursorImg != null) {
                int sizeIndex = cursorImg.getColumnIndex(OpenableColumns.SIZE);
                cursorImg.moveToFirst();
                long imageSize = cursorImg.getLong(sizeIndex);
                cursorImg.close();

                if (imageSize > MAX_FILE_SIZE_BYTES) {
                    String sizeInKB = String.format(Locale.getDefault(), "%.2f", imageSize / 1024.0);
                    Toast.makeText(this, "La imagen pesa " + sizeInKB + " KB y excede el límite de 500 KB", Toast.LENGTH_LONG).show();
                    // Resetear bandera y habilitar botón
                    isUploading = false;
                    btnSubir.setEnabled(true);
                    return;
                }
            }

            // Validar tamaño de PDF
            Cursor cursorPdf = getContentResolver().query(pdfUri, null, null, null, null);
            if (cursorPdf != null) {
                int sizeIndex = cursorPdf.getColumnIndex(OpenableColumns.SIZE);
                cursorPdf.moveToFirst();
                long pdfSize = cursorPdf.getLong(sizeIndex);
                cursorPdf.close();

                if (pdfSize > MAX_FILE_SIZE_BYTES) {
                    String sizeInKB = String.format(Locale.getDefault(), "%.2f", pdfSize / 1024.0);
                    Toast.makeText(this, "El PDF pesa " + sizeInKB + " KB y excede el límite de 500 KB", Toast.LENGTH_LONG).show();
                    // Resetear bandera y habilitar botón
                    isUploading = false;
                    btnSubir.setEnabled(true);
                    return;
                }
            }

            // Convertir imagen a Base64
            InputStream inputStreamImg = getContentResolver().openInputStream(imagenUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStreamImg);
            ByteArrayOutputStream baosImg = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baosImg);
            String imgBase64 = Base64.encodeToString(baosImg.toByteArray(), Base64.DEFAULT);

            // Convertir PDF a Base64
            InputStream inputStreamPdf = getContentResolver().openInputStream(pdfUri);
            ByteArrayOutputStream baosPdf = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStreamPdf.read(buffer)) != -1) {
                baosPdf.write(buffer, 0, len);
            }
            String pdfBase64 = Base64.encodeToString(baosPdf.toByteArray(), Base64.DEFAULT);

            // Crear objeto a guardar
            Map<String, Object> libroData = new HashMap<>();
            libroData.put("titulo", titulo);
            libroData.put("categoria", categoria);
            libroData.put("edad", edad);
            libroData.put("portadaBase64", imgBase64);
            libroData.put("pdfBase64", pdfBase64);

            // Guardar en Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("libros")
                    .add(libroData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Libro subido correctamente", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar en Firestore", Toast.LENGTH_SHORT).show();
                        // Resetear bandera y habilitar botón en caso de error
                        isUploading = false;
                        btnSubir.setEnabled(true);
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar archivos", Toast.LENGTH_SHORT).show();
            // Resetear bandera y habilitar botón en caso de error
            isUploading = false;
            btnSubir.setEnabled(true);
        }
    }

    private void cargarLibroParaEditar(String id) {
        mFirestore.collection("libros").document(id).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        etTitulo.setText(document.getString("titulo"));
                        etCategoria.setText(document.getString("categoria"));
                        etEdad.setText(document.getString("edad"));

                        // Imagen Base64 a Bitmap
                        String imgBase64 = document.getString("portadaBase64");
                        if (imgBase64 != null && !imgBase64.isEmpty()) {
                            byte[] imgBytes = Base64.decode(imgBase64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                            imgPortada.setImageBitmap(bitmap);
                        }

                        // No se puede previsualizar PDF directamente
                        Toast.makeText(this, "PDF cargado. Si deseas reemplazarlo, selecciona uno nuevo.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar libro", Toast.LENGTH_SHORT).show());
    }

    private void actualizarLibro(String id) {
        String titulo = etTitulo.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();

        if (titulo.isEmpty() || categoria.isEmpty() || edad.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Map<String, Object> libroActualizado = new HashMap<>();
            libroActualizado.put("titulo", titulo);
            libroActualizado.put("categoria", categoria);
            libroActualizado.put("edad", edad);

            // Validar imagen si se seleccionó
            if (imagenUri != null) {
                InputStream inputStreamImg = getContentResolver().openInputStream(imagenUri);
                int sizeInBytes = inputStreamImg.available();
                if (sizeInBytes > 500 * 1024) { // > 500 KB
                    Toast.makeText(this, "La imagen no debe superar los 500 KB", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap bitmap = BitmapFactory.decodeStream(inputStreamImg);
                ByteArrayOutputStream baosImg = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baosImg);
                String imgBase64 = Base64.encodeToString(baosImg.toByteArray(), Base64.DEFAULT);
                libroActualizado.put("portadaBase64", imgBase64);
            }

            // Validar PDF si se seleccionó
            if (pdfUri != null) {
                InputStream inputStreamPdf = getContentResolver().openInputStream(pdfUri);
                int sizeInBytes = inputStreamPdf.available();
                if (sizeInBytes > 1 * 1024 * 1024) { // > 3 MB
                    Toast.makeText(this, "El PDF no debe superar 1 MB", Toast.LENGTH_SHORT).show();
                    return;
                }

                ByteArrayOutputStream baosPdf = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStreamPdf.read(buffer)) != -1) {
                    baosPdf.write(buffer, 0, len);
                }
                String pdfBase64 = Base64.encodeToString(baosPdf.toByteArray(), Base64.DEFAULT);
                libroActualizado.put("pdfBase64", pdfBase64);
            }

            // Subir cambios
            mFirestore.collection("libros").document(id)
                    .update(libroActualizado)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Libro actualizado correctamente", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar archivos", Toast.LENGTH_SHORT).show();
        }

    }
}