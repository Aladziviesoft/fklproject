package com.aladziviesoft.kegiatandakwahfkl.Rab.Model;

public class RabModel {
    public RabModel() {
    }

    public RabModel(String namaRab, String jumlahUang) {
        NamaRab = namaRab;
        JumlahUang = jumlahUang;
    }

    String Id_rad, NamaRab,JumlahUang;

    public String getId_rad() {
        return Id_rad;
    }

    public void setId_rad(String id_rad) {
        Id_rad = id_rad;
    }

    public String getNamaRab() {
        return NamaRab;
    }

    public void setNamaRab(String namaRab) {
        NamaRab = namaRab;
    }

    public String getJumlahUang() {
        return JumlahUang;
    }

    public void setJumlahUang(String jumlahUang) {
        JumlahUang = jumlahUang;
    }
}
