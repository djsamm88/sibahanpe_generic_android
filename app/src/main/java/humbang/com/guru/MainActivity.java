package humbang.com.guru;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import humbang.com.guru.config.Config;
import humbang.com.guru.config.HttpsTrustManager;
import humbang.com.guru.database.DbLokasi;
import humbang.com.guru.database.DbUser;
import humbang.com.guru.model.ModelLokasi;
import humbang.com.guru.model.ModelUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /************************* atribut fused lokasi ***************************/
    private static final String TAG = MainActivity.class.getSimpleName();


    Button btnStartUpdate;

    private String mLastUpdateTime;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 100000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 50000;
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


    String NIP,nama,jabatan,gambar,id_opd;
    int id_user;

    private ProgressDialog pDialog;

    String all_haram;
    List<String> haram = new ArrayList<>();

    List<String> gpsBawaan = new ArrayList<>();


    android.app.AlertDialog.Builder dialog;
    View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ini https kacau
        HttpsTrustManager.allowAllSSL();

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */


        /****** cek paket terinstall ******/
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
        /****** cek paket terinstall ******/

        /*** ambil gps lolos dari db ***/
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest sptRequest = new JsonArrayRequest(Config.StringUrl.gps_lolos,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("gps_lolos:"+response);
                // Parsing json

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

        /*** ambil gps lolos dari db ***/



        /******* ceking fake gps *********/
       // new CekAplikasiHaram().execute("");
        /******* ceking fake gps *********/




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Button btnSpt = (Button)findViewById(R.id.btnAbsensi);
        btnSpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_List = new Intent(MainActivity.this,InterfaceKamera.class);
                startActivity(go_List);
            }
        });


        /**************** cek dulu database apakah sudah pernah login **********/
        DbUser dbUser = new DbUser(getApplicationContext());
        try{
            ModelUser modelUser = dbUser.select_by_terbesar();

            NIP = modelUser.getNIP();
            nama = modelUser.getNama();
            jabatan = modelUser.getJabatan();
            id_user = modelUser.getId_user();
            gambar = modelUser.getGambar();
            id_opd = modelUser.getId_opd();

        }catch (Exception x) {

        }
        /**************** cek dulu database apakah sudah pernah login **********/


        /****** menampilkan maps sekali aja ****/

        try {
            ModelLokasi modelLokasi = new DbLokasi(getApplicationContext()).get_terbesar();

            webviewku(Config.StringUrl.cek_visual+"?lat="+modelLokasi.getLat()+"&lng="+modelLokasi.getLng()+"&nip="+NIP+"&id_opd="+id_opd);
        }catch (Exception e)
        {

        }

        /****** menampilkan maps sekali aja ****/



        View headerView = navigationView.getHeaderView(0);
        TextView namanya = (TextView) headerView.findViewById(R.id.v_nama);
        namanya.setText(nama);

        TextView mNama = (TextView) findViewById(R.id.mNama);
        mNama.setText(nama);

        TextView NIPnya = (TextView) headerView.findViewById(R.id.v_NIP);
        NIPnya.setText(NIP);

        TextView mNIP = (TextView) findViewById(R.id.mNIP);
        mNIP.setText(NIP);

        ImageView imageView = (ImageView) headerView.findViewById(R.id.imageView);
        ImageView logonya = (ImageView) findViewById(R.id.logo_hum);

        try {

            File imgFile = new File(gambar);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            imageView.setImageBitmap(myBitmap);
            logonya.setImageBitmap(myBitmap);

        }catch (Exception e)
        {
            System.out.println(gambar+"-gambar:"+e.toString());
        }





        /************************* atribut fused lokasi ***************************/
        ButterKnife.bind(this);
        // initialize the necessary libraries
        init();
        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);
        startLocationButtonClick();
        /************************* atribut fused lokasi ***************************/






    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            new DbUser(getApplicationContext()).hapus();
            finish();
            return true;
        }


        if (id == R.id.action_versi) {

            DialogForm();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void DialogForm()
    {
        PackageInfo pInfo;
        int verCode;
        String version;
        try {
            pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            verCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version="";
            verCode=0;
        }


        dialog = new android.app.AlertDialog.Builder(MainActivity.this);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setTitle("Versi Aplikasi");
        dialog.setMessage("Versi App : "+String.valueOf(verCode)+" - "+version);
        dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history_absen) {
            // Handle the camera action

            Intent go_absen = new Intent(getApplicationContext(),HistoryAbsenActivity.class);
            startActivity(go_absen);


        } else if (id == R.id.nav_interface) {
            Intent go_cam = new Intent(getApplicationContext(),InterfaceKamera.class);
            startActivity(go_cam);
            System.out.println("Abc");
        }
        /*
        else if (id == R.id.nav_sibahanpe) {
            Intent go_sibahan = new Intent(getApplicationContext(), SibahanpeActivity.class);
            startActivity(go_sibahan);
            System.out.println("Abc");
            //reqUrl("https://sibahanpe.pakpakbharatkab.go.id/sibahanpe/index.php/curl_vote/go");
        }

         */
        else if (id == R.id.nav_lap_sibahanpe) {
            Intent go_lap_sibahan = new Intent(getApplicationContext(),LapSibahanpe.class);
            startActivity(go_lap_sibahan);
            System.out.println("Abc");
        }

        else if (id == R.id.nav_dinas_luar) {
            Intent go_d = new Intent(getApplicationContext(),DinasLuar.class);
            startActivity(go_d);
            System.out.println("Abc");
        }

        else if (id == R.id.nav_cuti_sakit) {
            Intent go_cs = new Intent(getApplicationContext(),CutiSakit.class);
            startActivity(go_cs);
            System.out.println("Abc");
        }

        else if (id == R.id.nav_cuti_lain) {
            Intent go_cl = new Intent(getApplicationContext(),CutiLain.class);
            startActivity(go_cl);
            System.out.println("Abc");
        }
        /*
        else if (id == R.id.nav_ket_sah) {
            Intent go_ks = new Intent(getApplicationContext(),KetSah.class);
            startActivity(go_ks);
            System.out.println("Abc");
        }

         */

        else if (id == R.id.nav_cuti_tahunan) {
            Intent go_ct = new Intent(getApplicationContext(),CutiTahunan.class);
            startActivity(go_ct);
            System.out.println("Abc");
        }






        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    /************************* atribut fused lokasi ***************************/
    private void init() {
        mFusedLocationClient    = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient         = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation    = locationResult.getLastLocation();
                mLastUpdateTime     = DateFormat.getTimeInstance().format(new Date());

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

           // webviewku(Config.StringUrl.cek_visual+"?lat="+mCurrentLocation.getLatitude()+"&lng="+mCurrentLocation.getLongitude()+"&nip="+NIP+"&id_opd="+id_opd);

            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            //ModelLokasi(String id_lokasi,String lat,String lng,String jam,String tanggal)
            new DbLokasi(getApplicationContext()).insert(new ModelLokasi("1",String.valueOf(mCurrentLocation.getLatitude()),String.valueOf(mCurrentLocation.getLongitude()),mLastUpdateTime.toString(),fDate));
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
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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



                        AlertDialog.Builder xx = new AlertDialog.Builder(MainActivity.this);
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


        /****** menampilkan maps sekali aja ****/

        try {
            ModelLokasi modelLokasi = new DbLokasi(getApplicationContext()).get_terbesar();

            webviewku(Config.StringUrl.cek_visual+"?lat="+modelLokasi.getLat()+"&lng="+modelLokasi.getLng()+"&nip="+NIP+"&id_opd="+id_opd);
        }catch (Exception e)
        {

        }

        /****** menampilkan maps sekali aja ****/

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






    private void webviewku(String url)
    {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        //pDialog.show();
        System.out.println(url);
        WebView myWebView = (WebView) findViewById(R.id.myWebView);
        WebSettings webSettings = myWebView.getSettings();

        myWebView.loadUrl(url);
        //myWebView.loadData(content, "text/html; charset=UTF-8", null);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                /***************** ini agar bisa donlod ****************/
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                /***************** ini agar bisa donlod ****************/

                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }catch (Exception e)
                {
                    System.out.println(e.toString());
                }

            }
        });


        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);

        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // callback.invoke(String origin, boolean allow, boolean remember);
                callback.invoke(origin, true, false);

            }
        });
    }



    // Use When the user clicks a link from a web page in your WebView
    private class MyWebViewClient extends WebViewClient {
        /*
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
       */

        @Override

        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //final Uri uri = request.getUrl();


            return false;
        }



        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub

            super.onPageStarted(view, url, favicon);

            super.onPageFinished(view, url);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub

            super.onPageFinished(view, url);
            hidePDialog();
        }


        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            Toast.makeText(getApplicationContext(),"erronya:"+description,Toast.LENGTH_LONG).show();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }



    }



    public void reqUrl(String url)
    {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


            try {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                //http://eotm.pakpakbharatkab.go.id/index.php/curl_sibahanpe/go
                //https://sibahanpe.pakpakbharatkab.go.id/sibahanpe/index.php/curl_vote/go
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);
                        reqUrl("http://eotm.pakpakbharatkab.go.id/index.php/curl_sibahanpe/go");
                        hidePDialog();


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                        hidePDialog();

                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {

                        return null;
                    }

                };

                requestQueue.add(stringRequest);

                /** mengatur time out volley***/
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                /** mengatur time out volley***/


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Nampaknya ada masalah. Pastikan jaringan anda bagus."+e.toString(),Toast.LENGTH_LONG).show();

            }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
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
                if(x.indexOf("fake")!=-1 || x.indexOf("gps")!=-1 || x.indexOf("fakelocation")!=-1 || x.indexOf("spoof")!=-1 || x.indexOf("cleverspg")!=-1 || x.indexOf("mock")!=-1 || x.indexOf("locationchanger")!=-1)
                {
                    //Toast.makeText(getApplicationContext(),"Peringatan. Hapus dulu app "+y,Toast.LENGTH_LONG).show();
                    haram.add(x);
                    /*
                    if(x.equals("com.oppo.factorygps") || x.equals("com.mediatek.ygps") || x.equals("com.lenovo.anyshare.gps") || x.equals("com.gpstrack.mapsnavigation.location"))
                    {
                        haram.remove(x);
                    }
                    */



                }


                if(gpsBawaan.contains(x)){
                    System.out.println("udah_hapus:"+x);
                    haram.remove(x);
                }



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
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
