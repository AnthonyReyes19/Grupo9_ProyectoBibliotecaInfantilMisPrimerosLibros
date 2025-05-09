package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        leerCredenciales();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
    }

    public void ingresarSistema(View v){
        EditText txt_usuario = findViewById(R.id.loginP_txtusuario);
        EditText txt_clave = findViewById(R.id.loginP_txtclave);
        Button btn_ingresar = findViewById(R.id.loginP_btnIngresar);

        Toast.makeText(v.getContext(),"Ha presionado el boton ingresar", Toast.LENGTH_LONG).show();

        CheckBox recordar = findViewById(R.id.login_chkrecordar);

        if(recordar.isChecked()){
            guardarCredenciales(txt_usuario.getText().toString(), txt_clave.getText().toString());

        }

        Intent ventanaPrincipal = new Intent( this, MainActivity.class);
        ventanaPrincipal.putExtra("user", txt_usuario.getText().toString());
        startActivity(ventanaPrincipal);
    }

    private void guardarCredenciales(String usuario, String clave){
        SharedPreferences splogin = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor speditlogin = splogin.edit();

        speditlogin.putString("spUsuario", usuario);
        speditlogin.putString("spClave", clave);

        speditlogin.commit();

    }
    private void leerCredenciales(){
        SharedPreferences splogin = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        String usuario = splogin.getString("spUsuario", "");
        String clave = splogin.getString("spClave", "");

        EditText txtuser = findViewById(R.id.loginP_txtusuario);
        EditText txtclave = findViewById(R.id.loginP_txtclave);

        txtuser.setText(usuario);
        txtclave.setText(clave);
    }
    public void ventanaPrueba(View v){
        Intent ventanaPrueba = new Intent(this, loginPrincipalModuloReyes.class);
        startActivity(ventanaPrueba);
    }
}