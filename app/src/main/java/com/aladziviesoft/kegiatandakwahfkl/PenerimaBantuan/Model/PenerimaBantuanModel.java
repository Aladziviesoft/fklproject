package com.aladziviesoft.kegiatandakwahfkl.PenerimaBantuan.Model;

public class PenerimaBantuanModel {
    String Id,Nama,Nominal,IdSumberDana,Tanggal;

    public PenerimaBantuanModel() {
    }


    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getNominal() {
        return Nominal;
    }

    public void setNominal(String nominal) {
        Nominal = nominal;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getIdSumberDana() {
        return IdSumberDana;
    }

    public void setIdSumberDana(String idSumberDana) {
        IdSumberDana = idSumberDana;
    }

    public String getTanggal() {
        return Tanggal;
    }

    public void setTanggal(String tanggal) {
        Tanggal = tanggal;
    }
}
