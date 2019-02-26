package com.aladziviesoft.kegiatandakwahfkl.PemberiInfaqShodaqohZakat.Model;

public class PemberiInfaq_Model {
    String nama,nominal,tanggal;

    public PemberiInfaq_Model(String nama, String nominal, String tanggal) {
        this.nama = nama;
        this.nominal = nominal;
        this.tanggal = tanggal;
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
