package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

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
            //Para guardar en la BD
            if(guardarBD(titulo.getText().toString(), autor.getText().toString(), nivel.getRating())){
                Toast.makeText(this, "Datos guardados en BD correctamente", Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(this,"Hubo error en BD", Toast.LENGTH_LONG).show();
            }

    }

    public boolean guardarBD(String titulo, String autor, float rating){
        BDOpenHelper BDProyecto = new BDOpenHelper(this);
        final SQLiteDatabase BDProyectoEdit = BDProyecto.getWritableDatabase();

        if(BDProyectoEdit != null){
            ContentValues cv = new ContentValues();
            cv.put("titulo", titulo);
            cv.put("autor", autor);
            cv.put("ratingNivel", rating);

            BDProyectoEdit.insert("cuento",null, cv);
            //BDProyectoEdit.close();
            return true;

        }else{
            //BDProyectoEdit.close();
            return false;

        }
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
        Intent ventanaLogin = new Intent(this, loginPrincipalModuloReyes.class);
        startActivity(ventanaLogin);
    }
    public void consultarDatos(View v){
        Intent ventanaLogin = new Intent(this, consultarUsuario.class);
        startActivity(ventanaLogin);
    }
}