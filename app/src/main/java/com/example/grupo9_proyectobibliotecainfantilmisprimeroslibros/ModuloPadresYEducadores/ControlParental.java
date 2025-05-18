package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloPadresYEducadores;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

public class ControlParental extends AppCompatActivity {
    // UI Components
    private EditText etContrasenaActual, etNuevaContrasena, etConfirmarContrasena;
    private Button btnCambiarContrasena, btnGuardarLimites, btnGuardarNotificaciones, btnRestaurarDefecto;
    private Button btnHoraInicio, btnHoraFin;
    private Switch switchLimiteActivo;
    private SeekBar seekBarTiempo;
    private TextView tvTiempoSeleccionado;
    private CheckBox checkBoxLecturas, checkBoxActividades, checkBoxTiempoLimite, checkBoxReportesSemanal;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userConfigRef;

    // Variables para control parental
    private String horaInicio = "08:00 AM";
    private String horaFin = "08:00 PM";
    private int tiempoMaximoDiario = 60; // minutos
    private boolean limiteActivo = false;

    // SharedPreferences para guardar configuración local
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "ControlParentalPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_parental);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar referencia a la base de datos para este usuario
        userConfigRef = FirebaseDatabase.getInstance().getReference("controlParental")
                .child(currentUser.getUid());

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Configurar la Toolbar
        setupToolbar();

        // Inicializar componentes UI
        initializeUI();

        // Cargar configuración existente
        cargarConfiguracion();

        // Configurar los listeners
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initializeUI() {
        // Componentes de contraseña
        etContrasenaActual = findViewById(R.id.etContraseñaActual);
        etNuevaContrasena = findViewById(R.id.etNuevaContraseña);
        etConfirmarContrasena = findViewById(R.id.etConfirmarContraseña);
        btnCambiarContrasena = findViewById(R.id.btnCambiarContraseña);

        // Componentes de límites de uso
        switchLimiteActivo = findViewById(R.id.switchLimiteActivo);
        seekBarTiempo = findViewById(R.id.seekBarTiempo);
        tvTiempoSeleccionado = findViewById(R.id.tvTiempoSeleccionado);
        btnHoraInicio = findViewById(R.id.btnHoraInicio);
        btnHoraFin = findViewById(R.id.btnHoraFin);
        btnGuardarLimites = findViewById(R.id.btnGuardarLimites);

        // Componentes de notificaciones
        checkBoxLecturas = findViewById(R.id.checkBoxLecturas);
        checkBoxActividades = findViewById(R.id.checkBoxActividades);
        checkBoxTiempoLimite = findViewById(R.id.checkBoxTiempoLimite);
        checkBoxReportesSemanal = findViewById(R.id.checkBoxReportesSemanal);
        btnGuardarNotificaciones = findViewById(R.id.btnGuardarNotificaciones);

        // Botón para restaurar valores predeterminados
        btnRestaurarDefecto = findViewById(R.id.btnRestaurarDefecto);
    }

