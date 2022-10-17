package humbang.com.guru.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import humbang.com.guru.model.ModelUser;


/**
 * Created by dinaskominfokab.pakpakbharat on 14/04/18.
 */

public class DbUser extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_laporan_lokasi_tbl.db";
    private static final String table       = "tbl_user";
    private static final String id_user     = "id_user";
    private static final String nama        = "nama";
    private static final String NIP         = "NIP";
    private static final String jabatan     = "jabatan";
    private static final String password    = "password";
    private static final String fid    = "fid";
    private static final String gambar    = "gambar";
    private static final String id_opd    = "id_opd";



    public DbUser(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE  "+table+" ("+id_user+" INTEGER  PRIMARY KEY AUTOINCREMENT,"+nama+" TEXT,"+NIP+" TEXT,"+jabatan+" TEXT,"+password+" TEXT,"+fid+" TEXT,"+gambar+" TEXT,"+id_opd+" TEXT)";
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

    public  void hapus()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+table);
        onCreate(db);
    }


    public void insert(ModelUser modelUser)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
            values.put(id_user,modelUser.getId_user());
        values.put(nama,modelUser.getNama());
        values.put(NIP,modelUser.getNIP());
        values.put(jabatan,modelUser.getJabatan());
        values.put(password,modelUser.getPassword());
        values.put(fid,modelUser.getFid());
        values.put(gambar,modelUser.getGambar());
        values.put(id_opd,modelUser.getId_opd());
            db.insertOrThrow(table, null, values);
            //db.insert(table,null,values);
            db.close();
    }



    public void update(ModelUser modelUser)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id_user,modelUser.getId_user());
        values.put(nama,modelUser.getNama());
        values.put(NIP,modelUser.getNIP());
        values.put(jabatan,modelUser.getJabatan());
        values.put(password,modelUser.getPassword());
        values.put(fid,modelUser.getFid());
        values.put(gambar,modelUser.getGambar());
        values.put(id_opd,modelUser.getId_opd());
        db.update(table, values,id_user+"=?",new String[]{String.valueOf(modelUser.getId_user())});
        //db.insert(table,null,values);
        db.close();
    }



    public void updateGambar(ModelUser modelUser)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NIP,modelUser.getNIP());
        values.put(gambar,modelUser.getGambar());

        db.update(table, values,NIP+"=?",new String[]{String.valueOf(modelUser.getNIP())});
        //db.insert(table,null,values);
        db.close();
    }





    public ModelUser select_by_id(String id_nya)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(table, new String[]{
                            id_user, nama, NIP,jabatan,password,fid,gambar
                        },id_user+ "=?",new String[]{id_nya},null,null,null,null  );

        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        ModelUser modelUser = new ModelUser(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7)
        );
        return modelUser;
    }






    public ModelUser select_by_terbesar()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(table, new String[]{
                id_user, nama, NIP,jabatan,password,fid,gambar,id_opd
        },null,null,null,null,id_user+" DESC LIMIT 1"  );

        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        ModelUser modelUser = new ModelUser(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7)

                );
        return modelUser;
    }




    public ModelUser select_by_NIP(String NIPnya)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(table, new String[]{
                id_user, nama, NIP,jabatan,password,fid,gambar
        },NIP+ "=?",new String[]{NIPnya},null,null,null,null  );

        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        ModelUser modelUser = new ModelUser(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7)
        );
        return modelUser;
    }


}
