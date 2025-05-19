package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.ModuloActividadesyJuegos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

import java.util.List;

public class CuentoAdapter extends RecyclerView.Adapter<CuentoAdapter.CuentoViewHolder> {

    private List<Cuento> lista;
    private Context context;

    public CuentoAdapter(List<Cuento> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public CuentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.activity_item_cuento, parent, false);
        return new CuentoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull CuentoViewHolder holder, int position) {
        Cuento cuento = lista.get(position);
        holder.txtTitulo.setText(cuento.getTitulo());

        holder.btnVer.setOnClickListener(v -> {
            String titulo = cuento.getTitulo();

            Intent intent;

            switch (titulo) {
                case "Caperucita Roja":
                    intent = new Intent(context, verCuentoUnoPaginauno.class);
                    break;
                //case "La Bella durmiente":
                //    intent = new Intent(context, verCuentoUnoPaginados.class);
                //    break;
                default:
                    Toast.makeText(context, "Cuento no disponible", Toast.LENGTH_SHORT).show();
                    return;
            }

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class CuentoViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo;
        Button btnVer;

        public CuentoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            btnVer = itemView.findViewById(R.id.btnVer);
        }
    }
}

