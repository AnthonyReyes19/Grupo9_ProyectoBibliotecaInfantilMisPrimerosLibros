package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Modelo.Libros;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class LibrosAdapter extends FirestoreRecyclerAdapter<Libros, LibrosAdapter.ViewHolder>{
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LibrosAdapter(@NonNull FirestoreRecyclerOptions<Libros> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull LibrosAdapter.ViewHolder holder, int position, @NonNull Libros model) {

        holder.titulo.setText(model.getTitulo());
        holder.categoria.setText(model.getCategoria());
        holder.edad.setText(model.getEdad());
    }

    @NonNull
    @Override
    public LibrosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_libros_administracion, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, categoria, edad;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.titulo);
            categoria = itemView.findViewById(R.id.categoria);
            edad = itemView.findViewById(R.id.edad);
        }
    }
}
