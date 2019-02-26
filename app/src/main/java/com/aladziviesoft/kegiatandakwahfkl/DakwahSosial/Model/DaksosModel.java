package com.aladziviesoft.kegiatandakwahfkl.DakwahSosial.Model;

public class DaksosModel {
    String NamaDaksos;
    int gambar;

    public DaksosModel(String namaDaksos, int gambar) {
        NamaDaksos = namaDaksos;
        this.gambar = gambar;
    }

    public String getNamaDaksos() {
        return NamaDaksos;
    }

    public void setNamaDaksos(String namaDaksos) {
        NamaDaksos = namaDaksos;
    }

    public int getGambar() {
        return gambar;
    }

    public void setGambar(int gambar) {
        this.gambar = gambar;
    }
}
