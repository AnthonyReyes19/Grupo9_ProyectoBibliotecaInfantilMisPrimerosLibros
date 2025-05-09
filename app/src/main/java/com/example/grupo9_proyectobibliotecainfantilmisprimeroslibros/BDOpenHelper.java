package com.example.grupo9_proyectobibliotecainfantilmisprimeroslibros;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class BDOpenHelper extends SQLiteOpenHelper {

    public static final String bdName="proyectobiblioteca.sqlite";
    public static final int bdVersion=2;

    public static final String tablaCuento = "CREATE TABLE cuento(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "titulo TEXT, " +
            "autor TEXT, "+
            "ratingNivel REAL)";

    public BDOpenHelper(Context context){
        super(context, bdName, null, bdVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sq) {
        sq.execSQL(tablaCuento);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

