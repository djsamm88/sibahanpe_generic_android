package humbang.com.guru;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import humbang.com.guru.adapter.HistoryAbsenAdapter;
import humbang.com.guru.config.Config;
import humbang.com.guru.config.HttpsTrustManager;
import humbang.com.guru.database.DbUser;
import humbang.com.guru.model.ModelHistoryAbsen;
import humbang.com.guru.model.ModelUser;

/**
 * Created by dinaskominfokab.pakpakbharat on 27/09/18.
 */

public class HistoryAbsenActivity extends AppCompatActivity {


    private ProgressDialog pDialog;

    private ListView listView;


    private HistoryAbsenAdapter historyAbsenAdapter;

    private List<ModelHistoryAbsen> historyAbsenList = new ArrayList<ModelHistoryAbsen>();

    String NIP,nama,jabatan,fid;
    int id_user;

    SwipeRefreshLayout pullToRefresh;

    String bulan,tahun;
    Spinner spinBulan,spinTahun;
    ArrayAdapter<String> arrayAdapterBulan,arrayAdapterTahun;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_absen);

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
            fid = modelUser.getFid();
        }catch (Exception x) {

        }
        /**************** cek dulu database apakah sudah pernah login **********/





        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        tahun = String.valueOf(year);

        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        bulan = sdf.format(new Date());


        /**spinner bulan**/
        spinBulan = (Spinner)findViewById(R.id.spinnerBulan);
        List<String> bulans = new ArrayList<String>();
        for(int i=1;i<13;i++)
        {
            bulans.add(String.valueOf(i));
        }
        arrayAdapterBulan= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bulans);
        arrayAdapterBulan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinBulan.setAdapter(arrayAdapterBulan);

        int posisiBulan = arrayAdapterBulan.getPosition(String.valueOf(Integer.parseInt(bulan)));
        spinBulan.setSelection(posisiBulan);

        /**spinner bulan**/

        /**spinner tahun**/
        spinTahun = (Spinner)findViewById(R.id.spinnerTahun);
        List<String> tahuns = new ArrayList<String>();
        String tahun_lalu = String.valueOf(year-1);

        tahuns.add(tahun);
        tahuns.add(tahun_lalu);
        arrayAdapterTahun = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tahuns);
        arrayAdapterTahun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTahun.setAdapter(arrayAdapterTahun);
        /**spinner tahun**/

        Button btnView = (Button) findViewById(R.id.btnShow);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String getBulan = spinBulan.getSelectedItem().toString();
                String getTahun = spinTahun.getSelectedItem().toString();
                listHitoryAbsen(fid,getBulan,getTahun);
            }
        });



        /*********Panggil adapter***********************/
        listView = (ListView) findViewById(R.id.list);
        historyAbsenAdapter = new HistoryAbsenAdapter(this, historyAbsenList,getApplicationContext());
        listView.setAdapter(historyAbsenAdapter);
        listHitoryAbsen(fid,bulan,tahun);
        /*********Panggil adapter***********************/

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String getBulan = spinBulan.getSelectedItem().toString();
                String getTahun = spinTahun.getSelectedItem().toString();
                listHitoryAbsen(fid,getBulan,getTahun);
                pullToRefresh.setRefreshing(false);
            }
        });




    }



    private void listHitoryAbsen(String fid,String bulan,String tahun)
    {


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        historyAbsenList.clear();

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest sptRequest = new JsonArrayRequest(Config.StringUrl.all_history_by_nip+"?fid="+fid+"&tahun="+tahun+"&bulan="+bulan,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Hasil", response.toString());
                hidePDialog();

                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject obj = response.getJSONObject(i);

                        ModelHistoryAbsen modelHistoryAbsen = new ModelHistoryAbsen();

                        modelHistoryAbsen.setId(obj.getString("Id"));
                        modelHistoryAbsen.setLat(obj.getString("lat"));
                        modelHistoryAbsen.setLng(obj.getString("lng"));
                        modelHistoryAbsen.setImage(obj.getString("image"));
                        modelHistoryAbsen.setWaktu(obj.getString("waktu"));
                        modelHistoryAbsen.setFid(obj.getString("Fid"));



                        historyAbsenList.add(modelHistoryAbsen);
                        historyAbsenAdapter.notifyDataSetChanged();
                        System.out.println("jumlah_data:"+historyAbsenList.size());






                    } catch (JSONException e) {
                        e.printStackTrace();
                        hidePDialog();
                    }

                }



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


                hidePDialog();

                Toast.makeText(getApplicationContext(),"Offline Mode. No inet.."+error.toString(),Toast.LENGTH_SHORT).show();
                System.out.println(error.toString());



            }
        });

        queue.add(sptRequest);
        historyAbsenAdapter.notifyDataSetChanged();

        /** mengatur time out volley***/

        sptRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        /** mengatur time out volley***/


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
