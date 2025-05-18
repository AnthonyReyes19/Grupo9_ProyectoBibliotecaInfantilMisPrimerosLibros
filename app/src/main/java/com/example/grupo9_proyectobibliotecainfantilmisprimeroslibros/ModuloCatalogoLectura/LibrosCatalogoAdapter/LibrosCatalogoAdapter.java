package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloCatalogoLectura.LibrosCatalogoAdapter;

import android.content.Context;
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

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Modelo.Libros;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

import java.util.List;

public class LibrosAdapter extends RecyclerView.Adapter<LibrosAdapter.LibrosViewHolder> {

    private Context context;
    private List<Libros> listaLibros;

    public LibrosAdapter(Context context, List<Libros> listaLibros) {
        this.context = context;
        this.listaLibros = listaLibros;
    }

    @NonNull
    @Override
    public LibrosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_libro, parent, false);
        return new LibrosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibrosViewHolder holder, int position) {
        Libros libro = listaLibros.get(position);

        // Configura el título y categoría
        holder.tvTitulo.setText(libro.getTitulo());
        holder.tvCategoria.setText(libro.getCategoria());

        // Decodifica la imagen de portada desde base64
        String portadaBase64 = libro.getPortadaBase64();
        if (portadaBase64 != null && !portadaBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(portadaBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.ivPortada.setImageBitmap(decodedByte);
        } else {
            // Imagen por defecto si no hay portada
            holder.ivPortada.setImageResource(R.drawable.placeholder_image);
        }
    }

    @Override
    public int getItemCount() {
        return listaLibros.size();
    }

    public static class LibrosViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvCategoria;
        ImageView ivPortada;

        public LibrosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            ivPortada = itemView.findViewById(R.id.ivPortada);
        }
    }
}