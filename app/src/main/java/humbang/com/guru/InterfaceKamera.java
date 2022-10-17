package humbang.com.guru;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import humbang.com.guru.config.Config;
import humbang.com.guru.config.HttpsTrustManager;
import humbang.com.guru.database.DbUser;
import humbang.com.guru.model.ModelUser;


/**
 * Created by dinaskominfokab.pakpakbharat on 01/05/18.
 */

public class InterfaceKamera extends AppCompatActivity
{


    /************************* atribut fused lokasi ***************************/
    private static final String TAG = InterfaceKamera.class.getSimpleName();
    Button btnStartUpdates,btnCekLokasi;
    private String mLastUpdateTime;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    /************************* atribut fused lokasi ***************************/



    Button btnUpload;
    ImageView mImageView1;
    EditText mInputPhotonya1,mLat,mLng;


    EditText mNip;
    TextView mJudulSpt;


    String mCurrentPhotoPath,id_kategori;
    private static final int STORAGE_PERMISSION_CODE = 123;

    final int REQUEST_IMAGE_CAPTURE = 10;
    final int REQUEST_IMAGE_GALERY = 20;


    String NIP;
    String nama;
    String jabatan;
    String id_pegawai;
    String fid;
    String id_opd;

    private ProgressDialog pDialog;
    private android.app.AlertDialog.Builder alertWebview;


