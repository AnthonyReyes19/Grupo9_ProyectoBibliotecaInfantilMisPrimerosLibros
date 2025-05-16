package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.MainActivity;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RegistrarPadresYEducadores extends AppCompatActivity {

    String[] items = {"Padre", "Educador"};

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    Button buttonFecha, btnRegistrar, btnCancelar;
    EditText editTextfecha, cedula, nombres, apellidos, correo, contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_padres_yeducadores);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cedula = findViewById(R.id.txtcedulareg);
        nombres = findViewById(R.id.txtnombresreg);
        apellidos = findViewById(R.id.txtapellidosreg);
        correo = findViewById(R.id.txtcorreoreg);
        contrasena = findViewById(R.id.txtpassreg);
        btnRegistrar = findViewById(R.id.btnRegistrar_reg);
        btnCancelar = findViewById(R.id.btnCancelarreg);

        buttonFecha = findViewById(R.id.btnFechareg);
        editTextfecha = findViewById(R.id.CajaFechareg);
        autoCompleteTextView = findViewById(R.id.autoCompleteTxt);

        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, items);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            Toast.makeText(getApplicationContext(), "Rol: " + item, Toast.LENGTH_SHORT).show();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void dateReg(View v) {
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccionar Fecha")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(selection));
            editTextfecha.setText(date);
        });

        materialDatePicker.show(getSupportFragmentManager(), "tag");
    }

    public void registrar(View v){

        String cedulaUser = cedula.getText().toString().trim();
        String nombresUser = nombres.getText().toString().trim();
        String apellidosUser = apellidos.getText().toString().trim();

        final String[] rolUser = {""};
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            rolUser[0] = parent.getItemAtPosition(position).toString();
        });
        String rolUser0 = autoCompleteTextView.getText().toString().trim();

        String fechaUser = editTextfecha.getText().toString().trim();
        String correoUser = correo.getText().toString().trim();
        String contrasenaUser = contrasena.getText().toString().trim();

        // Obtener los valores del adaptador del AutoCompleteTextView
        ArrayAdapter adapter = (ArrayAdapter) autoCompleteTextView.getAdapter();
        List<String> listaRoles = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            listaRoles.add(adapter.getItem(i).toString());
        }

        if(cedulaUser.isEmpty() && nombresUser.isEmpty() && apellidosUser.isEmpty() && fechaUser.isEmpty() &&
                correoUser.isEmpty() && contrasenaUser.isEmpty() && rolUser[0].isEmpty() && !listaRoles.contains(rolUser[0])){
            Toast.makeText(RegistrarPadresYEducadores.this, "Complete los campos faltantes", Toast.LENGTH_SHORT).show();
        }else{
            registroPadreEducador(cedulaUser, nombresUser, apellidosUser, rolUser0, fechaUser, correoUser, contrasenaUser);
        }

    }

    private void registroPadreEducador(String cedulaUser, String nombresUser, String apellidosUser, String rol, String fechaUser, String correoUser, String contrasenaUser) {

        mAuth.createUserWithEmailAndPassword(correoUser, contrasenaUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String id = mAuth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id",id);
                map.put("cedula", cedulaUser);
                map.put("nombres", nombresUser);
                map.put("apellidos", apellidosUser);
                map.put("rol", rol);
                map.put("fecha_nacimiento", fechaUser);
                map.put("email", correoUser);
                map.put("password", contrasenaUser);

                mFirestore.collection("usuarios").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        startActivity(new Intent(RegistrarPadresYEducadores.this, MainActivity.class));
                        Toast.makeText(RegistrarPadresYEducadores.this, "Usuario Registrado con Exito", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrarPadresYEducadores.this, "Error al Guardar", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrarPadresYEducadores.this, "Error al Registrar", Toast.LENGTH_SHORT).show();
            }
        });



    }
}