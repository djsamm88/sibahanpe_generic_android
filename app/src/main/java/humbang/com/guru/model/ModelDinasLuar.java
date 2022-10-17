package humbang.com.guru.model;

/**
 * Created by dinaskominfokab.pakpakbharat on 25/09/18.
 */

public class ModelDinasLuar {


    private String id,tanggal,keterangan,NIK,status,FID;


    public ModelDinasLuar(){

    }

    public ModelDinasLuar(String id, String tanggal, String keterangan, String NIK, String status, String FID){
        this.id=id;
        this.tanggal=tanggal;
        this.keterangan=keterangan;
        this.NIK=NIK;
        this.status=status;
        this.FID=FID;

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getFID() {
        return FID;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getNIK() {
        return NIK;
    }

    public void setFID(String FID) {
        this.FID = FID;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setNIK(String NIK) {
        this.NIK = NIK;
    }


}