    String all_haram;
    List<String> haram = new ArrayList<>();
    List<String> gpsBawaan = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interface_kamera);
        requestStoragePermission();

        //ini https kacau
        HttpsTrustManager.allowAllSSL();


        btnUpload = (Button) findViewById(R.id.btnUpload);


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //uploadMultipart();
                checkLokasi();
                //uploadBase64();
            }
        });


        /******* cek aplikasi haram terbaru ****/


        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);



        for (ApplicationInfo packageInfo : packages) {
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));

            String y;
            try {
                y = packageInfo.loadLabel(pm).toString();
            }catch (Exception e)
            {
                y ="";
            }

            String x = packageInfo.packageName.toString();
            if(x.indexOf("fake")!=-1 || x.indexOf("fakegps")!=-1 || x.indexOf("gpsfake")!=-1  || x.indexOf("fakelocation")!=-1 || x.indexOf("spoof")!=-1 || x.indexOf("cleverspg")!=-1 || x.indexOf("mock")!=-1 || x.indexOf("locationchanger")!=-1)
            {
                //Toast.makeText(getApplicationContext(),"Peringatan. Hapus dulu app "+y,Toast.LENGTH_LONG).show();
                haram.add(x);

                if(x.equals("com.oppo.factorygps") || x.equals("com.mediatek.ygps") || x.equals("com.lenovo.anyshare.gps") || x.equals("com.gpstrack.mapsnavigation.location") || x.equals("org.codeaurora.gps.gpslogsave"))
                {
                    haram.remove(x);
                }



                if(gpsBawaan.contains(x)){
                    haram.remove(x);
                }

            }

                /*
                if(y.indexOf("fake")!=-1 || y.indexOf("gps")!=-1 || y.indexOf("fakelocation")!=-1 || y.indexOf("spoof")!=-1 || y.indexOf("GPS")!=-1 || y.indexOf("Gps")!=-1 || y.indexOf("Fake")!=-1 || y.indexOf("FAKE")!=-1)
                {
                    //Toast.makeText(getApplicationContext(),"Peringatan. Hapus dulu app "+y,Toast.LENGTH_LONG).show();
                    haram.add(y);
                    if(y.equals("YGPS"))
                    {
                        haram.remove(y);
                    }
                }
                 */

        }




        all_haram="";

        for(String h : haram)
        {
            all_haram+=h+"\n";
        }

        TextView warning_fake = (TextView)findViewById(R.id.warning_fake);
        warning_fake.setText("Peringatan !!! HP anda terdeteksi fake GPS. Silahkan hapus dulu untuk melanjutkan!!! \n"+all_haram);
        if(haram.size()>0)
        {
            warning_fake.setVisibility(View.VISIBLE);

        }else{
            btnUpload.setVisibility(View.VISIBLE);
        }

        /************************************************/


        /****** cek paket terinstall ******/
        /*
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));

            String x = packageInfo.packageName.toString();
            if(x.indexOf("fake")!=-1 || x.indexOf("gps")!=-1 || x.indexOf("fakelocation")!=-1 || x.indexOf("spoof")!=-1 || x.indexOf("cleverspg")!=-1 || x.indexOf("mock")!=-1 || x.indexOf("locationchanger")!=-1)
            {
                //Toast.makeText(getApplicationContext(),"Peringatan. Hapus dulu app "+x,Toast.LENGTH_LONG).show();
                haram.add(x);
            }

        }
         */
        /****** cek paket terinstall ******/

        /*** ambil gps lolos dari db ***/
        /*
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest sptRequest = new JsonArrayRequest(Config.StringUrl.gps_lolos,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("gps_lolos:"+response);
                // Parsing json
                btnUpload.setVisibility(View.VISIBLE);




                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject obj = response.getJSONObject(i);
                        System.out.println("paketnya:"+obj.getString("paket"));
                        gpsBawaan.add(obj.getString("paket"));

                        if(haram.contains(obj.getString("paket")))
                        {
                            haram.remove(obj.getString("paket"));
                        }

                    }catch (Exception e){

                    }

                    System.out.println("list_bawaan_sebelum:"+haram);
                    if(i+1==response.length()){
                        System.out.println("list_bawaan:"+haram);
                        all_haram="";
                        for(String h : haram)
                        {
                            all_haram+=h+"\n";
                        }

                        TextView warning_fake = (TextView)findViewById(R.id.warning_fake);
                        warning_fake.setText("Peringatan !!! HP anda terdeteksi fake GPS. Silahkan hapus dulu untuk melanjutkan!!! \n"+all_haram);
                        if(haram.size()>0)
                        {
                            warning_fake.setVisibility(View.VISIBLE);
                            btnUpload.setVisibility(View.INVISIBLE);

                        }else{
                            btnUpload.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(sptRequest);
        */
        /*** ambil gps lolos dari db ***/




        /****** cek fake gps *******/
        //new CekAplikasiHaram().execute("");
        /****** cek fake gps *******/

        /**************** cek dulu database apakah sudah pernah login **********/
        DbUser dbUser = new DbUser(getApplicationContext());
        ModelUser modelUser = dbUser.select_by_terbesar();
        NIP = modelUser.getNIP();
        nama = modelUser.getNama();
        jabatan = modelUser.getJabatan();
        id_pegawai = String.valueOf(modelUser.getId_user());
        fid = modelUser.getFid();
        id_opd = modelUser.getId_opd();
        /**************** cek dulu database apakah sudah pernah login **********/


        /******** membuat back button *******/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        /******** membuat back button *******/

        mLat = (EditText) findViewById(R.id.lat);
        mLng = (EditText) findViewById(R.id.lng);

        mLng.setEnabled(false);
        mLat.setEnabled(false);


        mImageView1 = (ImageView) findViewById(R.id.photonya1);
        mInputPhotonya1 = (EditText)findViewById(R.id.inputPhotonya1);

        mNip = (EditText) findViewById(R.id.nip);
        mJudulSpt = (TextView) findViewById(R.id.judul_spt);

        mNip.setEnabled(false);


        mImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startDialog(1);
                ambilCamera(1);
                Log.d("gambar","gambar di kick");
            }
        });




        btnCekLokasi = (Button)findViewById(R.id.btnCekLokasi);
        btnCekLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lat = mLat.getText().toString();
                String lng = mLng.getText().toString();

                DialogWebview(Config.StringUrl.cek_visual+"?lat="+lat+"&lng="+lng+"&nip="+NIP+"&id_opd="+id_opd,"Lokasi Anda");

                //DialogWebview("https://pakpakbharatkab.go.id","apa");
            }
        });


        /*******************checking the permission***********************************/
        //if the permission is not given we will open setting to add permission
        //else app will not open
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;
        }
        */
        /*******************checking the permission***********************************/



        /************************* atribut fused lokasi ***************************/
        ButterKnife.bind(this);
        // initialize the necessary libraries
        init();
        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);
        startLocationButtonClick();
        /************************* atribut fused lokasi ***************************/




        try{
            mNip.setText(NIP);
        }catch (Exception e)
        {
            System.out.println(NIP +"-kosong");
        }





    }


    private void startDialog(final int CODE_GAMBAR)
    {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(InterfaceKamera.this);
        myAlertDialog.setTitle("Pilihan Gambar");
        myAlertDialog.setMessage("Ambil gambar darimana?");
        myAlertDialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ambilCamera(CODE_GAMBAR);
            }
        });

        myAlertDialog.setNegativeButton("Galery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ambilGalery(CODE_GAMBAR);
            }
        });

        myAlertDialog.show();


    }



    private void ambilCamera(final int xxx)
    {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
                galleryAddPic();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this,
                        ex.toString()+"Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "humbang.com.guru.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE+xxx);
            }
        }

    }



    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void galleryAddPic()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private void ambilGalery(final int yyy)
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALERY+yyy);
    }


    public String getPath(Uri uri)
    {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        //imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == (REQUEST_IMAGE_CAPTURE+1))
        {
            Log.d("activityResult","req kamera");
            if (resultCode == Activity.RESULT_OK)
            {

                try {


                    File imgFile = new File(mCurrentPhotoPath);

                    if (imgFile.exists()) {

                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                        /** mengecilkan Bitmap **/
                        Bitmap converetdImage = getResizedBitmap(myBitmap, 200);
                        /** mengecilkan Bitmap **/

                        /** membuat watermark **/
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                        Bitmap mark = mark(converetdImage, timeStamp);
                        /** membuat watermark **/

                        /** menyimpan Bitmap jadi file **/
                        File file = new File(imgFile.getAbsolutePath());
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                        mark.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.close();
                        /** menyimpan Bitmap jadi file **/

                        mImageView1.setImageBitmap(mark);


                        //mInputPhotonya1.setText(mCurrentPhotoPath);

                        /** image to base64 ***/
                        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos); //bm is the bitmap object
                        byte[] byteArrayImage = baos.toByteArray();
                        /** image to base64 ***/

                        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                        mInputPhotonya1.setText(encodedImage);

                        /** menjadikan foto profil otomatis terakhir**/
                        DbUser dbUser = new DbUser(getApplicationContext());
                        ModelUser modelUser = new ModelUser();
                        modelUser.setGambar(mCurrentPhotoPath);
                        modelUser.setNIP(NIP);
                        dbUser.updateGambar(modelUser);
                        System.out.println(mCurrentPhotoPath+"-gambar");
                        /** menjadikan foto profil otomatis terakhir**/

                    } else {
                        Log.d("xxx", "tidaak ada");
                    }

                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Ada kesalahan. Ulangi lagi.",Toast.LENGTH_LONG).show();



                }




            }

        }




        /************** jika dari galery *****************/
        if (requestCode == (REQUEST_IMAGE_GALERY+1))
        {

            if (resultCode == Activity.RESULT_OK)
            {
                Uri selectedImage = data.getData();

                String filePath = getPath(selectedImage);

                mInputPhotonya1.setText(filePath);

                mImageView1.setImageURI(null);//mengosongkan dulu
                mImageView1.setImageURI(selectedImage);

            }
        }


        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");



                        AlertDialog.Builder xx = new AlertDialog.Builder(InterfaceKamera.this);
                        xx.setTitle("Perhatian");
                        xx.setMessage("Anda harus menghidupkan GPS?");
                        xx.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //finish();
                                startLocationButtonClick();
                            }
                        });

                        xx.show();




                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }



    }






    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void checkLokasi()
    {

        //?lat=2.554583014155121&lng=98.32454245505323&nip=000&id_opd=10
        String lat = mLat.getText().toString();
        String lng = mLng.getText().toString();
        String urlCek = Config.StringUrl.ceking+"?lat="+lat+"&lng="+lng+"&nip="+NIP+"&id_opd="+id_opd;
        System.out.println(urlCek);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest sptRequest = new JsonArrayRequest(urlCek,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Hasil", response.toString());
                for (int i = 0; i < response.length(); i++) {


                    try{
                        JSONObject obj = response.getJSONObject(i);
                        System.out.println(obj.getString("msg"));
                        String msg = obj.getString("msg");
                        String deskripsi = obj.getString("deskripsi");
                        if(msg.equals("sukses"))
                        {
                            //disini nanti upload
                            uploadBase64();
                        }else{
                            //jika tidak dilokasi

                            String lat = mLat.getText().toString();
                            String lng = mLng.getText().toString();
                            DialogWebview(Config.StringUrl.cek_visual+"?lat="+lat+"&lng="+lng+"&nip="+NIP+"&id_opd="+id_opd,"Lokasi Anda");
                            //https://ujicoba.pakpakbharatkab.go.id/eabsensi/cek_lokasi/visual/?lat=2.554583014155121&lng=98.32454245505323&nip=000&id_opd=10
                            //uploadBase64();

                        }

                        Toast.makeText(getApplicationContext(),deskripsi,Toast.LENGTH_LONG).show();
                    }catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Ada masalah:"+e.toString(),Toast.LENGTH_LONG).show();
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("HasilError", error.toString());
                Toast.makeText(getApplicationContext(),"Ada masalah:"+error.toString(),Toast.LENGTH_LONG).show();

            }
        });

        queue.add(sptRequest);

    }

    public void uploadBase64()
    {
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        String filePath1 = mInputPhotonya1.getText().toString().trim();
        String imageUri=null;
        String lat,lng,id_spt,keterangan_tugas;
        lat = mLat.getText().toString().trim();
        lng = mLng.getText().toString().trim();
        NIP = mNip.getText().toString().trim();



        boolean lanjut = true;

        if(lat.equals(""))
        {
            Toast.makeText(this,"Tunggu koordinat didapatkan. Hidupkan GPS.",Toast.LENGTH_LONG).show();
            lanjut = false;
        }


        if(lng.equals(""))
        {
            Toast.makeText(this,"Tunggu koordinat didapatkan. Hidupkan GPS.",Toast.LENGTH_LONG).show();
            lanjut = false;
        }


        if(NIP.equals(""))
        {
            Toast.makeText(this,"Sesi Login anda habis. Login kembali.",Toast.LENGTH_LONG).show();
            lanjut = false;
        }



        if(filePath1.equals(""))
        {
            Toast.makeText(this,"Peringatan.Gambar harus ada.",Toast.LENGTH_LONG).show();
            lanjut = false;
        }

        if(!lanjut)
        {
            hidePDialog();
        }

        if(lanjut)
        {

            try {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String URL = Config.StringUrl.tampung_base54;
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("NIP", NIP);
                jsonBody.put("lat", lat);
                jsonBody.put("lng", lng);
                jsonBody.put("base64", filePath1);
                jsonBody.put("fid", fid);
                jsonBody.put("nama", nama);
                final String requestBody = jsonBody.toString();

                System.out.println(requestBody);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);
                        hidePDialog();
                        Toast.makeText(getApplicationContext(),"Absensi berhasil di upload.",Toast.LENGTH_LONG).show();
                        notifnya("Berhasil","Berhasil upload absensi.");



                        finish();
                        Intent i = new Intent(getApplicationContext(),HistoryAbsenActivity.class);
                        startActivity(i);


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                        hidePDialog();
                        Toast.makeText(getApplicationContext(),"Nampaknya ada masalah. Pastikan jaringan anda bagus.",Toast.LENGTH_LONG).show();
                        notifnya("Gagal","Nampaknya ada masalah. Pastikan jaringan anda bagus.");
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                };

                requestQueue.add(stringRequest);

                /** mengatur time out volley***/
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                /** mengatur time out volley***/


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Nampaknya ada masalah. Pastikan jaringan anda bagus."+e.toString(),Toast.LENGTH_LONG).show();
                notifnya("Gagal","Nampaknya ada masalah. Pastikan jaringan anda bagus.");
            }

        }



    }


    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void uploadMultipart()
    {
        //getting name for the image
        String filePath1 = mInputPhotonya1.getText().toString().trim();


        String imageUri=null;

        String lat,lng,id_spt,keterangan_tugas;
        lat = mLat.getText().toString().trim();
        lng = mLng.getText().toString().trim();
        NIP = mNip.getText().toString().trim();


        boolean lanjut = true;

        if(lat.equals(""))
        {
            Toast.makeText(this,"Tunggu koordinat didapatkan. Hidupkan GPS.",Toast.LENGTH_LONG).show();
            lanjut = false;
        }


        if(lng.equals(""))
        {
            Toast.makeText(this,"Tunggu koordinat didapatkan. Hidupkan GPS.",Toast.LENGTH_LONG).show();
            lanjut = false;
        }


        if(NIP.equals(""))
        {
            Toast.makeText(this,"Sesi Login anda habis. Login kembali.",Toast.LENGTH_LONG).show();
            lanjut = false;
        }





        if(lanjut)
        {

        }
        //Uploading code

        try {
            String uploadId = UUID.randomUUID().toString();

            UploadNotificationConfig uploadNotificationConfig = new UploadNotificationConfig();
            uploadNotificationConfig.setTitle(getString(R.string.app_name));
            uploadNotificationConfig.setCompletedMessage("Absensi anda sukses diupload..");
            uploadNotificationConfig.setInProgressMessage("Mohon tunggu, lagi upload absensi...");
            uploadNotificationConfig.setErrorMessage("Nampaknya ada masalah.. periksa jaringan anda.");


            //Creating a multi part request
            //new MultipartUploadRequest(this, uploadId, "http://ujicoba.pakpakbharatkab.go.id/upload/upload.php")
            new MultipartUploadRequest(this, uploadId, Config.StringUrl.tampung_base54)
                    .addFileToUpload(filePath1, "image1") //Adding file
                    .addParameter("NIP", NIP) //Adding text parameter to the request
                    .addParameter("lat", lat) //Adding text parameter to the request
                    .addParameter("lng", lng) //Adding text parameter to the request
                    .setNotificationConfig(uploadNotificationConfig)
                    .setMaxRetries(5)
                    .startUpload(); //Starting the upload


            Toast.makeText(this, "Absensi anda lagi kami upload. Akan segera kami kabari anda.", Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception exc) {
            Toast.makeText(this, "Kesalahan:. "+exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");


            AlertDialog.Builder xx = new AlertDialog.Builder(InterfaceKamera.this);
            xx.setTitle("Perhatian");
            xx.setMessage("Data belum di upload. Yakin meninggalkan?");
            xx.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            xx.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            xx.show();


            return false;

        }
        return super.onKeyDown(keyCode, event);
    }






    /************************* atribut fused lokasi ***************************/
    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState)
    {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }


    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            //txtLocationResult.setText("Lat: " + mCurrentLocation.getLatitude() + ", " + "Lng: " + mCurrentLocation.getLongitude());
            Log.d(TAG,"lat:"+mCurrentLocation.getLatitude());
            Log.d(TAG,"lng:"+mCurrentLocation.getLongitude());
            Log.d(TAG,"Last updated on: " + mLastUpdateTime);

            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            mLat.setText(lat);
            mLng.setText(lng);
        }

        //toggleButtons();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }


    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(InterfaceKamera.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(InterfaceKamera.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    //@OnClick(R.id.btn_start_location_updates)
    public void startLocationButtonClick()
    {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    public void stopLocationButtonClick()
    {
        mRequestingLocationUpdates = false;
        stopLocationUpdates();
    }

    public void stopLocationUpdates()
    {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        //toggleButtons();
                    }
                });
    }


    public void showLastKnownLocation()
    {
        if (mCurrentLocation != null) {
            Toast.makeText(getApplicationContext(), "Lat: " + mCurrentLocation.getLatitude()
                    + ", Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();
        }
    }


    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateLocationUI();
    }



    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates)
        {
            // pausing location updates
            stopLocationUpdates();
        }
    }

    /************************* atribut fused lokasi ***************************/









    private void DialogWebview(final String webUrl,final String judul)
    {

        alertWebview = new android.app.AlertDialog.Builder(this);
        alertWebview.setTitle(judul);

        WebView wv = new WebView(getApplicationContext());
        wv.loadUrl(webUrl);
        wv.setWebViewClient(new WebViewClient() {
            /*
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

             */

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return false;
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request,
                                                  WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Do something
                System.out.println("error_webview"+error.toString());
                Toast.makeText(getApplicationContext(),"erronya:"+error.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // When an error occurs, ignore and go on
            }
        });
        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // callback.invoke(String origin, boolean allow, boolean remember);
                callback.invoke(origin, true, false);

            }
        });
        wv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                // ini agar bisa donlod
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                // ini agar bisa donlod

                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }catch (Exception e)
                {
                    System.out.println("webview Masalah!!"+e.toString());
                }

            }
        });
        WebSettings webSettings = wv.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);

        alertWebview.setView(wv);
        alertWebview.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alertWebview.show();

        //alert.getWindow().setLayout(600, 400); //Controlling width and height.


    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    //calling the method:
    //Bitmap converetdImage = getResizedBitmap(photo, 500);


    public static Bitmap mark(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(12);
        paint.setAntiAlias(true);

        canvas.drawText(watermark, 20, 25, paint);

        return result;
    }

    public void notifnya(String title,String konten)
    {

        /****************  notif **********/
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(uri)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(konten)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        Notification notif = mBuilder.build();


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1",
                    "upload_absen",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }


        mNotificationManager.notify(1, notif);
        /**************** notif **********/
    }

    private class CekAplikasiHaram extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            final PackageManager pm = getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);



            for (ApplicationInfo packageInfo : packages) {
                Log.d(TAG, "Installed package :" + packageInfo.packageName);
                Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
                Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));

                String y;
                try {
                    y = packageInfo.loadLabel(pm).toString();
                }catch (Exception e)
                {
                    y ="";
                }

                String x = packageInfo.packageName.toString();
                if(x.indexOf("fake")!=-1 || x.indexOf("fakegps")!=-1 || x.indexOf("gpsfake")!=-1  || x.indexOf("fakelocation")!=-1 || x.indexOf("spoof")!=-1 || x.indexOf("cleverspg")!=-1 || x.indexOf("mock")!=-1 || x.indexOf("locationchanger")!=-1)
                {
                    //Toast.makeText(getApplicationContext(),"Peringatan. Hapus dulu app "+y,Toast.LENGTH_LONG).show();
                    haram.add(x);

                    if(x.equals("com.oppo.factorygps") || x.equals("com.mediatek.ygps") || x.equals("com.lenovo.anyshare.gps") || x.equals("com.gpstrack.mapsnavigation.location") || x.equals("org.codeaurora.gps.gpslogsave"))
                    {
                        haram.remove(x);
                    }



                    if(gpsBawaan.contains(x)){
                        haram.remove(x);
                    }

                }

                /*
                if(y.indexOf("fake")!=-1 || y.indexOf("gps")!=-1 || y.indexOf("fakelocation")!=-1 || y.indexOf("spoof")!=-1 || y.indexOf("GPS")!=-1 || y.indexOf("Gps")!=-1 || y.indexOf("Fake")!=-1 || y.indexOf("FAKE")!=-1)
                {
                    //Toast.makeText(getApplicationContext(),"Peringatan. Hapus dulu app "+y,Toast.LENGTH_LONG).show();
                    haram.add(y);
                    if(y.equals("YGPS"))
                    {
                        haram.remove(y);
                    }
                }
                 */

            }




            all_haram="";

            for(String h : haram)
            {
                all_haram+=h+"\n";
            }



            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            System.out.println("Siap di eksekusi");
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            TextView warning_fake = (TextView)findViewById(R.id.warning_fake);
            warning_fake.setText("Peringatan !!! HP anda terdeteksi fake GPS. Silahkan hapus dulu untuk melanjutkan!!! \n"+all_haram);
            if(haram.size()>0)
            {
                warning_fake.setVisibility(View.VISIBLE);

            }else{
                btnUpload.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
