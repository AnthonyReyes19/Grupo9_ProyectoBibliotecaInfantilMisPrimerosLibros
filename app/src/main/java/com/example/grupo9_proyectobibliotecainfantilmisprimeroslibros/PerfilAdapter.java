package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.PerfilViewHolder> {

    private List<PerfilItem> perfiles;
    private Context context;
    private String userId;

    public PerfilAdapter(List<PerfilItem> perfiles, Context context) {
        this.perfiles = perfiles;
        this.context = context;
        // Obtener el userId una sola vez
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @NonNull
    @Override
    public PerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfil, parent, false);
        return new PerfilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfilViewHolder holder, int position) {
        PerfilItem item = perfiles.get(position);
        RegistroPerfilInfantil perfil = item.perfil;

        int avatarResId = obtenerResourceAvatar(perfil.getAvatar());
        holder.imgBtnAvatar.setImageResource(avatarResId);
        holder.imgBtnAvatar.setContentDescription("Avatar de " + perfil.getNombre());

        holder.txtNombre.setText(perfil.getNombre());
        holder.txtEdad.setText("Edad: " + perfil.getEdad() + " años");

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarPerfilInfantilActivity.class);
            // Pasar tanto usuarioId como perfilId
            intent.putExtra("usuarioId", userId);
            intent.putExtra("perfilId", item.key);
            context.startActivity(intent);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            // Mostrar diálogo de confirmación antes de eliminar
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Eliminar Perfil")
                    .setMessage("¿Estás seguro de eliminar el perfil de " + perfil.getNombre() + "?")
                    .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                        eliminarPerfil(item.key, position);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void eliminarPerfil(String perfilId, int position) {
        if (userId == null) {
            Toast.makeText(context, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference("perfiles")
                .child(userId)
                .child(perfilId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Remover de la lista local
                    perfiles.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, perfiles.size());

                    // Verificar si era el perfil seleccionado actualmente
                    android.content.SharedPreferences sharedPreferences =
                            context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    String perfilSeleccionadoId = sharedPreferences.getString("perfil_infantil_id", null);

                    if (perfilId.equals(perfilSeleccionadoId)) {
                        // Si se eliminó el perfil seleccionado, limpiar SharedPreferences
                        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("perfil_infantil_id");
                        editor.remove("perfil_infantil_nombre");
                        editor.remove("perfil_infantil_edad");
                        editor.remove("perfil_infantil_avatar");
                        editor.apply();
                    }

                    Toast.makeText(context, "Perfil eliminado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private int obtenerResourceAvatar(String avatarNombre) {
        if (avatarNombre == null) return R.drawable.avatar_nino1;

        switch(avatarNombre) {
            case "avatar1": return R.drawable.avatar_nino1;
            case "avatar2": return R.drawable.avatar_nino2;
            case "avatar3": return R.drawable.avatar_nino3;
            default: return R.drawable.avatar_nino1;
        }
    }

    @Override
    public int getItemCount() {
        return perfiles != null ? perfiles.size() : 0;
    }

    public static class PerfilViewHolder extends RecyclerView.ViewHolder {
        ImageButton imgBtnAvatar;
        TextView txtNombre, txtEdad;
        Button btnEditar, btnEliminar;

        public PerfilViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBtnAvatar = itemView.findViewById(R.id.imgBtnAvatar);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtEdad = itemView.findViewById(R.id.txtEdad);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    public void actualizarLista(List<PerfilItem> nuevaLista) {
        perfiles = nuevaLista;
        notifyDataSetChanged();
    }
}