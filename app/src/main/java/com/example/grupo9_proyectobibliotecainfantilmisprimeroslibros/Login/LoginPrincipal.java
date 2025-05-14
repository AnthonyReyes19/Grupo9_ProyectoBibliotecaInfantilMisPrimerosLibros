package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.MainActivity;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos.loginPrincipalModuloReyes;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.RegistroPerfilInfantil;

public class LoginPrincipal extends AppCompatActivity {

    EditText usuarioTexto, contrasenaTexto;
    Button botonIniciar, botonRegistrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_principal);

        leerCredenciales();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuarioTexto = findViewById(R.id.usernameLogin_input);
        contrasenaTexto = findViewById(R.id.passwordLogin_input);
        botonIniciar = findViewById(R.id.btnIngresar);
        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(v -> MostrarDialogo());

    }

    public void MostrarDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_profile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnMaestro = dialogView.findViewById(R.id.btnPerfilMaestro);
        Button btnInfantil = dialogView.findViewById(R.id.btnPerfilInfantil);

        //btnMaestro.setOnClickListener(v -> {
            // Cambia "RegistroMaestroActivity.class" por tu actividad
            //Intent intent = new Intent(this, RegistroMaestroActivity.class);
            //startActivity(intent);
            //dialog.dismiss();
        //});

        btnInfantil.setOnClickListener(v -> {

            Intent intent = new Intent(this, RegistroPerfilInfantil.class);
            startActivity(intent);
            dialog.dismiss();
        });
    }

    public void ingresarSistema(View v){
        String usuario = usuarioTexto.getText().toString();
        String contrasena = contrasenaTexto.getText().toString();

        if (usuario.equals("admin") && contrasena.equals("grupo9")) {
            Toast.makeText(this, "Acceso Concedido", Toast.LENGTH_SHORT).show();
            Intent ventanaPrueba = new Intent(this, MainActivity.class);
            startActivity(ventanaPrueba);
        } else {
            Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
        }

        //CheckBox recordar = findViewById(R.id.login_chkrecordar);

        // if(recordar.isChecked()){
          //  guardarCredenciales(usuarioTexto.getText().toString(), contrasenaTexto.getText().toString());
        //}


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

        EditText txtuser = findViewById(R.id.usernameLogin_input);
        EditText txtclave = findViewById(R.id.passwordLogin_input);

        txtuser.setText(usuario);
        txtclave.setText(clave);
    }
    public void ventanaPrueba(View v){
        Intent ventanaPrueba = new Intent(this, MainActivity.class);
        startActivity(ventanaPrueba);
    }


}