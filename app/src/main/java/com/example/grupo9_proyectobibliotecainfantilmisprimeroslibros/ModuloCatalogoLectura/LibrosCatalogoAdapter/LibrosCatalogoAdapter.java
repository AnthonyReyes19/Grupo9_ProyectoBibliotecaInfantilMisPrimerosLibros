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
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LibrosCatalogoAdapter extends FirestoreRecyclerAdapter<LibrosCatalogoModelo, LibrosCatalogoAdapter.LibrosViewHolder> {

    private OnLibroClickListener listener;
    private RecyclerView recyclerView;

    public interface OnLibroClickListener {
        void onLibroClick(LibrosCatalogoModelo libro);
    }

    public void setOnLibroClickListener(OnLibroClickListener listener) {
        this.listener = listener;
    }

    public LibrosCatalogoAdapter(@NonNull FirestoreRecyclerOptions<LibrosCatalogoModelo> options) {
        super(options);
        // Enable stable IDs to help prevent inconsistency errors
        setHasStableIds(true);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        if (recyclerView != null && recyclerView.getItemAnimator() != null) {
            recyclerView.getItemAnimator().setChangeDuration(0);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull LibrosViewHolder holder, int position, @NonNull LibrosCatalogoModelo model) {
        // Disable item change animations to prevent flicker
        if (recyclerView != null && recyclerView.getItemAnimator() != null) {
            recyclerView.getItemAnimator().setChangeDuration(0);
        }

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

    @Override
    public long getItemId(int position) {
        // Return a stable ID for each item
        if (position < getSnapshots().size()) {
            String documentId = getSnapshots().getSnapshot(position).getId();
            return documentId.hashCode();
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        // Called when the whole data set has changed
        // You can add any UI updates here if needed
    }


    public void onError(@NonNull Exception e) {
        super.onError((FirebaseFirestoreException) e);
        // Handle errors here
        e.printStackTrace();
    }

    public class LibrosViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, categoria, edad;
        ImageView imagen;

        public LibrosViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTitulo);
            categoria = itemView.findViewById(R.id.tvCategoria);
            edad = itemView.findViewById(R.id.textViewEdad);
            imagen = itemView.findViewById(R.id.ivPortada);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition(); // Use getBindingAdapterPosition instead
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    try {
                        if (position < getSnapshots().size()) {
                            LibrosCatalogoModelo libro = getSnapshots().getSnapshot(position).toObject(LibrosCatalogoModelo.class);
                            if (libro != null) {
                                listener.onLibroClick(libro);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

}