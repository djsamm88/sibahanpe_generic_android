package humbang.com.guru.model;

/**
 * Created by dinaskominfokab.pakpakbharat on 14/04/18.
 */

public class ModelHistoryAbsen {


    private String id,NIP,lat,lng,image,waktu,fid,nama;

    public ModelHistoryAbsen()
    {

    }


    public ModelHistoryAbsen(String id,String NIP,String lat,String lng,String image,String waktu,String fid,String nama)
    {
        this.id=id;
        this.NIP=NIP;
        this.lat=lat;
        this.lng=lng;
        this.image=image;
        this.waktu=waktu;
        this.fid=fid;
        this.nama=nama;
    }


    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFid() {
        return fid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    public String getNIP() {
        return NIP;
    }

    public void setNIP(String NIP) {
        this.NIP = NIP;
    }

}
