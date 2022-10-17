package humbang.com.guru;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import humbang.com.guru.adapter.LapSibahanpeAdapter;
import humbang.com.guru.config.Config;
import humbang.com.guru.config.HttpsTrustManager;
import humbang.com.guru.database.DbUser;
import humbang.com.guru.model.ModelLapSibahanpe;
import humbang.com.guru.model.ModelUser;

/**
 * Created by dinaskominfokab.pakpakbharat on 27/09/18.
 */

public class LapSibahanpe extends AppCompatActivity {

    private ProgressDialog pDialog;
    private ListView listView;
    private LapSibahanpeAdapter lapSibahanpeAdapter;
    private List<ModelLapSibahanpe> lapSibahanpeList = new ArrayList<ModelLapSibahanpe>();
    String NIP,nama,jabatan;
    int id_user;
    Spinner spinBulan,spinTahun;
    ArrayAdapter<String> arrayAdapterBulan,arrayAdapterTahun;
    String bulan,tahun;
    TextView ket,vPokok,vEkin,vAbsensi,vTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lap_sibahanpe);

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

        }catch (Exception x) {

        }
        /**************** cek dulu database apakah sudah pernah login **********/

        ket = (TextView)findViewById(R.id.ket);
        vPokok = (TextView)findViewById(R.id.vPokok);
        vEkin = (TextView)findViewById(R.id.vEkin);
        vAbsensi = (TextView) findViewById(R.id.vAbsensi);
        vTotal = (TextView) findViewById(R.id.vTotal);



        /*********Panggil adapter***********************/
        listView = (ListView) findViewById(R.id.list);
        lapSibahanpeAdapter = new LapSibahanpeAdapter(this, lapSibahanpeList,getApplicationContext());
        listView.setAdapter(lapSibahanpeAdapter);

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        tahun = String.valueOf(year);

        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        bulan = sdf.format(new Date());
        listHitoryAbsen(NIP,bulan,tahun);
        /*********Panggil adapter***********************/

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
                listHitoryAbsen(NIP,getBulan,getTahun);
            }
        });





    }



    private void listHitoryAbsen(String NIP,String bulan,String tahun)
    {
        ket.setText("Laporan bulan "+bulan+" tahun "+tahun);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        lapSibahanpeList.clear();


        // Creating volley request obj
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest sptRequest = new JsonArrayRequest(Config.StringUrl.lap_sibahanpe+"?nik="+NIP+"&tahun="+tahun+"&bulan="+bulan,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //Log.d("Hasil", response.toString());
                hidePDialog();


                try {
                    JSONObject obj = response.getJSONObject(0);
                    System.out.println("pokok:"+obj.getString("pokok"));
                    System.out.println("ekinerja:"+obj.getString("ekinerja"));

                    int total = Integer.parseInt(obj.getString("total"));
                    int ekin  = Integer.parseInt(obj.getString("ekinerja"));
                    int absen = total-ekin;
                    String hasil_absen = String.valueOf(absen);

                    Locale localeID = new Locale("in", "ID");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

                    vPokok.setText(formatRupiah.format(Double.parseDouble(obj.getString("pokok"))));
                    vEkin.setText(formatRupiah.format(Double.parseDouble(obj.getString("ekinerja"))));
                    vAbsensi.setText(formatRupiah.format(Double.parseDouble(hasil_absen)));
                    vTotal.setText(formatRupiah.format(Double.parseDouble(obj.getString("total"))));

                    try {

                        JSONArray array = obj.getJSONArray("kehadiran");
                        System.out.println("kehadiran-" + array.toString());


                        for (int i = 0; i < array.length(); i++) {
                            JSONObject kehadiran = array.getJSONObject(i);
                            //System.out.println(kehadiran.getString("tgl"));
                            //System.out.println(kehadiran.getString("masuk"));

                            ModelLapSibahanpe modelLapSibahanpe = new ModelLapSibahanpe();
                            modelLapSibahanpe.setNik(kehadiran.getString("nik"));
                            modelLapSibahanpe.setTgl(kehadiran.getString("tgl"));
                            modelLapSibahanpe.setMasuk(kehadiran.getString("masuk"));
                            modelLapSibahanpe.setPulang(kehadiran.getString("pulang"));
                            modelLapSibahanpe.setTelat_masuk(kehadiran.getString("telat_masuk"));
                            modelLapSibahanpe.setTotal_telat(kehadiran.getString("total_telat"));
                            modelLapSibahanpe.setPotongan(kehadiran.getString("potongan"));

                            lapSibahanpeList.add(modelLapSibahanpe);
                            lapSibahanpeAdapter.notifyDataSetChanged();

                        }

                    }catch (Exception x)
                    {
                        System.out.println(x);
                    }


                }catch (Exception e)
                {

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
        lapSibahanpeAdapter.notifyDataSetChanged();

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
