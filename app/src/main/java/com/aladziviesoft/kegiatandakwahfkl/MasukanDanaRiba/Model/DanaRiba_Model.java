package com.aladziviesoft.kegiatandakwahfkl.MasukanDanaRiba.Model;

public class DanaRiba_Model {
    String id, nama,nominal,nominaldua, tanggal;

    public DanaRiba_Model() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DanaRiba_Model(String nama, String nominal, String tanggal) {
        this.nama = nama;
        this.nominal = nominal;
        this.tanggal = tanggal;
    }

    public String getNominaldua() {
        return nominaldua;
    }

    public void setNominaldua(String nominaldua) {
        this.nominaldua = nominaldua;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
