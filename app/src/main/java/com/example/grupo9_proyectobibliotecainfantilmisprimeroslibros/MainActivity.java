package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Login.LoginPrincipal;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.AdminPanelActivity;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloPadresYEducadores.MenudelmoduloPadresEducadores;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.CatalogoYLectura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos.loginPrincipalModuloReyes;
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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
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
            Intent ventana = new Intent(this, loginPrincipalModuloReyes.class);
            startActivity(ventana);
        } else if(title.equals("Padres y Educadores")){
            Intent ventana = new Intent(this, MenudelmoduloPadresEducadores.class);
            startActivity(ventana);
        }   else if(title.equals("Usuario y Perfil Infantil")) {
            Intent ventana = new Intent(this, MenuPerfilInfantilActivity.class);
            startActivity(ventana);
        }
            else if(title.equals("Catalogo y Lectura")) {
            Intent ventana = new Intent(this, CatalogoYLectura.class);
            startActivity(ventana);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
}