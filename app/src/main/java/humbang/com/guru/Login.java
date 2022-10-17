package humbang.com.guru;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import humbang.com.guru.config.Config;
import humbang.com.guru.config.HttpsTrustManager;
import humbang.com.guru.database.DbUser;
import humbang.com.guru.model.ModelUser;

/**
 * Created by dinaskominfokab.pakpakbharat on 27/09/18.
 */

public class Login extends Activity {

    EditText mNIP,mPassword;
    Button mBtnGo;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mNIP = (EditText) findViewById(R.id.NIP);
        mPassword = (EditText) findViewById(R.id.password);
        mBtnGo = (Button) findViewById(R.id.btnGo);



        mBtnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String NIP = mNIP.getText().toString();
                String password = mPassword.getText().toString();

                //Log.d("Login.javaa","login:"+NIP+"&"+password);
                if(NIP.equals("") || password.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"NIP dan Password harus diisi",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("Login.java","login:"+NIP+"&"+password);

                //ini https kacau
                HttpsTrustManager.allowAllSSL();
                panggilJson(NIP,password);
            }
        });


        /**************** cek dulu database apakah sudah pernah login **********/
        DbUser dbUser = new DbUser(getApplicationContext());
        try{
            ModelUser modelUser = dbUser.select_by_terbesar();

            Log.d("dariDb","hasil:"+modelUser.getNIP()+","+modelUser.getPassword());

            mNIP.setText(modelUser.getNIP());
            mPassword.setText(modelUser.getPassword());

            Intent i = new Intent(Login.this,MainActivity.class);
            i.putExtra("NIP",modelUser.getNIP());
            i.putExtra("nama",modelUser.getNama());
            i.putExtra("jabatan",modelUser.getJabatan());
            i.putExtra("id_user",String.valueOf(modelUser.getId_user()));
            startActivity(i);
            finish();

        }catch (Exception x) {

        }
        /**************** cek dulu database apakah sudah pernah login **********/

    }

    private void panggilJson(String NIP, String password)
    {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        final String pass = password;

        // prepare the Request
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Config.StringUrl.cek_login+"?NIP="+NIP+"&password="+password;
        //String url = "https://sibahanpe.pakpakbharatkab.go.id/api/login_android.php?NIP=197909072010011016&password=197909072010011016";

        System.out.println("login :"+url);

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                // display response
                System.out.println("jumlah:"+response.length());
                hidePDialog();

                    /*
                    try {
                        JSONObject hasil = response.getJSONObject(0);

                        final int jumlah = Integer.parseInt(hasil.getString("count"));
                        if(jumlah>0)
                        {
                            System.out.println(hasil.getString("count"));
                        }else{
                            System.out.println("Hasil <= 0");
                        }
                    }catch (Exception x)
                    {

                    }
                    */


                        try {

                            JSONObject obj = response.getJSONObject(0);


                                final int jumlah = Integer.parseInt(obj.getString("count"));
                                if(jumlah>0)
                                {
                                    ModelUser modelUser = new ModelUser();
                                    //modelUser.setId_user(Integer.parseInt(obj.getString("FID")));
                                    modelUser.setNama(obj.getString("NAMA"));
                                    modelUser.setJabatan(obj.getString("JABATAN"));
                                    modelUser.setNIP(obj.getString("NIP"));
                                    modelUser.setPassword(obj.getString("COSTUM_2"));
                                    modelUser.setFid(obj.getString("FID"));
                                    modelUser.setId_opd(obj.getString("ID_OPD"));

                                    if(pass.equals(obj.getString("COSTUM_2")))
                                    {
                                        try {
                                            new DbUser(getApplicationContext()).insert(modelUser);
                                        }catch (Exception x)
                                        {
                                            try {
                                                new DbUser(getApplicationContext()).update(modelUser);
                                            }catch (Exception o)
                                            {
                                                System.out.println(o);
                                            }
                                            System.out.println(x);
                                        }


                                        /*********** direk ke beranda ***********/
                                        Intent intent = new Intent(Login.this,MainActivity.class);
                                        intent.putExtra("NIP",modelUser.getNIP());
                                        intent.putExtra("nama",modelUser.getNama());
                                        intent.putExtra("jabatan",modelUser.getJabatan());
                                        //intent.putExtra("id_user",String.valueOf(modelUser.getId_user()));
                                        intent.putExtra("fid",modelUser.getFid());

                                        startActivity(intent);
                                        finish();
                                        /**********************************************/
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Password salah.",Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    Toast.makeText(getApplicationContext(),"NIP salah.",Toast.LENGTH_SHORT).show();
                                }




                            }catch (Exception e)
                            {
                                Log.d("error_simpan","aaa-"+e.toString());

                            }



            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response",error.toString());

                        hidePDialog();

                        Toast.makeText(getApplicationContext(),"Gagal login. Periksa jaringan internet anda. "+error.toString(),Toast.LENGTH_SHORT).show();
                        System.out.println("gagal_login: "+error.toString());

                    }
                }
        );

        // add it to the RequestQueue
        queue.add(jsonObjReq);

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



