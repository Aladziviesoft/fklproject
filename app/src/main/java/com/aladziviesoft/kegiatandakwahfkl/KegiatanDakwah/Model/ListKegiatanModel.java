package com.aladziviesoft.kegiatandakwahfkl.KegiatanDakwah.Model;

public class ListKegiatanModel {
    String IdKegiatan, NamaKegiatan, JumlahUangKegiatan;

    public ListKegiatanModel() {
    }

    public ListKegiatanModel(String idKegiatan, String namaKegiatan, String jumlahUangKegiatan) {
        this.IdKegiatan = idKegiatan;
        this.NamaKegiatan = namaKegiatan;
        this.JumlahUangKegiatan = jumlahUangKegiatan;
    }

    public String getIdKegiatan() {
        return IdKegiatan;
    }

    public void setIdKegiatan(String idkegiatan) {
        IdKegiatan = idkegiatan;
    }

    public String getNamaKegiatan() {
        return NamaKegiatan;
    }

    public void setNamaKegiatan(String namakegiatan) {
        NamaKegiatan = namakegiatan;
    }

    public String getJumlahUangKegiatan() {
        return JumlahUangKegiatan;
    }

    public void setJumlahUangKegiatan(String jumlahUangkegiatan) {
        JumlahUangKegiatan = jumlahUangkegiatan;
    }
}
