package com.aladziviesoft.kegiatandakwahfkl.PemasukanKegiatanSosial.Model;

public class ModelPemasukanKegiatanSosial {
    String Id, Nama, JumlahUang, nominal, Tanggal, Status, IdKegiatan;

    public ModelPemasukanKegiatanSosial() {

    }

    public String getId() {
        return Id;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getJumlahUang() {
        return JumlahUang;
    }

    public void setJumlahUang(String jumlahUang) {
        JumlahUang = jumlahUang;
    }

    public String getTanggal() {
        return Tanggal;
    }

    public void setTanggal(String tanggal) {
        Tanggal = tanggal;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getIdKegiatan() {
        return IdKegiatan;
    }

    public void setIdKegiatan(String idKegiatan) {
        IdKegiatan = idKegiatan;
    }
}
