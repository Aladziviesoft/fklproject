package com.aladziviesoft.kegiatandakwahfkl.Dashboard.Model;

public class DashModel {
    int gambar;
    String Nama;

    public DashModel(int gambar, String nama) {
        this.gambar = gambar;
        Nama = nama;
    }

    public int getGambar() {
        return gambar;
    }

    public void setGambar(int gambar) {
        this.gambar = gambar;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }
}
