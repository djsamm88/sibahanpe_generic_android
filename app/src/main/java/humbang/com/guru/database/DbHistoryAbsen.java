package humbang.com.guru.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import humbang.com.guru.model.ModelHistoryAbsen;


/**
 * Created by dinaskominfokab.pakpakbharat on 14/04/18.
 */

public class DbHistoryAbsen extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "db_history_absen.db";

    private static final String table = "tbl_history_absen";



    private static final String id = "id";
    private static final String NIP = "NIP";
    private static final String lat = "lat";
    private static final String lng = "lng";
    private static final String image = "image";
    private static final String waktu = "waktu";
    private static final String fid  = "fid";
    private static final String nama   = "nama";




    public DbHistoryAbsen(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_CONTACTS_TABLE = "CREATE TABLE  "+table+" ("+id+" INTEGER PRIMARY KEY,"+NIP+" TEXT,"+lat+" TEXT,"+lng+" TEXT,"+image+" TEXT,"+waktu+" TEXT,"+fid+" TEXT,"+nama+" TEXT)";
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


    public void insert(ModelHistoryAbsen modelHistoryAbsen)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();



            values.put(id,modelHistoryAbsen.getId());
            values.put(NIP,modelHistoryAbsen.getNIP());
            values.put(lat,modelHistoryAbsen.getLat());
            values.put(lng,modelHistoryAbsen.getLng());
            values.put(image,modelHistoryAbsen.getImage());
            values.put(waktu,modelHistoryAbsen.getWaktu());
            values.put(fid,modelHistoryAbsen.getFid());
            values.put(nama,modelHistoryAbsen.getNama());

            db.insertOrThrow(table, null, values);
            //db.insert(table,null,values);
            db.close();
    }



    public void update(ModelHistoryAbsen modelHistoryAbsen)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();



        values.put(id,modelHistoryAbsen.getId());
        values.put(NIP,modelHistoryAbsen.getNIP());
        values.put(lat,modelHistoryAbsen.getLat());
        values.put(lng,modelHistoryAbsen.getLng());
        values.put(image,modelHistoryAbsen.getImage());
        values.put(waktu,modelHistoryAbsen.getWaktu());
        values.put(fid,modelHistoryAbsen.getFid());
        values.put(nama,modelHistoryAbsen.getNama());

        db.update(table, values, id+"=?",new String[]{modelHistoryAbsen.getId()});
        //db.insert(table,null,values);
        db.close();
    }



    public List<ModelHistoryAbsen> getAll() {
        List<ModelHistoryAbsen> semuanya = new ArrayList<ModelHistoryAbsen>();

        String selectQuery = "SELECT  * FROM " + table + " ORDER BY "+id+" DESC";


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                ModelHistoryAbsen modelHistoryAbsen = new ModelHistoryAbsen();

                modelHistoryAbsen.setId(cursor.getString(0));
                modelHistoryAbsen.setNIP(cursor.getString(1));
                modelHistoryAbsen.setLat(cursor.getString(2));
                modelHistoryAbsen.setLng(cursor.getString(3));
                modelHistoryAbsen.setImage(cursor.getString(4));
                modelHistoryAbsen.setWaktu(cursor.getString(5));
                modelHistoryAbsen.setFid(cursor.getString(6));
                modelHistoryAbsen.setNama(cursor.getString(7));




                semuanya.add(modelHistoryAbsen);
            } while (cursor.moveToNext());
        }

        return semuanya;
    }




}
