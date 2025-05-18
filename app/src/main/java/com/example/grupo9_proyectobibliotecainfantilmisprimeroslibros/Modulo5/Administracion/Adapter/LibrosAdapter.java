package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Modelo.Libros;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LibrosAdapter extends FirestoreRecyclerAdapter<Libros, LibrosAdapter.ViewHolder>{

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    Activity activity;

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

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.titulo.setText(model.getTitulo());
        holder.categoria.setText(model.getCategoria());
        holder.edad.setText(model.getEdad());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminar(id);

            }
        });
    }

    private void eliminar(String id) {

        mFirestore.collection("libros").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Eliminado Correctamente", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView btnDelete;

        TextView titulo, categoria, edad;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.titulo);
            categoria = itemView.findViewById(R.id.categoria);
            edad = itemView.findViewById(R.id.edad);
            btnDelete = itemView.findViewById(R.id.btn_eliminarLibro);
        }
    }
}
