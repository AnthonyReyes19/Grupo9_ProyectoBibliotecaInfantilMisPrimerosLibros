package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Login.LoginPrincipal;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.AdminPanelActivity;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloPadresYEducadores.MenudelmoduloPadresEducadores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos.menuPrincipalModuloReyes;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        configurarInterfazSegunUsuario();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_Inicio);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String title = item.getTitle().toString();

        if (title.equals("Inicio")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (title.equals("Administrar Contenido")) {
            Intent ventana = new Intent(this, AdminPanelActivity.class);
            startActivity(ventana);
        } else if (title.equals("Configuracion")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        } else if (title.equals("Compartir")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShareFragment()).commit();
        } else if (title.equals("Acerca De")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
        } else if (title.equals("Cerrar Sesion")) {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            Intent ventana = new Intent(this, LoginPrincipal.class);
            startActivity(ventana);
            finish();
        } else if (title.equals("Actividades y Juegos")) {
            Intent ventana = new Intent(this, menuPrincipalModuloReyes.class);
            startActivity(ventana);
        } else if (title.equals("Padres y Educadores")) {
            Intent ventana = new Intent(this, MenudelmoduloPadresEducadores.class);
            startActivity(ventana);
        } else if (title.equals("Usuario y Perfil Infantil")) {
            Intent ventana = new Intent(this, MenuPerfilInfantilActivity.class);
            startActivity(ventana);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void configurarInterfazSegunUsuario() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String tipoUsuario = sharedPreferences.getString("tipo_usuario", "adulto");

        if ("infantil".equals(tipoUsuario)) {
            configurarModoInfantil(sharedPreferences);
        } else {
            configurarModoAdulto();
        }
    }

    private void configurarModoInfantil(SharedPreferences sharedPreferences) {
        String nombreInfantil = sharedPreferences.getString("perfil_infantil_nombre", "");
        String avatarInfantil = sharedPreferences.getString("perfil_infantil_avatar", "");
        int edadInfantil = sharedPreferences.getInt("perfil_infantil_edad", 0);

        // Mostrar saludo personalizado
        TextView saludoPersonalizado = findViewById(R.id.textSaludo);
        if (saludoPersonalizado != null) {
            saludoPersonalizado.setText("¡Hola " + nombreInfantil + "!");
        }

        // Configurar avatar en la interfaz
        ImageView avatarUsuario = findViewById(R.id.imageAvatarUsuario);
        if (avatarUsuario != null) {
            int avatarResId = getAvatarResource(avatarInfantil);
            avatarUsuario.setImageResource(avatarResId);
        }

        // Ocultar funciones de administración
//        View menuAdministracion = findViewById(R.id.menuAdministracion);
//        if (menuAdministracion != null) {
//            menuAdministracion.setVisibility(View.GONE);
//        }

        // Mostrar contenido apropiado para la edad
        mostrarContenidoPorEdad(edadInfantil);
    }

    private void configurarModoAdulto() {
        // Mostrar saludo para adultos
        TextView saludoPersonalizado = findViewById(R.id.textSaludo);
        if (saludoPersonalizado != null) {
            saludoPersonalizado.setText("Bienvenido/a");
        }

        // Mostrar funciones de administración
//        View menuAdministracion = findViewById(R.id.menuAdministracion);
//        if (menuAdministracion != null) {
//            menuAdministracion.setVisibility(View.VISIBLE);
//        }
    }

    private void mostrarContenidoPorEdad(int edad) {
        // Lógica para mostrar contenido apropiado según la edad
        // Por ejemplo, libros más simples para niños más pequeños
    }

    private int getAvatarResource(String avatarName) {
        switch (avatarName) {
            case "avatar1":
                return R.drawable.avatar_nino1;
            case "avatar2":
                return R.drawable.avatar_nino2;
            case "avatar3":
                return R.drawable.avatar_nino3;
            default:
                return R.drawable.avatar_nino1;
        }
    }

    // Método para cambiar de perfil (agregar a un botón en la interfaz)
    private void cambiarPerfil() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("tipo_usuario");
        editor.remove("perfil_infantil_id");
        editor.remove("perfil_infantil_nombre");
        editor.remove("perfil_infantil_edad");
        editor.remove("perfil_infantil_avatar");
        editor.apply();

        Intent intent = new Intent(this, SeleccionarPerfil.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();


    }
}