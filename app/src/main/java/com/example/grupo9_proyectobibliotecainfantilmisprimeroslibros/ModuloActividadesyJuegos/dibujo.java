package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class dibujo extends AppCompatActivity {

    DrawingView drawingView;
    Button btnBorrar, btnGuardar;
    private static final int REQUEST_STORAGE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dibujo);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }

        drawingView = findViewById(R.id.drawing_view);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnBorrar.setOnClickListener(v -> drawingView.clear());

        btnGuardar.setOnClickListener(v -> guardarImagen());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso denegado. No se podrá guardar dibujos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void guardarImagen() {
        Bitmap dibujo = drawingView.getBitmap();
        String nombreArchivo = "dibujo_" + System.currentTimeMillis() + ".png";

        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MisDibujos");

                Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fos = resolver.openOutputStream(uri);
            } else {
                File carpeta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MisDibujos");
                if (!carpeta.exists()) carpeta.mkdirs();
                File archivo = new File(carpeta, nombreArchivo);
                fos = new FileOutputStream(archivo);
            }

            dibujo.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            Toast.makeText(this, "Dibujo guardado", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    public void regresar(View v){
        Intent menu = new Intent(this, menuPrincipalModuloReyes.class);
        startActivity(menu);
    }
}