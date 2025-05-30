package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

public class formularioCuento extends AppCompatActivity {

    Spinner ensenar, edades;
    EditText txtTitulo, txtAutor;
    Button btnRegistrar, btnBorrar, btnVolver, btnConsultar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario_cuento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtTitulo = findViewById(R.id.reg_txtTitulo);
        txtAutor = findViewById(R.id.reg_txtAutor);
        ensenar = findViewById(R.id.reg_contEnsenar);
        edades = findViewById(R.id.reg_contEdad);
        btnRegistrar = findViewById(R.id.reg_btnRegistrar);
        btnBorrar = findViewById(R.id.reg_btnBorrar);
        btnVolver = findViewById(R.id.reg_btnCancelar);
        btnConsultar = findViewById(R.id.btnConsultar);

        ArrayAdapter<CharSequence> valores = ArrayAdapter.createFromResource(this,
                R.array.valores, android.R.layout.simple_spinner_item);
        valores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ensenar.setAdapter(valores);

        ArrayAdapter<CharSequence> edad = ArrayAdapter.createFromResource(this,
                R.array.edad, android.R.layout.simple_spinner_item);
        edad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edades.setAdapter(edad);

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarCampos();
            }
        });

    }

    public void guardar(View v){
            String data_formulario;
            EditText titulo = findViewById(R.id.reg_txtTitulo);
            EditText autor = findViewById(R.id.reg_txtAutor);

            RatingBar nivel = findViewById(R.id.reg_ratNivel);

            data_formulario = txtTitulo.getText().toString() + "\n" + txtAutor.getText().toString() + "\n\n" + nivel.getRating();
            Log.i("Formulario",data_formulario);

            if (guardarenSD(data_formulario)){
                Toast.makeText(this, "Los datos han sido guardados correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hubo error al guardar los datos en el SD", Toast.LENGTH_SHORT).show();
            }
        String valorSeleccionado = ensenar.getSelectedItem().toString();
        String edadSeleccionada = edades.getSelectedItem().toString();

        guardarEnFirebase(
                titulo.getText().toString(),
                autor.getText().toString(),
                nivel.getRating(),
                valorSeleccionado,
                edadSeleccionada
        );


    }

    public void guardarEnFirebase(String titulo, String autor, float rating, String valor, String edad){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String id = dbRef.push().getKey(); // genera un ID único

        Map<String, Object> datos = new HashMap<>();
        datos.put("titulo", titulo);
        datos.put("autor", autor);
        datos.put("nivel", rating);
        datos.put("valor", valor);
        datos.put("edadRecomendada", edad);

        dbRef.child("actividades")
                .child("cuentos_registrados")
                .child(id)
                .setValue(datos)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Guardado en Firebase", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar en Firebase", Toast.LENGTH_SHORT).show();
                });
    }


    private boolean guardarenSD(String datos) {
        try{
            File fRegistro = new File(getExternalFilesDir(null), "RegistroEquipo");
            OutputStreamWriter outRegistro = new OutputStreamWriter(new FileOutputStream(fRegistro, true));
            outRegistro.write(datos);
            outRegistro.close();
            return true;
        } catch (Exception e) {
            Log.e("Guardar SD", "Error al guardar datos: " + e.getMessage());
            return false;
        }
    }


    private void borrarCampos() {
        ((EditText) findViewById(R.id.reg_txtTitulo)).setText("");
        ((EditText) findViewById(R.id.reg_txtAutor)).setText("");

        ensenar.setSelection(0);
        edades.setSelection(0);

        RatingBar nivel = findViewById(R.id.reg_ratNivel);
        nivel.setRating(0f);
    }
    public void regresar(View v){
        Intent ventanaLogin = new Intent(this, menuPrincipalModuloReyes.class);
        startActivity(ventanaLogin);
    }
    public void consultarDatos(View v){
        Intent ventanaLogin = new Intent(this, consultarDatos.class);
        startActivity(ventanaLogin);
    }
}