package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.Modulo5.Administracion.Modelo.Usuario;
import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> listaUsuarios;
    private UsuarioClickListener listener;

    public interface UsuarioClickListener {
        void onEditarClick(Usuario usuario);
        void onEliminarClick(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> listaUsuarios, UsuarioClickListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);

        holder.txtNombreCompleto.setText(usuario.getNombres() + " " + usuario.getApellidos());
        holder.txtRol.setText(usuario.getRol());
        holder.txtEmail.setText(usuario.getEmail());

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditarClick(usuario);
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminarClick(usuario);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreCompleto, txtRol, txtEmail;
        ImageButton btnEditar, btnEliminar;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreCompleto = itemView.findViewById(R.id.txtNombreCompleto);
            txtRol = itemView.findViewById(R.id.txtRol);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
