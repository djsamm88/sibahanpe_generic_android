package humbang.com.guru.model;

/**
 * Created by dinaskominfokab.pakpakbharat on 27/09/18.
 */

public class ModelSptAbsen {
    private String id_spt_absensi,status;

    public ModelSptAbsen(){

    }

    public ModelSptAbsen(String id_spt_absensi,String status){
        this.id_spt_absensi=id_spt_absensi;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId_spt_absensi() {
        return id_spt_absensi;
    }

    public void setId_spt_absensi(String id_spt_absensi) {
        this.id_spt_absensi = id_spt_absensi;
    }
}
