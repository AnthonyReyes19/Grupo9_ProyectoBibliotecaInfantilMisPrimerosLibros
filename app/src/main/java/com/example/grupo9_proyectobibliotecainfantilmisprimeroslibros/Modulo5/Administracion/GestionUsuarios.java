package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Adapter.UsuarioAdapter;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Modelo.Usuario;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GestionUsuarios extends AppCompatActivity implements UsuarioAdapter.UsuarioClickListener {

    private RecyclerView recyclerView;
    private UsuarioAdapter adapter;
    private List<Usuario> listaUsuarios;
    private FirebaseFirestore mFirestore;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Gestionar Usuarios");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerViewUsuarios);
        progressBar = findViewById(R.id.progressBar);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaUsuarios = new ArrayList<>();
        adapter = new UsuarioAdapter(listaUsuarios, this);
        recyclerView.setAdapter(adapter);

        // Inicializar Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Cargar usuarios
        cargarUsuarios();

        // Botón para añadir nuevo usuario
        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregarUsuario);
        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(GestionUsuarios.this, RegistrarPadresYEducadores.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarUsuarios(); // Recargar al volver a la actividad
    }

    private void cargarUsuarios() {
        progressBar.setVisibility(View.VISIBLE);

        mFirestore.collection("usuarios")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        listaUsuarios.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Usuario usuario = document.toObject(Usuario.class);
                            usuario.setId(document.getId());
                            listaUsuarios.add(usuario);
                        }

                        adapter.notifyDataSetChanged();

                        if (listaUsuarios.isEmpty()) {
                            // Mostrar mensaje de que no hay usuarios
                            findViewById(R.id.txtNoUsuarios).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.txtNoUsuarios).setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(GestionUsuarios.this,
                                "Error al cargar usuarios: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onEditarClick(Usuario usuario) {
        // Abrir actividad de edición
        Intent intent = new Intent(this, EditarUsuario.class);
        intent.putExtra("USUARIO_ID", usuario.getId());
        startActivity(intent);
    }

    @Override
    public void onEliminarClick(Usuario usuario) {
        // Mostrar diálogo de confirmación
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de que deseas eliminar a " + usuario.getNombres() + " " + usuario.getApellidos() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarUsuario(usuario.getId());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarUsuario(String usuarioId) {
        progressBar.setVisibility(View.VISIBLE);

        mFirestore.collection("usuarios").document(usuarioId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(GestionUsuarios.this,
                            "Usuario eliminado correctamente",
                            Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(GestionUsuarios.this,
                            "Error al eliminar usuario: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}