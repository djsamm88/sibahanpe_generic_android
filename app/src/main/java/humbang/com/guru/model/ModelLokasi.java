package humbang.com.guru.model;

/**
 * Created by dinaskominfokab.pakpakbharat on 27/09/18.
 */

public class ModelLokasi {
    String lat,lng,id_lokasi,jam,tanggal;
    public ModelLokasi()
    {

    }

    public ModelLokasi(String id_lokasi,String lat,String lng,String jam,String tanggal)
    {
        this.id_lokasi = id_lokasi;
        this.lat = lat;
        this.lng = lng;
        this.jam = jam;
        this.tanggal = tanggal;

    }

    public String getTanggal() {
        return tanggal;
    }

    public String getId_lokasi() {
        return id_lokasi;
    }

    public String getJam() {
        return jam;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public void setId_lokasi(String id_lokasi) {
        this.id_lokasi = id_lokasi;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
