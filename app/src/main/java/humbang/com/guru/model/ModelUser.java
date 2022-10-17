package humbang.com.guru.model;

/**
 * Created by dinaskominfokab.pakpakbharat on 25/09/18.
 */

public class ModelUser {

    private int id_user;
    private String nama,NIP,jabatan,password,fid,gambar,id_opd;


    public ModelUser(){

    }

    public ModelUser(int id_user,String nama,String NIP,String jabatan,String password,String fid,String gambar,String id_opd){
        this.id_user=id_user;
        this.nama=nama;
        this.jabatan=jabatan;
        this.password=password;
        this.NIP=NIP;
        this.fid=fid;
        this.gambar=gambar;
        this.id_opd=id_opd;
    }

    public String getId_opd() {
        return id_opd;
    }

    public void setId_opd(String id_opd) {
        this.id_opd = id_opd;
    }

    public void setId_user(int id_user){
        this.id_user=id_user;
    }

    public void setNama(String nama){
        this.nama=nama;
    }

    public void setNIP(String NIP){
        this.NIP = NIP;
    }

    public void setJabatan(String jabatan){
        this.jabatan = jabatan;
    }

    public void setPassword(String password){
        this.password = password;
    }


    public int getId_user()
    {
        return id_user;
    }

    public String getNama(){
        return nama;
    }

    public String getNIP(){
        return NIP;
    }

    public String getJabatan(){
        return jabatan;
    }

    public String getPassword(){
        return password;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
