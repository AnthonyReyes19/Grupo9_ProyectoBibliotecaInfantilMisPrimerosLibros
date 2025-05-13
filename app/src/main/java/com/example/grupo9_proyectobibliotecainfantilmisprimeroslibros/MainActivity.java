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

import ModuloActividadesyJuegos.loginPrincipalModuloReyes;

public class MainActivity extends AppCompatActivity {

    EditText usuarioTexto, contrasenaTexto;
    Button botonIniciar, botonRegistrar;
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
        usuarioTexto = findViewById(R.id.login_txtusuario);
        contrasenaTexto = findViewById(R.id.login_txtclave);
        botonIniciar = findViewById(R.id.login_btnIngresar);
        //botonRegistrar = findViewById(R.id.btnCrearCuenta);
    }

    public void ingresarSistema(View v){
        String usuario = usuarioTexto.getText().toString();
        String contrasena = contrasenaTexto.getText().toString();

        if (usuario.equals("admin") && contrasena.equals("grupo9")) {
            Toast.makeText(MainActivity.this, "Acceso Concedido", Toast.LENGTH_SHORT).show();
            Intent ventanaPrueba = new Intent(this, loginPrincipalModuloReyes.class);
            startActivity(ventanaPrueba);
        } else {
            Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
        }

        CheckBox recordar = findViewById(R.id.login_chkrecordar);

        if(recordar.isChecked()){
            guardarCredenciales(usuarioTexto.getText().toString(), contrasenaTexto.getText().toString());
        }


    }

   // public void crearCuenta(View v) {
   //     Intent ventanaRegistrarse = new Intent(this, CrearCuenta.class);
    //    startActivity(ventanaRegistrarse);
    // }

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

        EditText txtuser = findViewById(R.id.login_txtusuario);
        EditText txtclave = findViewById(R.id.login_txtclave);

        txtuser.setText(usuario);
        txtclave.setText(clave);
    }
    public void ventanaPrueba(View v){
        Intent ventanaPrueba = new Intent(this, loginPrincipalModuloReyes.class);
        startActivity(ventanaPrueba);
    }
}