    private void setupListeners() {
        // Listener para el cambio de contraseña
        btnCambiarContrasena.setOnClickListener(v -> cambiarContrasena());

        // Listener para el SeekBar de tiempo
        seekBarTiempo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tiempoMaximoDiario = progress;
                tvTiempoSeleccionado.setText(progress + " minutos");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Listeners para los selectores de hora
        btnHoraInicio.setOnClickListener(v -> mostrarSelectorHora(true));
        btnHoraFin.setOnClickListener(v -> mostrarSelectorHora(false));

        // Listener para guardar límites
        btnGuardarLimites.setOnClickListener(v -> guardarLimites());

        // Listener para guardar notificaciones
        btnGuardarNotificaciones.setOnClickListener(v -> guardarNotificaciones());

        // Listener para restaurar valores predeterminados
        btnRestaurarDefecto.setOnClickListener(v -> restaurarValoresPredeterminados());

        // Listener para el switch de límite activo
        switchLimiteActivo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            limiteActivo = isChecked;
            // Habilitar o deshabilitar controles basados en el estado del switch
            seekBarTiempo.setEnabled(isChecked);
            btnHoraInicio.setEnabled(isChecked);
            btnHoraFin.setEnabled(isChecked);
        });
    }

    private void cargarConfiguracion() {
        userConfigRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Cargar configuración de límites
                    if (dataSnapshot.hasChild("limites")) {
                        DataSnapshot limitesSnapshot = dataSnapshot.child("limites");
                        limiteActivo = limitesSnapshot.child("activo").getValue(Boolean.class) != null ?
                                limitesSnapshot.child("activo").getValue(Boolean.class) : false;
                        tiempoMaximoDiario = limitesSnapshot.child("tiempoMaximo").getValue(Integer.class) != null ?
                                limitesSnapshot.child("tiempoMaximo").getValue(Integer.class) : 60;
                        horaInicio = limitesSnapshot.child("horaInicio").getValue(String.class) != null ?
                                limitesSnapshot.child("horaInicio").getValue(String.class) : "08:00 AM";
                        horaFin = limitesSnapshot.child("horaFin").getValue(String.class) != null ?
                                limitesSnapshot.child("horaFin").getValue(String.class) : "08:00 PM";

                        // Actualizar UI con valores cargados
                        switchLimiteActivo.setChecked(limiteActivo);
                        seekBarTiempo.setProgress(tiempoMaximoDiario);
                        tvTiempoSeleccionado.setText(tiempoMaximoDiario + " minutos");
                        btnHoraInicio.setText(horaInicio);
                        btnHoraFin.setText(horaFin);
                    }

                    // Cargar configuración de notificaciones
                    if (dataSnapshot.hasChild("notificaciones")) {
                        DataSnapshot notificacionesSnapshot = dataSnapshot.child("notificaciones");
                        checkBoxLecturas.setChecked(notificacionesSnapshot.child("lecturas").getValue(Boolean.class) != null ?
                                notificacionesSnapshot.child("lecturas").getValue(Boolean.class) : true);
                        checkBoxActividades.setChecked(notificacionesSnapshot.child("actividades").getValue(Boolean.class) != null ?
                                notificacionesSnapshot.child("actividades").getValue(Boolean.class) : true);
                        checkBoxTiempoLimite.setChecked(notificacionesSnapshot.child("tiempoLimite").getValue(Boolean.class) != null ?
                                notificacionesSnapshot.child("tiempoLimite").getValue(Boolean.class) : true);
                        checkBoxReportesSemanal.setChecked(notificacionesSnapshot.child("reportesSemanal").getValue(Boolean.class) != null ?
                                notificacionesSnapshot.child("reportesSemanal").getValue(Boolean.class) : true);
                    }
                }

                // Habilitar o deshabilitar controles basados en el estado del switch
                seekBarTiempo.setEnabled(limiteActivo);
                btnHoraInicio.setEnabled(limiteActivo);
                btnHoraFin.setEnabled(limiteActivo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ControlParental.this,
                        "Error al cargar configuración: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // También cargar desde SharedPreferences para valores locales
        cargarConfiguracionLocal();
    }

    private void cargarConfiguracionLocal() {
        // Cargar valores desde SharedPreferences
        limiteActivo = sharedPreferences.getBoolean("limiteActivo", false);
        tiempoMaximoDiario = sharedPreferences.getInt("tiempoMaximo", 60);
        horaInicio = sharedPreferences.getString("horaInicio", "08:00 AM");
        horaFin = sharedPreferences.getString("horaFin", "08:00 PM");

        // Actualizar UI con valores locales
        switchLimiteActivo.setChecked(limiteActivo);
        seekBarTiempo.setProgress(tiempoMaximoDiario);
        tvTiempoSeleccionado.setText(tiempoMaximoDiario + " minutos");
        btnHoraInicio.setText(horaInicio);
        btnHoraFin.setText(horaFin);

        // Habilitar o deshabilitar controles basados en el estado del switch
        seekBarTiempo.setEnabled(limiteActivo);
        btnHoraInicio.setEnabled(limiteActivo);
        btnHoraFin.setEnabled(limiteActivo);

        // Cargar configuración de notificaciones
        checkBoxLecturas.setChecked(sharedPreferences.getBoolean("notif_lecturas", true));
        checkBoxActividades.setChecked(sharedPreferences.getBoolean("notif_actividades", true));
        checkBoxTiempoLimite.setChecked(sharedPreferences.getBoolean("notif_tiempoLimite", true));
        checkBoxReportesSemanal.setChecked(sharedPreferences.getBoolean("notif_reportesSemanal", true));
    }

    private void cambiarContrasena() {
        String contrasenaActual = etContrasenaActual.getText().toString().trim();
        String nuevaContrasena = etNuevaContrasena.getText().toString().trim();
        String confirmarContrasena = etConfirmarContrasena.getText().toString().trim();

        // Validar campos
        if (contrasenaActual.isEmpty() || nuevaContrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nuevaContrasena.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-autenticar al usuario primero
        // Nota: Este es un método simplificado. En producción, deberías usar una reautenticación completa
        // con FirebaseUser.reauthenticate() antes de cambiar la contraseña
        user.updatePassword(nuevaContrasena)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ControlParental.this,
                                "Contraseña actualizada correctamente",
                                Toast.LENGTH_SHORT).show();
                        // Limpiar campos
                        etContrasenaActual.setText("");
                        etNuevaContrasena.setText("");
                        etConfirmarContrasena.setText("");
                    } else {
                        Toast.makeText(ControlParental.this,
                                "Error al actualizar contraseña: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarSelectorHora(final boolean esHoraInicio) {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    // Formatear hora a AM/PM
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int hour12Format = hourOfDay > 12 ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                    String timeString = String.format("%02d:%02d %s", hour12Format, minute, amPm);

                    if (esHoraInicio) {
                        horaInicio = timeString;
                        btnHoraInicio.setText(timeString);
                    } else {
                        horaFin = timeString;
                        btnHoraFin.setText(timeString);
                    }
                }, hora, minuto, false);

        timePickerDialog.show();
    }

    private void guardarLimites() {
        // Guardar en Firebase
        Map<String, Object> limites = new HashMap<>();
        limites.put("activo", limiteActivo);
        limites.put("tiempoMaximo", tiempoMaximoDiario);
        limites.put("horaInicio", horaInicio);
        limites.put("horaFin", horaFin);

        userConfigRef.child("limites").setValue(limites)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ControlParental.this,
                            "Límites guardados correctamente",
                            Toast.LENGTH_SHORT).show();

                    // También guardar en SharedPreferences para uso local
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("limiteActivo", limiteActivo);
                    editor.putInt("tiempoMaximo", tiempoMaximoDiario);
                    editor.putString("horaInicio", horaInicio);
                    editor.putString("horaFin", horaFin);
                    editor.apply();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ControlParental.this,
                            "Error al guardar límites: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarNotificaciones() {
        // Obtener estados de los checkboxes
        boolean notifLecturas = checkBoxLecturas.isChecked();
        boolean notifActividades = checkBoxActividades.isChecked();
        boolean notifTiempoLimite = checkBoxTiempoLimite.isChecked();
        boolean notifReportesSemanal = checkBoxReportesSemanal.isChecked();

        // Guardar en Firebase
        Map<String, Object> notificaciones = new HashMap<>();
        notificaciones.put("lecturas", notifLecturas);
        notificaciones.put("actividades", notifActividades);
        notificaciones.put("tiempoLimite", notifTiempoLimite);
        notificaciones.put("reportesSemanal", notifReportesSemanal);

        userConfigRef.child("notificaciones").setValue(notificaciones)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ControlParental.this,
                            "Preferencias de notificaciones guardadas",
                            Toast.LENGTH_SHORT).show();

                    // También guardar en SharedPreferences para uso local
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("notif_lecturas", notifLecturas);
                    editor.putBoolean("notif_actividades", notifActividades);
                    editor.putBoolean("notif_tiempoLimite", notifTiempoLimite);
                    editor.putBoolean("notif_reportesSemanal", notifReportesSemanal);
                    editor.apply();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ControlParental.this,
                            "Error al guardar notificaciones: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void restaurarValoresPredeterminados() {
        // Resetear valores a predeterminados
        limiteActivo = false;
        tiempoMaximoDiario = 60;
        horaInicio = "08:00 AM";
        horaFin = "08:00 PM";

        // Actualizar UI
        switchLimiteActivo.setChecked(false);
        seekBarTiempo.setProgress(60);
        tvTiempoSeleccionado.setText("60 minutos");
        btnHoraInicio.setText(horaInicio);
        btnHoraFin.setText(horaFin);

        // Resetear checkboxes de notificaciones
        checkBoxLecturas.setChecked(true);
        checkBoxActividades.setChecked(true);
        checkBoxTiempoLimite.setChecked(true);
        checkBoxReportesSemanal.setChecked(true);

        // Guardar valores predeterminados
        guardarLimites();
        guardarNotificaciones();

        Toast.makeText(this, "Valores restaurados a predeterminados", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}