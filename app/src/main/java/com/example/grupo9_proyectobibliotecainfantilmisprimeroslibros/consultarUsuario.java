package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class consultarUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consultar_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @SuppressLint("Range")
    public void consultarDatosDB(View v){
        BDOpenHelper BDProyecto = new BDOpenHelper(this);
        final SQLiteDatabase BDProyectoSelect = BDProyecto.getReadableDatabase();

        EditText id = findViewById(R.id.cons_txtId);
        TextView titulo = findViewById(R.id.cons_lblTitulo);
        TextView autor = findViewById(R.id.cons_lblAutor);

        int identificador = Integer.valueOf(id.getText().toString());

        Cursor data = BDProyectoSelect.rawQuery("SELECT id, titulo, autor FROM cuento "+
                "WHERE id = " + identificador, null);


        if(data != null){
            if(data.getCount()==0){
                Toast.makeText(this,"No hay registros encontrados", Toast.LENGTH_LONG).show();
            }
            else{
                data.moveToFirst();
                titulo.setText(data.getString(data.getColumnIndex("titulo")).toString());
                autor.setText(data.getString(data.getColumnIndex("autor")).toString());
            }
        }
        data.close();
        BDProyectoSelect.close();

    }

    public void regresar(View v){
        Intent ventanaRegistro = new Intent(this, formularioCuento.class);
        startActivity(ventanaRegistro);
    }

}