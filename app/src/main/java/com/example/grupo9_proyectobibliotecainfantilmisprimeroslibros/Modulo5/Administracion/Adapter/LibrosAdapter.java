package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Modelo.Libros;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.SubirLibro;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LibrosAdapter extends FirestoreRecyclerAdapter<Libros, LibrosAdapter.ViewHolder>{

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Activity activity;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LibrosAdapter(@NonNull FirestoreRecyclerOptions<Libros> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull LibrosAdapter.ViewHolder holder, int position, @NonNull Libros model) {
        // Validar que el ViewHolder aún está en una posición válida
        int adapterPosition = holder.getBindingAdapterPosition();
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return;
        }
        // Validar que la posición existe en los snapshots
        if (adapterPosition >= getSnapshots().size()) {
            return;
        }
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(adapterPosition);
        final String id = documentSnapshot.getId();
        holder.titulo.setText(model.getTitulo());
        holder.categoria.setText(model.getCategoria());
        holder.edad.setText(model.getEdad());
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPos = holder.getBindingAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(activity, SubirLibro.class);
                    intent.putExtra("id_libro", id);
                    activity.startActivity(intent);
                }
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPos = holder.getBindingAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    eliminar(id);
                }
            }
        });
    }


    private void eliminar(String id) {
        mFirestore.collection("libros").document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(activity, "Eliminado Correctamente", Toast.LENGTH_SHORT).show();
                        // No es necesario notificar cambios al adaptador,
                        // FirestoreRecyclerAdapter se actualiza automáticamente
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @NonNull
    @Override
    public LibrosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_libros_administracion, parent, false);
        return new ViewHolder(v);
    }

    // Método para manejar la detención del listener
    @Override
    public void onDataChanged() {
        super.onDataChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView btnDelete;
        ImageView btnEdit;
        TextView titulo, categoria, edad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            categoria = itemView.findViewById(R.id.categoria);
            edad = itemView.findViewById(R.id.edad);
            btnDelete = itemView.findViewById(R.id.btn_eliminarLibro);
            btnEdit = itemView.findViewById(R.id.btn_editarLibro);
        }
    }
}