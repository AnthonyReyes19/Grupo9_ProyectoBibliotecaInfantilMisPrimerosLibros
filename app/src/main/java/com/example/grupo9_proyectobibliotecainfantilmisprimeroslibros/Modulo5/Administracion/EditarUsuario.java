package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Modelo.Usuario;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditarUsuario extends AppCompatActivity {

    private String usuarioId;
    private EditText editCedula, editNombres, editApellidos, editFecha, editCorreo, editContrasena;
    private TextView txtRol;
    private Button btnFecha, btnActualizar, btnCancelar;
    private ProgressBar progressBar;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Editar Usuario");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtener ID del usuario a editar
        usuarioId = getIntent().getStringExtra("USUARIO_ID");
        if (usuarioId == null) {
            Toast.makeText(this, "Error: No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        editCedula = findViewById(R.id.txtcedulareg);
        editNombres = findViewById(R.id.txtnombresreg);
        editApellidos = findViewById(R.id.txtapellidosreg);
        txtRol = findViewById(R.id.txtRolMostrar); // Este será un TextView, no editable
        editFecha = findViewById(R.id.CajaFechareg);
        editCorreo = findViewById(R.id.txtcorreoreg);
        editContrasena = findViewById(R.id.txtpassreg);
        btnFecha = findViewById(R.id.btnFechareg);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnCancelar = findViewById(R.id.btnCancelar);
        progressBar = findViewById(R.id.progressBar);

        // Inicializar Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Cargar datos del usuario
        cargarDatosUsuario();

        // Configurar selector de fecha
        btnFecha.setOnClickListener(v -> mostrarSelectorFecha());

        // Configurar botones
        btnActualizar.setOnClickListener(v -> actualizarUsuario());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarDatosUsuario() {
        progressBar.setVisibility(View.VISIBLE);

        mFirestore.collection("usuarios").document(usuarioId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        Usuario usuario = documentSnapshot.toObject(Usuario.class);
                        if (usuario != null) {
                            editCedula.setText(usuario.getCedula());
                            editNombres.setText(usuario.getNombres());
                            editApellidos.setText(usuario.getApellidos());
                            txtRol.setText(usuario.getRol()); // Mostrar rol pero no editable
                            editFecha.setText(usuario.getFecha_nacimiento());
                            editCorreo.setText(usuario.getEmail());
                            editContrasena.setText(usuario.getPassword());
                        }
                    } else {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void mostrarSelectorFecha() {
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccionar Fecha")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(selection));
            editFecha.setText(date);
        });

        materialDatePicker.show(getSupportFragmentManager(), "tag");
    }

    private void actualizarUsuario() {
        String cedula = editCedula.getText().toString().trim();
        String nombres = editNombres.getText().toString().trim();
        String apellidos = editApellidos.getText().toString().trim();
        String fecha = editFecha.getText().toString().trim();
        String correo = editCorreo.getText().toString().trim();
        String contrasena = editContrasena.getText().toString().trim();

        // Validar campos
        if (cedula.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || fecha.isEmpty() ||
                correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Crear mapa con datos actualizados
        Map<String, Object> datosActualizados = new HashMap<>();
        datosActualizados.put("cedula", cedula);
        datosActualizados.put("nombres", nombres);
        datosActualizados.put("apellidos", apellidos);
        datosActualizados.put("fecha_nacimiento", fecha);
        datosActualizados.put("email", correo);
        datosActualizados.put("password", contrasena);
        // No actualizamos el rol, mantenemos el original

        // Actualizar en Firestore
        mFirestore.collection("usuarios").document(usuarioId)
                .update(datosActualizados)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditarUsuario.this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditarUsuario.this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}