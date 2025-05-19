package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LibrosCatalogoAdapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LibrosCatalogoModelo.LibrosCatalogoModelo;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class LibrosCatalogoAdapter extends FirestoreRecyclerAdapter<LibrosCatalogoModelo, LibrosCatalogoAdapter.LibrosViewHolder> {

    public LibrosCatalogoAdapter(@NonNull FirestoreRecyclerOptions<LibrosCatalogoModelo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull LibrosViewHolder holder, int position, @NonNull LibrosCatalogoModelo model) {
        holder.titulo.setText(model.getTitulo());
        holder.categoria.setText(model.getCategoria());
        holder.edad.setText(model.getEdad());

        // Decodificar la imagen en Base64
        String base64Image = model.getPortadaBase64();
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.imagen.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                holder.imagen.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            holder.imagen.setImageResource(R.drawable.placeholder_image);
        }
    }

    @NonNull
    @Override
    public LibrosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.control_recyclerview, parent, false);
        return new LibrosViewHolder(view);
    }

    public static class LibrosViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, categoria, edad;
        ImageView imagen;

        public LibrosViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTitulo);
            categoria = itemView.findViewById(R.id.tvCategoria);
            edad = itemView.findViewById(R.id.textViewEdad);
            imagen = itemView.findViewById(R.id.ivPortada);
        }
    }
}