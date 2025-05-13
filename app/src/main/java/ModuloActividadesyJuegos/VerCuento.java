package ModuloActividadesyJuegos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros.R;

import java.util.ArrayList;
import java.util.List;

public class VerCuento extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BDOpenHelper dbHelper;
    private List<Cuento> listaCuentos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_cuento);

        recyclerView = findViewById(R.id.recyclerViewCuentos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new BDOpenHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cuento", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String titulo = cursor.getString(1);
                String autor = cursor.getString(2);
                float rating = cursor.getFloat(3);

                listaCuentos.add(new Cuento(id, titulo, autor, rating));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        CuentoAdapter adapter = new CuentoAdapter(listaCuentos, this);
        recyclerView.setAdapter(adapter);
    }
}
