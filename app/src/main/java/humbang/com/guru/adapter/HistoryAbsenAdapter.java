package humbang.com.guru.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import humbang.com.guru.R;
import humbang.com.guru.config.Config;
import humbang.com.guru.database.DbUser;
import humbang.com.guru.model.ModelHistoryAbsen;
import humbang.com.guru.model.ModelUser;

/**
 * Created by dinaskominfokab.pakpakbharat on 27/09/18.
 */

public class HistoryAbsenAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ModelHistoryAbsen> absenList;


    Context context;

    String NIP,nama,jabatan;



    public HistoryAbsenAdapter(Activity activity, List<ModelHistoryAbsen> absenList, Context context) {
        this.activity = activity;
        this.absenList = absenList;
        this.context = context;
    }



    @Override
    public int getCount() {
        return absenList.size();
    }

    @Override
    public Object getItem(int location) {
        return absenList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) convertView = inflater.inflate(R.layout.history_kehadiran_adapter, null);


        TextView v_nip = (TextView) convertView.findViewById(R.id.v_nip);
        TextView v_waktu = (TextView) convertView.findViewById(R.id.v_waktu);
        TextView v_aksi = (TextView) convertView.findViewById(R.id.v_aksi);
        TextView v_aksi1 = (TextView) convertView.findViewById(R.id.v_aksi1);


        final ModelHistoryAbsen modelHistoryAbsen = absenList.get(position);

        v_nip.setText(modelHistoryAbsen.getId());
        v_waktu.setText(modelHistoryAbsen.getWaktu());

        /**************** cek dulu database apakah sudah pernah login **********/
        DbUser dbUser = new DbUser(context);
        try{
            ModelUser modelUser = dbUser.select_by_terbesar();

            NIP = modelUser.getNIP();
            nama = modelUser.getNama();
            jabatan = modelUser.getJabatan();


        }catch (Exception x) {

        }
        /**************** cek dulu database apakah sudah pernah login **********/


        v_aksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogWebview(Config.StringUrl.view_history_by_id+"?id="+modelHistoryAbsen.getId(),"Maps Lokasi Absensi");

                System.out.println(Config.StringUrl.view_history_by_id+"?id="+modelHistoryAbsen.getId());
            }
        });

        v_aksi1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.StringUrl.view_history_by_id+"?id="+modelHistoryAbsen.getId()));
                activity.startActivity(browserIntent);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return convertView;
    }



    private void DialogWebview(final String webUrl,final String judul)
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(judul);

        WebView wv = new WebView(context);
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

                return true;
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request,
                                                  WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Do something
                System.out.println("error_webview"+error.toString());
                Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
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
                    activity.startActivity(i);
                }catch (Exception e)
                {
                    System.out.println(e.toString());
                }

            }
        });
        WebSettings webSettings = wv.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);


        alert.setView(wv);
        alert.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();

        //alert.getWindow().setLayout(600, 400); //Controlling width and height.


    }


}
