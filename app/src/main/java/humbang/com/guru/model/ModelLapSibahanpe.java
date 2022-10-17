package humbang.com.guru.model;

/**
 * Created by dinaskominfokab.pakpakbharat on 14/04/18.
 */

public class ModelLapSibahanpe {


    private String nik,tgl,masuk,pulang,telat_masuk,cepat_pulang,total_telat,potongan;

    public ModelLapSibahanpe()
    {

    }


    public ModelLapSibahanpe(String nik, String tgl, String masuk, String pulang, String telat_masuk, String cepat_pulang, String total_telat, String potongan)
    {
        this.nik=nik;
        this.tgl=tgl;
        this.masuk=masuk;
        this.pulang=pulang;
        this.telat_masuk=telat_masuk;
        this.cepat_pulang=cepat_pulang;
        this.total_telat=total_telat;
        this.potongan=potongan;
    }

    public void setMasuk(String masuk) {
        this.masuk = masuk;
    }

    public String getMasuk() {
        return masuk;
    }

    public String getCepat_pulang() {
        return cepat_pulang;
    }

    public String getNik() {
        return nik;
    }

    public String getPotongan() {
        return potongan;
    }

    public String getPulang() {
        return pulang;
    }

    public String getTelat_masuk() {
        return telat_masuk;
    }

    public String getTgl() {
        return tgl;
    }

    public String getTotal_telat() {
        return total_telat;
    }

    public void setCepat_pulang(String cepat_pulang) {
        this.cepat_pulang = cepat_pulang;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public void setPotongan(String potongan) {
        this.potongan = potongan;
    }

    public void setPulang(String pulang) {
        this.pulang = pulang;
    }

    public void setTelat_masuk(String telat_masuk) {
        this.telat_masuk = telat_masuk;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public void setTotal_telat(String total_telat) {
        this.total_telat = total_telat;
    }
}
