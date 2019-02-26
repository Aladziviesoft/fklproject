package com.aladziviesoft.kegiatandakwahfkl.Menabung.Model;

public class Menabung_model {
    String id_masjid, namamasjid, tanggal, status;

    public Menabung_model(String namamasjid, String tanggal) {
        this.namamasjid = namamasjid;
        this.tanggal = tanggal;

    }

    public Menabung_model() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId_masjid() {
        return id_masjid;
    }

    public void setId_masjid(String id_masjid) {
        this.id_masjid = id_masjid;
    }

    public String getNamamasjid() {
        return namamasjid;
    }

    public void setNamamasjid(String namamasjid) {
        this.namamasjid = namamasjid;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
