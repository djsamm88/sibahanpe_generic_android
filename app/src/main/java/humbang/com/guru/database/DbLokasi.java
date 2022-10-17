package humbang.com.guru.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import humbang.com.guru.model.ModelLokasi;

/**
 * Created by dinaskominfokab.pakpakbharat on 27/09/18.
 */

public class DbLokasi extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_lokasi.db";
    private static final String table       = "tbl_lokasi";

    private static final String id_lokasi   = "id_lokasi";
    private static final String jam         = "jam";
    private static final String tanggal     = "tanggal";
    private static final String lat         = "lat";
    private static final String lng         = "lng";



    public DbLokasi(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE  "+table+" ("+id_lokasi+" INTEGER PRIMARY KEY,"+jam+" TEXT,"+tanggal+" TEXT,"+lat+" TEXT,"+lng+" TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS "+table);

        // Create tables again
        onCreate(db);
    }


    public void insert(ModelLokasi modelLokasi)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put(id_lokasi,modelLokasi.getId_lokasi());
        values.put(jam,modelLokasi.getJam());
        values.put(tanggal,modelLokasi.getTanggal());
        values.put(lat,modelLokasi.getLat());
        values.put(lng,modelLokasi.getLng());

        db.insertOrThrow(table, null, values);
        //db.insert(table,null,values);
        db.close();
    }



    public void update(ModelLokasi modelLokasi)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(id_lokasi,modelLokasi.getId_lokasi());
        values.put(jam,modelLokasi.getJam());
        values.put(tanggal,modelLokasi.getTanggal());
        values.put(lat,modelLokasi.getLat());
        values.put(lng,modelLokasi.getLng());

        db.update(table, values,id_lokasi+"=?",new String[]{modelLokasi.getId_lokasi()});
        //db.insert(table,null,values);
        db.close();
    }


    public ModelLokasi get_terbesar()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(table, new String[]{
                id_lokasi, lat,lng,jam, tanggal
        },null,null,null,null,id_lokasi+" DESC LIMIT 1"  );

        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        ModelLokasi modelLokasi = new ModelLokasi(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4)
        );
        return modelLokasi;
    }





    // Getting All
    public List<ModelLokasi> getAll() {
        List<ModelLokasi> semuanya = new ArrayList<ModelLokasi>();

        String selectQuery = "SELECT  * FROM " + table + " ORDER BY "+id_lokasi+" DESC";


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ModelLokasi modelLokasi = new ModelLokasi();

                modelLokasi.setId_lokasi(cursor.getString(0));
                modelLokasi.setJam(cursor.getString(1));
                modelLokasi.setTanggal(cursor.getString(2));
                modelLokasi.setLat(cursor.getString(3));
                modelLokasi.setLng(cursor.getString(4));


                semuanya.add(modelLokasi);
            } while (cursor.moveToNext());
        }

        return semuanya;
    }




}
