package humbang.com.guru.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import humbang.com.guru.R;
import humbang.com.guru.model.ModelLapSibahanpe;

/**
 * Created by dinaskominfokab.pakpakbharat on 27/09/18.
 */

public class LapSibahanpeAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ModelLapSibahanpe> modelLapSibahanpeList;


    Context context;

    String NIP,nama,jabatan;



    public LapSibahanpeAdapter(Activity activity, List<ModelLapSibahanpe> absenList, Context context) {
        this.activity = activity;
        this.modelLapSibahanpeList = absenList;
        this.context = context;
    }



    @Override
    public int getCount() {
        return modelLapSibahanpeList.size();
    }

    @Override
    public Object getItem(int location) {
        return modelLapSibahanpeList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) convertView = inflater.inflate(R.layout.lap_sibahanpe_adapter, null);


        TextView v_tgl = (TextView) convertView.findViewById(R.id.v_tgl);
        TextView v_masuk = (TextView) convertView.findViewById(R.id.v_masuk);
        TextView v_keluar = (TextView) convertView.findViewById(R.id.v_keluar);
        TextView v_potongan = (TextView) convertView.findViewById(R.id.v_potongan);

        final ModelLapSibahanpe modelLapSibahanpe = modelLapSibahanpeList.get(position);

        v_tgl.setText(modelLapSibahanpe.getTgl());
        v_masuk.setText(modelLapSibahanpe.getMasuk());
        v_keluar.setText(modelLapSibahanpe.getPulang());
        v_potongan.setText(modelLapSibahanpe.getPotongan());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return convertView;
    }




}
