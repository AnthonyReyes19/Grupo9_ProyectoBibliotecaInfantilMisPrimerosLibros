package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SubirLibro extends AppCompatActivity {

    EditText etTitulo, etCategoria, etEdad;
    ImageView imgPortada;
    Button btnSeleccionarImagen, btnSeleccionarPdf, btnSubir;

    Uri imagenUri = null;
    Uri pdfUri = null;

    DatabaseReference databaseRef;

    private final int IMG_REQUEST = 1;
    private final int PDF_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subir_libro);

        etTitulo = findViewById(R.id.etTitulo);
        etCategoria = findViewById(R.id.etCategoria);
        etEdad = findViewById(R.id.etEdad);
        imgPortada = findViewById(R.id.imgPortada);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnSeleccionarPdf = findViewById(R.id.btnSeleccionarPdf);
        btnSubir = findViewById(R.id.btnSubir);

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

        btnSubir.setOnClickListener(v -> subirLibro());
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

        String key = databaseRef.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Error generando clave única", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
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

            // Guardar en Firebase
            Map<String, Object> libroData = new HashMap<>();
            libroData.put("titulo", titulo);
            libroData.put("categoria", categoria);
            libroData.put("edad", edad);
            libroData.put("portadaBase64", imgBase64);
            libroData.put("pdfBase64", pdfBase64);

            databaseRef.child(key).setValue(libroData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Libro subido correctamente", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error al guardar en base de datos", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar archivos", Toast.LENGTH_SHORT).show();
        }
    }
}