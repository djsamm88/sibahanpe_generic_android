package humbang.com.guru;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.StrictMode;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import humbang.com.guru.config.Config;
import humbang.com.guru.config.HttpsTrustManager;
import humbang.com.guru.database.DbUser;
import humbang.com.guru.model.ModelUser;

/**
 * Created by dinaskominfokab.pakpakbharat on 27/09/18.
 */

public class SibahanpeActivity extends Activity {


    private ProgressDialog pDialog;

    String NIP,nama,jabatan,password;
    int id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        //ini https kacau
        HttpsTrustManager.allowAllSSL();


        /**************** cek dulu database apakah sudah pernah login **********/
        DbUser dbUser = new DbUser(getApplicationContext());
        try{
            ModelUser modelUser = dbUser.select_by_terbesar();

            NIP = modelUser.getNIP();
            nama = modelUser.getNama();
            jabatan = modelUser.getJabatan();
            id_user = modelUser.getId_user();
            password = modelUser.getPassword();

        }catch (Exception x) {
            Intent i = new Intent(getApplicationContext(),Login.class);
            startActivity(i);
            finish();
        }
        /**************** cek dulu database apakah sudah pernah login **********/


        webviewku(Config.StringUrl.login_sibahanpe+"?NIK="+NIP+"&password="+password);

    }



    private void webviewku(String url)
    {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

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
    }



    // Use When the user clicks a link from a web page in your WebView
    private class MyWebViewClient extends WebViewClient {

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

            /*
            Toast.makeText(MainActivity.this,
                    "The Requested Page Does Not Exist", Toast.LENGTH_LONG).show();
                    */
            super.onReceivedError(view, errorCode, description, failingUrl);
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
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

}
