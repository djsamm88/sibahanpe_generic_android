package humbang.com.guru.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import humbang.com.guru.R;
import humbang.com.guru.database.DbUser;
import humbang.com.guru.model.ModelDinasLuar;
import humbang.com.guru.model.ModelUser;

/**
 * Created by dinaskominfokab.pakpakbharat on 27/09/18.
 */

public class DinasLuarAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ModelDinasLuar> modelDinasLuarList;


    Context context;

    String NIP,nama,jabatan;

    private String id,tanggal,keterangan,NIK,status,FID;



    public DinasLuarAdapter(Activity activity, List<ModelDinasLuar> modelDinasLuarList, Context context) {
        this.activity = activity;
        this.modelDinasLuarList = modelDinasLuarList;
        this.context = context;
    }



    @Override
    public int getCount() {
        return modelDinasLuarList.size();
    }

    @Override
    public Object getItem(int location) {
        return modelDinasLuarList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) convertView = inflater.inflate(R.layout.dinas_luar_adapter, null);


        TextView v_tanggal = (TextView) convertView.findViewById(R.id.v_tanggal);
        TextView v_status = (TextView) convertView.findViewById(R.id.v_status);
        TextView v_keterangan = (TextView) convertView.findViewById(R.id.v_keterangan);

        final ModelDinasLuar modelDinasLuar = modelDinasLuarList.get(position);


        if(modelDinasLuar.getStatus().equals("approve"))
        {
            v_status.setTextColor(Color.parseColor("#FF17810B"));
        }else if(modelDinasLuar.getStatus().equals("pending"))
        {
            v_status.setTextColor(Color.parseColor("#d16328"));

        }else{

            v_status.setTextColor(Color.parseColor("#FFAC0D07"));

        }
        v_tanggal.setText(modelDinasLuar.getTanggal());
        v_status.setText(modelDinasLuar.getStatus());
        v_keterangan.setText(modelDinasLuar.getKeterangan());




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
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
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
